package com.kz.face.api.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class MysqlUtil {
	 private static ComboPooledDataSource cpds=new ComboPooledDataSource(true); 
	/**
	 * 此处可以不配置，采用默认也行
	 */
	static {
		try {
			cpds.setDataSourceName("mydatasource");
			cpds.setJdbcUrl("jdbc:mysql://192.168.129.103:3306/logdatauser");
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			cpds.setUser("liuxing");
			cpds.setMaxIdleTime(1800);
			cpds.setIdleConnectionTestPeriod(1800);
			cpds.setPassword("liuxing");
			cpds.setMaxPoolSize(30);
			cpds.setMinPoolSize(10);
			cpds.setAcquireIncrement(5);
			cpds.setInitialPoolSize(20);
		} catch (Exception e) {
			System.err.println("加载Mysql数据库驱动失败！");
		}
	}
    /**
     * 获取Connection
     * 
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection(){
        Connection conn = null;
        try {
            conn = cpds.getConnection();
        } catch (SQLException e) {
        	e.printStackTrace();
        	System.err.println("获取数据库连接失败！");
        }
        return conn;
    }
    /**
     * 还回Connection
     * @param conn
     */
    public static void returnConnection(Connection conn){
    	if (conn != null) {
            try {
            	conn.close();
            } catch (SQLException e) {
            	System.err.println(e.getMessage());
            }
        }
    }
    /**
     * 关闭ResultSet
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            	System.err.println(e.getMessage());
            }
        }
    }
    /**
     * 关闭Statement
     * @param stmt
     */
    public static void closePreparedStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }       
            catch (Exception e) {
            	System.err.println(e.getMessage());
            }
        }
    }

}
