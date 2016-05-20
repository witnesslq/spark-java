package com.kz.face.api.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.api.constant.Constant;
import com.kz.face.api.pojo.result.BatchSearchBean;
import com.kz.face.api.pojo.result.Comparebean;
import com.kz.face.api.pojo.result.DetectBean;
import com.kz.face.api.pojo.result.GroupImageBean;
import com.kz.face.api.pojo.result.GroupsBean;
import com.kz.face.api.pojo.result.ImageBean;
import com.kz.face.api.pojo.result.MoveBean;
import com.kz.face.api.pojo.result.OkayBean;
import com.kz.face.api.pojo.result.SearchBean;
import com.kz.face.api.pojo.result.StoreBean;
import com.kz.face.api.pojo.result.VerifyBean;
import com.kz.face.api.pojo.result.common.Attrs;
import com.kz.face.api.pojo.result.common.Face;
import com.kz.face.api.pojo.result.common.Groups;
import com.kz.face.api.pojo.result.common.Photos;
import com.kz.face.api.pojo.result.common.Rect;
import com.kz.face.api.pojo.result.common.Source;
import com.kz.face.api.service.HbaseService;
import com.kz.face.api.service.KafkaProducerService;
import com.kz.face.api.service.impl.HbaseServiceImpl;
import com.kz.face.api.service.impl.KafkaProducerServiceImpl;
import com.kz.face.api.utils.HTableClientUtils;
import com.kz.face.api.utils.PropertiesUtil;
import com.kz.face.pojo.AddFaceParams;
import com.kz.face.pojo.CompareParams;
import com.kz.face.pojo.DelFaceParams;
import com.kz.face.pojo.DetectParams;
import com.kz.face.pojo.Event;
import com.kz.face.pojo.EventType;
import com.kz.face.pojo.SearchParams;
import com.kz.face.pojo.VerifyParams;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * face rest api
 * @author liuxing
 *
 */
@Path("/")
public class FaceResource {
  private static final Log logger = LogFactory.getLog(FaceResource.class);
  //服务器内部错误                                  
  private static String INTERNAL_SERVER_ERROR="{\"error\":\"Internal Server Error\"}";
  //timeout
  private static String TIMEOUT= "{\"error\":\"request timeout\"}";
  //kafka发送消息
  private KafkaProducerService kafkaProducerService = new KafkaProducerServiceImpl();
  // hbase数据操作
  private HbaseService hbaseService = new HbaseServiceImpl();
  
  /**
   * 新增底库
   * 
   * @param group Required人脸库名称，每个库的名称都是唯一的
   * @param syncing 同步
   * @param slave 主/从数据库
   * @param version 版本号
   * @return
   */
  @POST
  @Path("/g/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response createGroup(
      @PathParam("group") String group,
      @FormParam("syncing") @DefaultValue("true") boolean syncing,
      @FormParam("slave") @DefaultValue("false") boolean slave,
      @FormParam("version") @DefaultValue("1") int version) {
    logger.info(new Date()+"  add group group:"+group+"  description:"+syncing);
      try {
        // 1.获取hbase预分区个数
        int splitNum = Integer.parseInt(PropertiesUtil.getValue(Constant.HBASESPLITNUM));
        byte[][] splitkeys = new byte[splitNum-1][];
        for (int i = 1; i < splitNum; i++) {
          splitkeys[i - 1] = Bytes.toBytes(String.format("%3d", i).replaceAll(" ", "0"));
        }

        // 2.获取hbase底库列族
        String cf = PropertiesUtil.getValue(Constant.HBASECFS);
        String[] cfs = cf.split(",");
        
        // 3.如果表不存在，建立预分区数据库表
        if(!HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
          HTableClientUtils.createTable(Constant.FACE_+group.trim(),cfs,splitkeys);
          
          // 4.建立完底库之后，需要将底库信息放到元数据表中
          String mateTable = PropertiesUtil.getValue(Constant.HBASEMATETABLE);
          String mateTableCf = PropertiesUtil.getValue(Constant.HBASEMATETABLECF);
          String rowkey = new Date().getTime()+UUID.randomUUID().toString().replace("-", "").substring(0, 6);
          hbaseService.writeRow(
              mateTable, rowkey, mateTableCf,
              new String[]{
                  "id",
                  "name",
                  "syncing",
                  "slave",
                  "version"
              },
              new String[]{
                  UUID.randomUUID().toString().replaceAll("-", ""),
                  group.trim(),
                  syncing+"",
                  slave+"",
                  version+""
              }
              );
          
          // 5.返回结果设置
          OkayBean bean = new OkayBean();
          return Response.status(Response.Status.OK).entity(bean).build();
        }else{
          return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":"+group+" exist\"}").build();
        }
      } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
  }
 
  /**
   * 查询现有库列表
   * 
   * @return
   */
  @GET
  @Path("/g")
  @Produces(MediaType.APPLICATION_JSON)
  public Response listGroups() {
    logger.info(new Date()+"  list all group");
    try {
      String mateTable = PropertiesUtil.getValue(Constant.HBASEMATETABLE);
      String mateTableCf = PropertiesUtil.getValue(Constant.HBASEMATETABLECF);
      
      // 1.得到元数据表下的所有数据,将数据包装成对象
      List<Object> groups = hbaseService.packRow2Result(mateTable,
          hbaseService.selectAllRow(mateTable), mateTableCf);
      // 2.封装 返回
      GroupsBean bean = new GroupsBean();
      bean.setGroups(groups);
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 删除底库(@PathParam不需要判断空)
   * 
   * @param group Required人脸库名称，每个库的名称都是唯一的
   * @return
   */
  @DELETE
  @Path("/g/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dropGroup(
      @PathParam("group") String group) {
    logger.info(new Date()+"  delete group group:"+group);
    try {
      if (group==null || "".equals(group.trim()) ||
          !HTableClientUtils.tableExist(Constant.FACE_+group.trim())) {
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\""+group+" not found\"}").build();
      }
      // 1.删除底库表之后
      HTableClientUtils.dropTable(Constant.FACE_+group.trim());
      
      // 2.需要删除元数据表中对应的信息
      String mateTable = PropertiesUtil.getValue(Constant.HBASEMATETABLE);
      hbaseService.deleteByValue(mateTable,group.trim());
      
      // 3.返回结果
      OkayBean bean = new OkayBean();
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 备份数据库
   * 
   * @param group 库名
   * @return
   */
  @GET
  @Path("/dump")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dump(@QueryParam("group") String group) {
    logger.info(new Date() + "  dump database:" + group);
    try {
      if (group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_ + group.trim())) {
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\""+group+" not found\"}").build();
      }
      // TODO 数据库备份
      return Response.status(Response.Status.OK).entity("数据库文件").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * 恢复数据库
   * 
   * @param group 库名
   * @param file 数据文件
   * @return
   */
  @POST
  @Path("/restore")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response restore(
      @FormDataParam("group") String group,
      @FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    logger.info(new Date() + "  restore database:" + group);
    try {
      // 1.校验
      if (group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_ + group.trim())) {
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\""+group+" not found\"}").build();
      }
      if(fileDetail == null){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"file: http: no such file\"}").build();
      }
      // 2.数据库恢复 TODO
      
      // 3.结果返回
      OkayBean bean = new OkayBean();
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
       e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * 图片入库
   * 
   * @param group Required人脸库名称，每个库的名称都是唯一的
   * @param tag 图片注释
   * @param image Required人脸图片
   * @param image_rect 人脸位置信息，格式：{ "left": -2, "top": 49, "width": 97, "height": 98 
   * @param crop 返回裁剪人脸图像
   * @return
   */
  @POST
  @Path("/g/{group}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response insertGroup(
      @PathParam("group") String group,
      @FormDataParam("tag") String tag,
      @FormDataParam("image") InputStream uploadedInputStream,
      @FormDataParam("image") FormDataContentDisposition fileDetail,
      @FormDataParam("image_rect") String image_rect,
      @FormDataParam("crop") boolean crop) {//TODO crop不一样返回结果不一样
    logger.info(new Date()+"  insert image into group:"+group+"  "
        + "image name:"+fileDetail+"  image_rect:"+image_rect);
    if (fileDetail == null) {        
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"image: http: no such file\"}").build();
    } else {
      try {
        // 1.底库不存在
        if(group==null || "".equals(group.trim()) || 
            !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\""+group+" not found\"}").build();
        }
        // 2.构造序列化消息发送到kafka
        Event event = new Event();
        String session_id = UUID.randomUUID().toString().replace("-", "");
        event.setType(EventType.add.getValue());
        event.setSession_id(session_id);
        //   2.1序列化的种类
        byte [] buffer = IOUtils.toByteArray(uploadedInputStream);
        AddFaceParams params = new AddFaceParams();
        params.setGroup_name(Constant.FACE_+group.trim());
        params.setTag(tag);
        params.setImage(buffer);
        params.setImage_rect(image_rect);
        params.setCrop(crop);
        event.setParams(params);
        kafkaProducerService.produceMessage(event);
        
        // 3.通过sessionid从hbase获取结果
        String face_result_info = PropertiesUtil
            .getValue(Constant.HBASERESULTTABLE);
        String resultJson = getRsultInfo(session_id, face_result_info);
        //  超时标识
        if("-1".equals(resultJson)){
          return Response.status(408).entity(TIMEOUT).build();
        }
        
        // 4.返回结果给用户
        StoreBean bean = new StoreBean();
        bean.setId(session_id);
        Face face = new Face();
        Attrs attrs = new Attrs();
        Rect rect = new Rect();
        face.setAttrs(attrs);
        face.setRect(rect);
        bean.setFace(face);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
    }
  }
  
  /**
   * 获取图片信息(@PathParam不需要判断空)
   * 
   * @param group Required人脸库名称，每个库的名称都是唯一的
   * @param photo Required图片id
   * @return
   */
  @GET
  @Path("/g/{group}/{photo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getImageInfo(
      @PathParam("group") String group, 
      @PathParam("photo") String photo) {
    logger.info(new Date()+"  getImageInfo group："+group+" photo:"+photo);
    try {
      // 1.校验
      if(group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"group "+group+" not found\"}").build();
      }
      // 2.获取图片信息   如果找不到对应图片 
      Result result = hbaseService.selectRow(Constant.FACE_+group.trim(), photo);
      if(result.size() == 0){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+photo+" not found\"}").build();
      }
      // 3.结果返回
      Map<byte[], byte[]> cfMap = result.getFamilyMap(Bytes.toBytes("attr"));
      String tag = Bytes.toString(cfMap.get("tag"));
      ImageBean bean = new ImageBean();
      bean.setId(photo);
      bean.setTag(tag);
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * 修改图片信息
   * 
   * @param group Required人脸库名称，每个库的名称都是唯一的
   * @param imageID Required图片id
   * @param tag 新的tag
   * @return
   */
  @PUT //TODO 没找到 @PATCH改用@PUT
  @Path("/g/{group}/{photo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response patchImage(
      @PathParam("group") String group, 
      @PathParam("photo") String photo,
      @FormParam("tag") String tag) {
    logger.info(new Date()+"  getImageInfo group："+group+" photo:"+photo+" tag:"+tag);
    try {
      // 1.校验
      if(group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"group "+group+" not found\"}").build();
      }
      // 2.获取图片信息   如果找不到对应图片 
      Result result = hbaseService.selectRow(Constant.FACE_+group.trim(), photo);
      if(result.size() == 0){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+photo+" not found\"}").build();
      }
      // 3.修改 TODO
      hbaseService.update(group, photo, "attr", "tag", tag);
      OkayBean bean = new OkayBean();
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 移动图片
   * 
   * @param targetgroup Required 目标库
   * @param sourcegroup Required 源库
   * @param sourcephoto Required 图片id
   * @param tag 新的tag
   * @param copy 非空则留在原库
   * @return
   */
  @POST
  @Path("/move")
  @Produces(MediaType.APPLICATION_JSON)
  public Response moveImage(
      @FormParam("targetgroup") String targetgroup,
      @FormParam("sourcegroup") String sourcegroup,
      @FormParam("sourcephoto") String sourcephoto,
      @FormParam("tag") String tag,
      @FormParam("copy") String copy) {
    logger.info(new Date()+"  moveImage targetgroup："+targetgroup+" sourcegroup:"+sourcegroup+" sourcephoto:"+sourcephoto
        +" tag:"+tag+" copy:"+copy);
    try {
      // 1.校验
      if(targetgroup==null || "".equals(targetgroup.trim()) ||
          sourcegroup==null || "".equals(sourcegroup.trim()) ||
          !HTableClientUtils.tableExist(Constant.FACE_+targetgroup.trim())||
          !HTableClientUtils.tableExist(Constant.FACE_+sourcegroup.trim())){
        return Response.status(Response.Status.NOT_FOUND).entity( "{\"error\":\""+targetgroup+" or "+sourcegroup+": not found\"}").build();
      }
      // 2.获取图片信息   如果找不到对应图片 
      Result result = hbaseService.selectRow(Constant.FACE_+sourcegroup.trim(), sourcephoto);
      if(result.size() == 0){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+sourcephoto+" not found\"}").build();
      }
      
      // 3.移动图片 TODO 获取结果信息返回
      MoveBean bean = new MoveBean();
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 本库信息和注册照片(@PathParam不需要判断空)
   * 
   * @param group Required 库名
   * @param cursor 游标
   * @return
   */
  @GET
  @Path("/g/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getGroupImage(
      @PathParam("group") String group,
      @QueryParam("cursor") @DefaultValue("1") String cursor) {
    logger.info(new Date()+"  getGroupImage group："+group+" cursor:"+cursor);
    try {
      // 1.判断空
      if(group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\""+group+": not found\"}").build();
      }
      // 2.获取信息 TODO
      
      // 3.结果返回
      GroupImageBean bean = new GroupImageBean();
      List<Photos> photos = new ArrayList<Photos>();
      photos.add(new Photos());
      bean.setPhotos(photos);
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 删除图片
   * 
   * @param group 库名
   * @param photo 图片 ID
   * @return
   */
  @DELETE
  @Path("/g/{group}/{photo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deletePhoto(
      @PathParam("group") String group, 
      @PathParam("photo") String photo) {
    logger.info(new Date()+"  delete image:"+photo+"  from group:"+group);
    try {
      // 1.校验
      if(group==null || "".equals(group.trim()) || 
          !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"group "+group+" not found\"}").build();
      }
      // 2.获取图片信息   如果找不到对应图片 
      Result result = hbaseService.selectRow(Constant.FACE_+group.trim(), photo);
      if(result.size() == 0){
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+photo+" not found\"}").build();
      }
      // 2.构造序列化消息发送到kafka
      Event event = new Event();
      String session_id = UUID.randomUUID().toString().replace("-", "");
      event.setType(EventType.delete.getValue());
      event.setSession_id(session_id);
      // 2.1序列化的种类
      DelFaceParams params = new DelFaceParams();
      params.setGroup(group);
      params.setPhoto(photo);
      event.setParams(params);
      kafkaProducerService.produceMessage(event);

      // 3.通过sessionid从hbase获取结果
      String face_result_info = PropertiesUtil
          .getValue(Constant.HBASERESULTTABLE);
      String resultJson = getRsultInfo(session_id, face_result_info);
      //  超时标识
      if("-1".equals(resultJson)){
        return Response.status(408).entity(TIMEOUT).build();
      }
      
      // 4.获取结果返回
      OkayBean bean = new  OkayBean();
      bean.setOkay(true);
      return Response.status(Response.Status.OK).entity(bean).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * 人脸检测
   * 
   * @param image 图片
   * @param crop 返回裁剪人脸图像
   * @return
   */
  @POST
  @Path("/detect")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response detect(
      @FormDataParam("image") InputStream uploadedInputStream,
      @FormDataParam("image") FormDataContentDisposition fileDetail,
      @FormDataParam("crop") boolean crop) { //TODO crop不一样结果不一样
    logger.info(new Date()+"  detect image  image name:"+fileDetail);
    if (fileDetail == null) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"image: http: no such file\"}").build();
    } else {
      try {
        // 1.构造序列化消息发送到kafka
        Event event = new Event();
        String session_id = UUID.randomUUID().toString().replace("-", "");
        event.setType(EventType.detect.getValue());
        event.setSession_id(session_id);
        //   1.1序列化的种类
        DetectParams params = new DetectParams();
        byte [] buffer = IOUtils.toByteArray(uploadedInputStream);
        params.setImage(buffer);
        params.setCrop(crop);
        event.setParams(params);
        kafkaProducerService.produceMessage(event);
        
        // 2.通过sessionid从hbase获取结果
        String face_result_info = PropertiesUtil
            .getValue(Constant.HBASERESULTTABLE);
        String resultJson = getRsultInfo(session_id, face_result_info);
        //  超时标识
        if("-1".equals(resultJson)){
          return Response.status(408).entity(TIMEOUT).build();
        }
        
        // 3.返回结果给用户
        DetectBean bean = new DetectBean();
        Face faces = new Face();
        Attrs attrs = new Attrs();
        Rect rect = new Rect();
        faces.setAttrs(attrs);
        faces.setRect(rect);
        bean.setFaces(faces);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
    }
  }
  
  /**
   * 人脸验证
   * 
   * @param group 库名
   * @param photo 图片id多个图片,分割
   * @param image 图片
   * @param image_rect 对比图片人脸矩形
   * @return
   */
  @POST
  @Path("/verify")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response verify(
      @FormDataParam("group") String group,
      @FormDataParam("photo") String photo,
      @FormDataParam("image") InputStream uploadedInputStream,
      @FormDataParam("image") FormDataContentDisposition fileDetail,
      @FormDataParam("image_rect")  String image_rect) {
    logger.info(new Date()+"  verify group:"+group+"  photo:"+photo
        +" fileDetail:"+fileDetail+" image_rect:"+image_rect);
    if(fileDetail == null){
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"image: http: no such file\"}").build();
    }else {
      try {
        // 1.校验
        if(group==null || "".equals(group.trim()) ||
            !HTableClientUtils.tableExist(Constant.FACE_+group.trim())){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"group "+group+" not found\"}").build();
        }
        // 2.获取图片信息   如果找不到对应图片 
        if(photo == null || "".equals(photo)){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+photo+" not found\"}").build();
        }
        String [] keys = photo.split(",");
        Result[] results = hbaseService.selectRows(Constant.FACE_+group.trim(), keys);
        if(results.length == 0){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+photo+" not found\"}").build();
        }
        
        // 3.构造序列化消息发送到kafka
        Event event = new Event();
        String session_id = UUID.randomUUID().toString().replace("-", "");
        event.setType(EventType.verify.getValue());
        event.setSession_id(session_id);
        //   3.1序列化的种类
        VerifyParams params = new VerifyParams();
        byte [] buffer = IOUtils.toByteArray(uploadedInputStream);
        params.setImage(buffer);
        params.setGroup(group);
        params.setImage_rect(image_rect);
        params.setPhoto(photo);
        event.setParams(params);
        kafkaProducerService.produceMessage(event);
        
        // 4.通过sessionid从hbase获取结果
        String face_result_info = PropertiesUtil
            .getValue(Constant.HBASERESULTTABLE);
        String resultJson = getRsultInfo(session_id, face_result_info);
        //  超时标识
        if("-1".equals(resultJson)){
          return Response.status(408).entity(TIMEOUT).build();
        }
        
        // 5.返回结果给用户
        VerifyBean bean = new VerifyBean();
        Rect rect = new Rect();
        Attrs attrs = new Attrs();
        Face face = new Face();
        face.setAttrs(attrs);
        face.setRect(rect);
        
        List<Photos> photosList = new ArrayList<Photos>();
        Photos photos = new Photos();
        photosList.add(photos);
        Groups groups2 = new Groups();
        groups2.setPhotos(photosList);
        
        List<Groups> groupsList = new ArrayList<Groups>();
        groupsList.add(groups2);
        bean.setFace(face);
        bean.setGroups(groupsList);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
    }
  }

  /**
   * 人脸比对
   * 
   * @param image1InputStream 图片1流
   * @param image1Detail 图片1元信息
   * @param image1_rect 图片1人脸矩形
   * @param image2InputStream 图片2流
   * @param image2Detail 图片2元信息
   * @param image2_rect 图片2人脸矩形
   * @return
   */
  @POST
  @Path("/compare")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response compare(
      @FormDataParam("image1") InputStream image1,
      @FormDataParam("image1") FormDataContentDisposition image1Detail,
      @FormDataParam("image1_rect") String image1_rect,
      @FormDataParam("image2") InputStream image2,
      @FormDataParam("image2") FormDataContentDisposition image2Detail,
      @FormDataParam("image2_rect") String image2_rect) {
    logger.info(new Date()+"  compare image1:"+image1Detail+" image1_rect:"+image1_rect+
        "  to image2:"+image2Detail+" image2_rect:"+image2_rect);
    if (image1Detail == null || image2Detail == null) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
          entity("{\"error\":\"image1 or image2: http: no such file\"}").build();
    } else {
      try {
        // 1.构造序列化消息发送到kafka
        Event event = new Event();
        String session_id = UUID.randomUUID().toString().replace("-", "");
        event.setType(EventType.compare.getValue());
        event.setSession_id(session_id);
        //   1.1序列化的种类
        CompareParams params = new CompareParams();
        byte [] buffer1 = IOUtils.toByteArray(image1);
        byte [] buffer2 = IOUtils.toByteArray(image2);
        params.setImage1(buffer1);
        params.setImage2(buffer2);
        params.setImage1_rect(image1_rect);
        params.setImage2_rect(image2_rect);
        event.setParams(params);
        kafkaProducerService.produceMessage(event);
        
        // 2.通过sessionid从hbase获取结果
        String face_result_info = PropertiesUtil
            .getValue(Constant.HBASERESULTTABLE);
        String resultJson = getRsultInfo(session_id, face_result_info);
        //  超时标识
        if("-1".equals(resultJson)){
          return Response.status(408).entity(TIMEOUT).build();
        }
        
        // 3.返回结果给用户
        Comparebean bean = new Comparebean();
        
        Face face1 = new Face();
        Attrs attrs1 = new Attrs();
        Rect rect1 = new Rect();
        face1.setAttrs(attrs1);
        face1.setRect(rect1);
        
        Face face2 = new Face();
        Attrs attrs2 = new Attrs();
        Rect rect2 = new Rect();
        face2.setAttrs(attrs2);
        face2.setRect(rect2);
        
        bean.setFace1(face1);
        bean.setFace2(face2);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
    }
  }
 
  /**
   * 相似人脸检索
   * 
   * @param group 被搜索库名，可以传多个，用逗号分隔
   * @param image 人脸图片数据
   * @param image_rect  对比图片人脸矩形
   * @param limit 返回相似照片张数
   * @param filter  结果过滤条件
   * @param crop  返回裁剪人脸图像
   * @return
   */
  @POST
  @Path("/search")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response search(
      @FormDataParam("group") String group,
      @FormDataParam("image") InputStream uploadedInputStream,
      @FormDataParam("image") FormDataContentDisposition fileDetail,
      @FormDataParam("image_rect") String image_rect,
      @DefaultValue("1") @FormDataParam("limit") Integer limit,
      @FormDataParam("filter") String filter,
      @FormDataParam("crop") boolean crop  //TODO crop不一样 返回结果不一样
      ) {
    logger.info(new Date()+"  search image from group:"+group+"  image name:"+
      fileDetail+" image_rect:"+image_rect+"  limit:"+limit);
    if (fileDetail == null) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"image: http: no such file\"}").build();
    } else {
      try {
        // 1.判断底库是否存在
        String [] groups = group.split(",");
        for(String g:groups){
          if(!HTableClientUtils.tableExist(Constant.FACE_+g)){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"group "+group+" not found\"}").build();
          }
        }
        
        // 2.发送消息给kafka
        Event event = new Event();
        String session_id = UUID.randomUUID().toString().replace("-", "");
        event.setType(EventType.search.getValue());
        event.setSession_id(session_id);
        //   1.1序列化的种类
        SearchParams params = new SearchParams();
        byte [] buffer = IOUtils.toByteArray(uploadedInputStream);
        params.setImage(buffer);
        params.setGroup(group);
        params.setImage_rect(image_rect);
        params.setLimit(limit);
        event.setParams(params);
        kafkaProducerService.produceMessage(event);
        
        // 3.通过sessionid从hbase获取结果
        String face_result_info = PropertiesUtil
            .getValue(Constant.HBASERESULTTABLE);
        String resultJson = getRsultInfo(session_id, face_result_info);
        //  超时标识
        if("-1".equals(resultJson)){
          return Response.status(408).entity(TIMEOUT).build();
        }
        
        // 4.返回json
        SearchBean bean = new SearchBean();
        Rect rect = new Rect();
        Attrs attrs = new Attrs();
        Face face = new Face();
        face.setAttrs(attrs);
        face.setRect(rect);
        
        List<Photos> photosList = new ArrayList<Photos>();
        Photos photos = new Photos();
        photosList.add(photos);
        Groups groups2 = new Groups();
        groups2.setPhotos(photosList);
        
        List<Groups> groupsList = new ArrayList<Groups>();
        groupsList.add(groups2);
        bean.setFace(face);
        bean.setGroups(groupsList);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
    }
  }
  
  /**
   * 批量检索
   * 
   * @param targetgroup 被搜索库名
   * @param sourcegroup 待搜索库名
   * @param sourcephoto  图片id,分割
   * @param limit 返回相似照片张数
   * @return
   */
  @POST
  @Path("/batchsearch")
  @Produces(MediaType.APPLICATION_JSON)
  public Response batchSearch(
      @FormParam("targetgroup") String targetgroup,
      @FormParam("sourcegroup") String sourcegroup,
      @FormParam("sourcephoto") String sourcephoto,
      @DefaultValue("1") @FormParam("limit") Integer limit) {
    logger.info(new Date()+"  batchsearch image from targetgroup:"+targetgroup+"  sourcegroup:"+
        sourcegroup+"  sourcephoto:"+sourcephoto);
      try {
        // 1.判断底库是否存在
        if(targetgroup == null || "".equals(targetgroup.trim()) ||
          sourcegroup == null || "".equals(sourcegroup.trim()) ||
            !HTableClientUtils.tableExist(Constant.FACE_+targetgroup)||
            !HTableClientUtils.tableExist(Constant.FACE_+sourcegroup)){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":"
              + "\"targetgroup "+targetgroup+" or sourcegroup "+sourcegroup+" not found\"}").build();
        }
        
        // 2.从hbase中获取数据 TODO 获取图片信息   如果找不到对应图片 
        // 获取表的信息
        String mateTable = PropertiesUtil.getValue(Constant.HBASEFAILURETABLE);
        hbaseService.selectByValue(mateTable, targetgroup);
        // 获取图片的信息
        if(sourcephoto == null || "".equals(sourcephoto)){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+sourcephoto+" not found\"}").build();
        }
        String [] keys = sourcephoto.split(",");
        Result [] results = hbaseService.selectRows(Constant.FACE_+targetgroup, keys);
        if(results.length == 0){
          return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"id "+sourcephoto+" not found\"}").build();
        }
        
        // 3.返回结果给用户
        BatchSearchBean bean = new BatchSearchBean();
        Source source = new Source();
        Photos photo = new Photos();
        List<Photos> photos = new ArrayList<Photos>();
        photos.add(photo);
        bean.setPhotos(photos);
        bean.setSource(source);
        return Response.status(Response.Status.OK).entity(bean).build();
      } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(INTERNAL_SERVER_ERROR).build();
      }
  }
  
//  /**
//   * 获取入库失败图片
//   * 
//   * @param group_name Required人脸库名称，每个库的名称都是唯一的
//   * @param start_id Required 起始图片id
//   * @param size 获取多少张
//   * @return
//   */
//  @POST
//  @Path("/g/failure")
//  @Produces(MediaType.APPLICATION_JSON)
//  public Response getFailureImage(
//      @FormParam("group")  String group,
//      @FormParam("start_id")  String start_id,
//      @FormParam("size") @DefaultValue("10") Integer size
//      ) {
//    logger.info(new Date()+"  get failure image from group:"+group+"  start_id:"+start_id+"  size:"+size);
//    ResponseMessage message = new ResponseMessage();
//    if (group == null || "".equals(group.trim()) || 
//        start_id == null || "".equals(start_id.trim())) {
//      message.setSuccess(false);
//      message.setError_info("group_name or start_id can not be empty");
//      return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
//    } else {
//      try {
//        // 1.直接从hbase获取失败的分页数据,将数据包装成对象
//        String failureTable = PropertiesUtil.getValue(Constant.HBASEFAILURETABLE);
//        String failureTableCf = PropertiesUtil.getValue(Constant.HBASEFAILURETABLECF);
//        List<Object> resultList = hbaseService.packRow2Result(failureTable,
//            hbaseService.selectRows(failureTable, start_id, size,group), failureTableCf);
//        
//        // 2.返回结果给用户
//        message.setResult(resultList);
//        return Response.status(Response.Status.OK).entity(message).build();
//      } catch (Exception e) {
//        e.printStackTrace();
//        serviceError(message);
//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
//      }
//    }
//  }
  
  
 
  
  /**
   * 从结果消息中取出结果
   * @param session_id id
   * @param face_result_info 结果表
   * @return
   * @throws IOException
   */
  private String getRsultInfo(String session_id, String face_result_info) throws IOException {
    // 结果表的列族
    String face_result_info_cf = PropertiesUtil.getValue(Constant.HBASERESULTTABLECF);
    // 获取结果表超时时间
    int timeout = Integer.parseInt(PropertiesUtil.getValue(Constant.HBASERESULTTABLETIMEOUT));
    // 结果消息
    Result result_info = null;
    // 超时开始时间
    long startTime = System.currentTimeMillis();
    while (true) {
      result_info = hbaseService.selectRow(face_result_info, session_id);
      // 得到结果返回
      if (!result_info.isEmpty()) {
        break;
      }
      // 超时返回
      if (System.currentTimeMillis() - startTime >= timeout) {
        // 超时标识
        return "-1";
      }
      // 10毫秒获取一次
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return null;
      }
    }
    String result = "";
    if (!result_info.isEmpty()) {
      // 获取返回的结果
      result = Bytes.toString(result_info.getValue(face_result_info_cf.getBytes(), "value".getBytes()));
      // 获取到结果后，删除结果
      hbaseService.deleteRows(face_result_info, session_id);
    }
    return result;
  }
  
}
