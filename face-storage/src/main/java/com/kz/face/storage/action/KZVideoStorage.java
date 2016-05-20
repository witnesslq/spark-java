package com.kz.face.storage.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;

import com.kz.face.storage.constant.Constant;
import com.kz.face.storage.server.HdfsServer;
import com.kz.face.storage.server.impl.HdfsServerImpl;
import com.kz.face.storage.utils.JsonUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * file upload ,download ,delete
 * 
 * @author liuxing
 *
 */
@Path("/video")
public class KZVideoStorage {
  static final Log logger = LogFactory.getLog(KZVideoStorage.class);
  HdfsServer hdfsServer = new HdfsServerImpl();
  // 测试路径
  static final String USERPATH = "/liuxing/";

  @GET
  @Path("/download/{videoID}")
  public Response video_download(@PathParam("videoID") String videoID,
      @Context HttpServletResponse response) {
    logger.info("download video file..." + videoID);
    InputStream inputStream = null;
    ServletOutputStream out = null;
    FileSystem fs = null;
    try {
      String [] name = videoID.split(Constant.SPLIT);
      if(name.length!=2){
        return Response.status(Response.Status.BAD_REQUEST).entity("file not found")
            .type(MediaType.APPLICATION_JSON).build();
      }
      response.setContentType("multipart/form-data");
      response.setCharacterEncoding("UTF-8");
      name[1] = new String(name[1].getBytes("GB2312"), "ISO_8859_1"); 
      response.setHeader("Content-Disposition", "attachment;fileName=" + name[1]);
      
      inputStream = hdfsServer.download(USERPATH + videoID);
      if (inputStream == null) {
        return Response
            .status(Response.Status.NOT_FOUND).entity(JsonUtil.getJson("download",
                Constant.DOWNLOADERROR + "", Constant.DOWNLOADERRORDES))
            .type(MediaType.APPLICATION_JSON).build();
      }
      
      out = response.getOutputStream();
      byte[] buffer = new byte[1024];
      int len = 0;
      while ((len = inputStream.read(buffer)) != -1) {
        out.write(buffer,0,len);
        out.flush();
      }
      return Response.ok(200).entity("0").build();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(Response.Status.NOT_FOUND)
          .entity(JsonUtil.getJson("download", Constant.HDFSERROR + "", Constant.HDFSERRORDES))
          .type(MediaType.APPLICATION_JSON).build();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
        if (inputStream != null) {
          inputStream.close();
        }
        if(fs != null){
          fs.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response video_upload(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    logger.info("upload video file...");
    String fileName = "";
    String returnJson = null;
    int result = 0;
    if (uploadedInputStream != null && fileDetail != null) {
      String uuid = UUID.randomUUID().toString().replaceAll("-", "");
      fileName = uuid +Constant.SPLIT+ fileDetail.getFileName();
      result = hdfsServer.upload(uploadedInputStream, USERPATH + fileName);
      switch (result) {
        case 0:
          returnJson = JsonUtil.getJson("upload", result + "", Constant.HDFSOK);
          break;
        case -1:
          returnJson = JsonUtil.getJson("upload", result + "", Constant.UPLOADERRORDES);
          break;
        case -2:
          returnJson = JsonUtil.getJson("upload", result + "", Constant.HDFSERRORDES);
          break;
      }
      return Response.status(Response.Status.OK).entity(returnJson).type(MediaType.APPLICATION_JSON)
          .build();
    } else {
      return Response.status(Response.Status.OK)
          .entity(JsonUtil.getJson("upload", Constant.UPLOADERROR + "", Constant.UPLOADERRORDES))
          .type(MediaType.APPLICATION_JSON).build();
    }
  }

  @DELETE
  @Path("/delete/{videoID}")
  public Response video_delete(@PathParam("videoID") String videoID) {
    logger.info("delete video file..." + videoID);
    int result = hdfsServer.delete(USERPATH + videoID);
    String returnJson = null;
    switch (result) {
      case 0:
        returnJson = JsonUtil.getJson("delete", result + "", Constant.HDFSOK);
        break;
      case -1:
        returnJson = JsonUtil.getJson("delete", result + "", Constant.DELETEERRORDES);
        break;
      case -2:
        returnJson = JsonUtil.getJson("delete", result + "", Constant.HDFSERRORDES);
        break;
    }
    return Response.status(Response.Status.OK).entity(returnJson).type(MediaType.APPLICATION_JSON)
        .build();
  }
}
