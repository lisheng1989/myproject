package com.owen.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.conf.HttpMethod;
import com.owen.dao.UserDao;
import com.owen.main.ChatServer;
import com.owen.message.MethodType;
import com.owen.util.RedisUtil;


public class UserMgr {
	
	private ConcurrentHashMap<String,UserDB> userList = new ConcurrentHashMap<String, UserDB>();
		
	
	/** 终端业务处理类单例 */
	private static UserMgr _instance = null;
	
	/** 获取终端业务处理类单例 */
	public static UserMgr getInstance() {
		if (_instance == null) {
			_instance = new UserMgr();
		}
		return _instance;
	}
	
	/**获得用户DB通过sfsID*/
//	public UserDB getUserDBBySfsid(int sfsid)
//	{
//		UserDB uDB = null;
//		Iterator<Entry<Long, UserDB>> it = userList.entrySet().iterator();
//		while(it.hasNext())
//		{
//			Entry<Long, UserDB> entry = it.next();
//			UserDB eDB = entry.getValue();
//			if(eDB.sfsid == sfsid)
//			{
//				uDB = eDB;
//				break;
//			}		
//			
//		}
//		
//		return uDB;
//		
//	}
	static Logger logger = LoggerFactory.getLogger(UserMgr.class);

//	public void repCount() throws JSONException
//	{
//		Iterator<Entry<Long, UserDB>> it = userList.entrySet().iterator();
//		while(it.hasNext())
//		{
//			Entry<Long, UserDB> entry = it.next();
//			UserDB eDB = entry.getValue();
//			if(eDB.isOnline == 1)
//			{
//				ArrayList<Long> plist = UserDao.getUserDBByparentId(eDB.userid);
//
//				if(plist != null && plist.size()>0)
//				{
//					if(plist.size() != eDB.nextList.size())
//					{
//						ArrayList<Long>templist = new ArrayList<Long>();
//						for(int i = 0;i<plist.size();i++)
//						{
//						
//							long resUid = plist.get(i);
//							templist.add(resUid);
//							if(eDB.nextList.contains(resUid) == false)
//							{
//								eDB.nextList.add(resUid);
//								logger.info("轮询用户"+resUid);
//								if(eDB._session != null && eDB._session.isConnected())
//								{
//									
//									JSONObject jS = new JSONObject();
//									jS.put("m",MethodType.SERVER_NEWADD_INFORM);
//									JSONObject data = new JSONObject();
//									data.put("nextList", getNextUserListBySFSArray(eDB.userid));
//									jS.put("p", data);
//									eDB._session.write(jS);
//									
//								}
//							}
//						}
//						if(eDB.nextList.size()>templist.size())
//						{
//							for(int i = 0;i<eDB.nextList.size();i++)
//							{
//								Long lid = eDB.nextList.get(i);
//								if(templist.contains(lid)== false)
//								{
//									eDB.nextList.remove(lid);
//									if(eDB._session!= null && eDB._session.isConnected())
//									{
//										JSONObject jS = new JSONObject();
//										jS.put("m",MethodType.SERVER_NEWREMOVE_INFORM);
//										JSONObject data = new JSONObject();
//										data.put("rid",lid);
//										jS.put("p", data);
//										eDB._session.write(jS);	
//										
//									}
//									break;
//								}
//								
//							}			
//						}
//						
//					}
//				
//				}
//				
//			}	
//		
//		}
//		
//	}
	
//	public UserDB getUserDBBySql(long userid)
//	{
//		UserDB uDB = getUserDBByUserid(userid);
//		if(uDB == null)
//		{
//			uDB = UserDao.getUserDB(userid);
//			ArrayList<Long> plist = UserDao.getUserDBByparentId(userid);
//			if(plist != null)
//			{		
//				uDB.nextList = plist;
//				
//			}
//			if(userList.containsKey(uDB.userid)== false)
//				userList.put(uDB.userid, uDB);
//		}
//		return uDB;
//	}
	
//	/**获得下级用户列表*/
//	public ArrayList<JSONObject> getNextUserListBySFSArray(long userid)
//	{
//		 ArrayList<JSONObject> arr = new  ArrayList<JSONObject>();
//		UserDB uDB = getUserDBByUserid(userid);
//		if(uDB != null)
//		{
//			for(int i = 0;i<uDB.nextList.size();i++)
//			{
//				long tuid = uDB.nextList.get(i);
//				UserDB tUDB = getUserDBBySql(tuid);
//				if(tUDB != null && tUDB.name != null)
//				{
//					arr.add(tUDB.getJson());
//				}
//			}		
//		}
//		return arr;
//	}

	
	/**获得用户DB通过Userid*/
	public UserDB getUserDB(String account)
	{
		if(userList.containsKey(account))
			return userList.get(account);
		else
			return null;
	}

	/**创建新的用户数据
	 * @throws JSONException */
	public UserDB createUserData(String account,IoSession session) throws JSONException {
		UserDB uDB = null;
		if(userList.containsKey(account))
		{
			uDB = userList.get(account);
			uDB.isOnline = 1;
			uDB._session = session;
		}else{
			uDB = getUserDBByRedis(account);
			if(uDB != null)
			{
				uDB._session = session;
				uDB.isOnline = 1;		
			}
		
		}
		if(uDB != null)
		{
			uDB._session.setAttribute("account", account);				
			JSONObject jS = new JSONObject();
			jS.put("m",MethodType.SERVER_LOGIN_REPLY);
			JSONObject data = new JSONObject();
			jS.put("p", data);
			uDB._session.write(jS);
			logger.info("[createUserData]创建用户"+uDB.userid+"-"+uDB.name);
		}
		return uDB;
		
	}

	private UserDB getUserDBByRedis(String account) {
		if(RedisUtil.getKeyString("account") != null)
		{
			UserDB db = new UserDB(0, account, 0);
			userList.put(db.name,db);
			return db;
		}else{
			return null;
		}
		
	}
	/**处理消息*/
	public boolean handerMsg(JSONObject jS) {
		try {
			int method = jS.getInt("m");
			if(method == HttpMethod.sendPrize || method == HttpMethod.sendHongBao)
			{
				broad(jS);	
				return true;
			}else if(method == HttpMethod.sendChongZhi || method == HttpMethod.sendTiKuan || method == HttpMethod.sendChongZhi)
			{
				String account = jS.getJSONObject("p").getString("account");
				UserDB uDB = getUserDB(account);
				if(uDB != null)
				{
					uDB._session.write(jS);
				}
				return true;
			}else if(method == HttpMethod.getOnline)
			{
				JSONObject obj = new JSONObject();
				obj.put("number", getOnlineSize());
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	private  int getOnlineSize(){
		int size = 0;
		Iterator<Entry<String, UserDB>> it = userList.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, UserDB> entry = it.next();
			UserDB uDB = entry.getValue();
			if(uDB._session != null && uDB._session.isConnected())
			{
				size++;
			}
		}
		return size;
	}
	
	private void broad(JSONObject obj)
	{
		Iterator<Entry<String, UserDB>> it = userList.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, UserDB> entry = it.next();
			UserDB uDB = entry.getValue();
			if(uDB._session != null)
			{
				uDB._session.write(obj);
			}
		}
		
	}

//	/**
//	 * 通知相关联的人改人的信息变动
//	 * @throws JSONException 
//	 * */
//	public void boradLink(UserDB uDB) throws JSONException {
//		if(uDB.parentId >0)
//		{
//			UserDB pDB = getUserDBBySql(uDB.parentId);
//			if(pDB._session != null && pDB._session.isConnected())
//			{
//
//				JSONObject jS = new JSONObject();
//				jS.put("m",MethodType.SERVER_LOGIN_INFORM);
//				JSONObject data = new JSONObject();
//				data.put("user", uDB.getJson());
//				jS.put("p", data);
//				pDB._session.write(jS);
//			}
//		}
//		for(int i=0;i<uDB.nextList.size();i++)
//		{
//			long tuid = uDB.nextList.get(i);
//			UserDB uTDB = getUserDBBySql(tuid);
//			if(uTDB._session != null&& uTDB._session.isConnected())
//			{
//				JSONObject jS = new JSONObject();
//				jS.put("m",MethodType.SERVER_LOGIN_INFORM);
//				JSONObject data = new JSONObject();
//				data.put("user", uDB.getJson());
//				jS.put("p", data);
//				uTDB._session.write(jS);
//				
//			}
//		}
//			
//		
//		
//		
//	}

//	public void chat(JSONObject jS, UserDB uDB) throws JSONException {
//		
//		long userid = jS.getJSONObject("p").getLong("userid");
//		String str = jS.getJSONObject("p").getString("data");
//		
//		UserDB uTDB = getUserDBBySql(userid);
//		if(uTDB != null && uTDB._session != null && uTDB._session.isConnected())
//		{
//			JSONObject temp = new JSONObject();
//			temp.put("m",MethodType.SERVER_CHAT_INFORM);
//			JSONObject data = new JSONObject();
//			data.put("data",str);
//			data.put("userid", uDB.userid);
//			temp.put("p", data);
//			uTDB._session.write(temp);
//			data.put("userid", uTDB.userid);
//			uDB._session.write(temp);
//
//		}
//
//	}

//	public void getList(JSONObject jS, UserDB uDB) throws JSONException {
//
//		ArrayList<JSONObject>  nextList = getNextUserListBySFSArray(uDB.userid);
//		
//		JSONObject temp = new JSONObject();
//		temp.put("m",MethodType.SERVER_GETLIST_INFORM);
//		JSONObject data = new JSONObject();
//		data.put("nextList",nextList);
//		data.put("user",uDB.getJson());
//		if(uDB.parentId > 0)
//		{
//			UserDB pDB = getUserDBBySql(uDB.parentId);
//			if(pDB != null)
//				data.put("parent", pDB.getJson());	
//			
//		}
//		temp.put("p", data);		
//		uDB._session.write(temp);
//		
//		boradLink(uDB);
//	}
	
	
	

}
