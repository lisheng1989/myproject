package com.owen.dao;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.owen.chat.UserDB;

public class UserDao {

	
	/** 获取指定班级的学生结构化信息
	 * @param refClassId 指定的班级编号
	 * @return
	 */
	public static UserDB getUserDB(long userId) {
		UserDB rtn = null;
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {

			conn = DaoUtility.getConnection();
			state = conn.createStatement();
			rs = state.executeQuery("select *from `member` where `id` = "+userId);
			//System.out.println(refClassId);
			while (rs.next()) {

				if (rtn == null) rtn = new UserDB(rs.getLong("id"),rs.getString("account"),rs.getLong("parent_member_id"));
				break;

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			rtn = null;
		} finally {
			DaoUtility.disposeResource(rs, state, conn);
		}
		return rtn;
	}
	
	
	/** 获取指定班级的学生结构化信息
	 * @param refClassId 指定的班级编号
	 * @return
	 */
	public static ArrayList<Long> getUserDBByparentId(long userId) {
		ArrayList<Long> rtn = null;
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {
			conn = DaoUtility.getConnection();
			state = conn.createStatement();
			rs = state.executeQuery("select `id` from `member` where `parent_member_id` = "+userId);
			//System.out.println(refClassId);
			while (rs.next()) {
				if (rtn == null) rtn = new ArrayList<Long>();
				rtn.add(rs.getLong("id"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			rtn = null;
		} finally {
			DaoUtility.disposeResource(rs, state, conn);
		}
		return rtn;
	}
}
