package com.kz.face.storage.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.storage.constant.Constant;
import com.kz.face.storage.server.HbaseServer;
import com.kz.face.storage.server.impl.HbaseServerImpl;
import com.kz.face.storage.utils.JsonUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * image upload ,download ,delete
 * 
 * @author liuxing
 *
 */
@Path("/image")
public class KZImageStorage {
  private static final Log logger = LogFactory.getLog(KZImageStorage.class);
  private HbaseServer hbaseServer = new HbaseServerImpl();

  @GET
  @Path("/download/{imageID}")
  public Response image_download(@PathParam("imageID") String imageID) {
    logger.info("download image...imageID:"+imageID);
    ByteArrayOutputStream bos = null;
    try {
      bos = new ByteArrayOutputStream();
      Result result = hbaseServer.getData(imageID);
      // 判空
      if (result == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("imageID not exist")
            .type(MediaType.APPLICATION_JSON).build();
      }
      String fileName =
          new String(result.getValue(Bytes.toBytes("attr"), Bytes.toBytes("fileName")), "UTF-8");
      byte[] fileContent = result.getValue(Bytes.toBytes("content"), Bytes.toBytes("fileContent"));

      bos.write(fileContent);
      bos.flush();
      return Response.ok(bos.toByteArray(), "multipart/form-data")
          .header("Content-Disposition", "attachment; filename=" + fileName).build();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(Response.Status.NOT_FOUND)
          .entity(JsonUtil.getJson("download", Constant.HDFSERROR + "", Constant.HDFSERRORDES))
          .type(MediaType.APPLICATION_JSON).build();
    } finally {
      if (bos != null)
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response image_upload(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    logger.info("upload image...");
    if (uploadedInputStream != null && fileDetail != null) {
      String resultID = hbaseServer.insertData(fileDetail, uploadedInputStream);
      if (resultID == null) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("server error")
            .type(MediaType.APPLICATION_JSON).build();
      }
      return Response.status(Response.Status.OK).entity(resultID).type(MediaType.APPLICATION_JSON)
          .build();
    }
    return Response.status(Response.Status.BAD_REQUEST).entity("file can not be empty")
        .type(MediaType.APPLICATION_JSON).build();
  }

  @DELETE
  @Path("/delete/{imageID}")
  public Response image_delete(@PathParam("imageID") String imageID) {
    logger.info("delete image...imageID:"+imageID);
    if (hbaseServer.deleteData(imageID)) {
      return Response.status(Response.Status.OK).entity("delete ok")
          .type(MediaType.APPLICATION_JSON).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("delete failure")
        .type(MediaType.APPLICATION_JSON).build();
  }
}
