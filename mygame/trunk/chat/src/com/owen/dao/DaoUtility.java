package com.owen.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @ClassName DaoUtility
 * @Description DAO 工具类
 * @author Sammy Zhong
 * @date 2012-06-13
 * @version 1.0.0
 */
public class DaoUtility {

	/** MySQL 数据库连接字符串
	 * Ver.Ex 迁移到在线学校数据库
	 */
	private static String _connectionString = "jdbc:mysql://103.252.240.39/mygame?user=myuser&password=mypassword&useUnicode=true&characterEncoding=UTF-8";
	
	
	
	// 对 MySQL JDBC 驱动进行引用
	static {
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/** 获取一个连接
	 * @return 返回一个 MySQL 连接
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DaoUtility._connectionString);
	}
	
	/** 释放连接占用的资源
	 * @param refResult 对应的结果集
	 * @param refState 对应的访问状态
	 * @param refConn 对应的连接
	 */
	public static void disposeResource(ResultSet refResult, Statement refState, Connection refConn) {
		try {
			if (refResult != null) refResult.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (refState != null) refState.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (refConn != null) refConn.close();
				} catch (SQLException e) {
					e.printStackTrace();  
				}  
			}  
		}
	}
	
}
