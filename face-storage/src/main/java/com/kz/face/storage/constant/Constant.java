package com.kz.face.storage.constant;

/**
 * 常量类
 * @author liuxing
 *
 */
public class Constant {
    /**创建文件成功码**/
    public static final int CREATEOK = 0;
    /**创建文件失败码**/
    public static final int CREATEERROR = -1;
    /**创建文件失败码描述**/
    public static final String CREATEERRORDES = "dircetory exist or path error";
    
    /**上传文件成功码**/
    public static final int UPLOADOK = 0;
    /**上传文件失败码**/
    public static final int UPLOADERROR = -1;
    /**上传文件失败码描述**/
    public static final String UPLOADERRORDES = "need a file";
    
    /**下载文件成功码**/
    public static final int DOWNLOADOK = 0;
    /**下载文件不存在失败码**/
    public static final int DOWNLOADERROR = -1;
    /**下载文件不存在失败码描述**/
    public static final String DOWNLOADERRORDES = "file not exist";
    
    /**删除文件成功码**/
    public static final int DELETEOK = 0;
    /**删除文件不存在失败码**/
    public static final int DELETEERROR = -1;
    /**删除文件不存在失败码描述**/
    public static final String DELETEERRORDES = "dircetory or file not exist";
    
    
    /**文件系统异常码**/
    public static final int HDFSERROR = -2;
    /**文件系统异常描述**/
    public static final String HDFSERRORDES = "hdfs system error";
    /**文件系统操作成功**/
    public static final String HDFSOK = "OK";
    
    /**文件名称的組拼**/
    public static final String SPLIT = "&_";
}