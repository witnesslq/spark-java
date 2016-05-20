package com.kz.face.storage.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.storage.server.HdfsServer;
import com.kz.face.storage.server.impl.HdfsServerImpl;
import com.kz.face.storage.utils.JsonUtil;

/**
 * file upload ,download ,delete
 * 
 * @author liuxing
 *
 */
@Path("/check")
public class KZCheck {
    private final Log logger = LogFactory.getLog(getClass());
	// 测试路径
	private static final String USERPATH = "/liuxing/";
	
    @GET
    @Path("/ailve")
    public Response check_alive() {
        logger.info("check system is alive");
        return Response.status(200).entity(JsonUtil.getJson("alive", 0+"", "is alive")).build();
    }   
    @POST
    @Path("/register")
    public Response user_register( 
            @FormParam("username") String username, 
            @FormParam("password") String password) {
        logger.info("register to system");
        if(username==null||username.trim().equals("")||password==null||password.trim().equals("")){
            return Response.status(200).entity(JsonUtil.getJson("register", -1+"", "username or password can not be empty")).build();
        }else{
            HdfsServer daoInter = new HdfsServerImpl();
            daoInter.create(USERPATH+username+password);
            return Response.status(200).entity(JsonUtil.getJson("register", 0+"", "register success")).build();
        }
    }
}
