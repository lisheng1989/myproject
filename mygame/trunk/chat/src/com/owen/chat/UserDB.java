package com.owen.chat;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;




public class UserDB {
	
	
	public UserDB(long _uid, String _name, long _pind) {
		userid = _uid;
		name = _name;
		parentId = _pind;
	
	}
	public long userid;
	public String name;
	public int isOnline;
	public long parentId;
	public IoSession _session;
	public ArrayList<Long> nextList = new ArrayList<Long>();
	public JSONObject getJson() {
		JSONObject jS = new JSONObject();
		try {
			jS.put("userid", userid);
			jS.put("parentId", parentId);
			jS.put("name", name);
			jS.put("isOnline", isOnline);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return jS;
	}
}
