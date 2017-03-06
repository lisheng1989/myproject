package com.owen.main;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.chat.UserDB;
import com.owen.chat.UserMgr;
import com.owen.message.MethodType;





public class SessionHandler extends IoHandlerAdapter {

	static Logger logger = LoggerFactory.getLogger(SessionHandler.class);

	
	private UserMgr userHandler = UserMgr.getInstance();

	/** 连接新建响应
	 * @param session 新建的连接Session
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("[ sessionOpened ] : 连接 " + session.getId() + " 准备创建");
	}
	
	/** 连接关闭响应
	 * @param session 即将关闭的连接Session
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("[ sessionOpened ] : 连接 " + session.getId() + " 准备关闭");
		if(session.containsAttribute("userId") == false)
			return;
		long userId = (Long) session.getAttribute("userId");
		UserDB uDB = userHandler.getUserDBByUserid(userId);
		if(uDB != null)
		{
			
			uDB.isOnline = 0;
			uDB._session = null;
			userHandler.boradLink(uDB);	
		}
		
	}
	
	/** 收到信息的动作
	 * @param session 对应的 IoSession
	 * @param message 收到的信息
	 */
	@Override
	public void messageReceived(IoSession session, Object params) throws Exception {
	
		JSONObject jS = (JSONObject)(params);
		if(jS.isNull("m"))
			return;
		int mT = jS.getInt("m");
		logger.info("[messageReceived]收到消息:"+jS.toString());
		if(mT == MethodType.CLIENT_LOGIN)
		{
			long userid = jS.getJSONObject("p").getLong("userId");
			userHandler.createUserData(userid,session);
			
		}else {
			if(session.containsAttribute("userId"))
			{
				long userId = (Long) session.getAttribute("userId");
				UserDB uDB = userHandler.getUserDBByUserid(userId);
				switch (mT) {
				case MethodType.CLIENT_CHAT : // 如果是用户型终端
					userHandler.chat(jS,uDB);	
					break;
				case MethodType.CLIENT_GETLIST : // 如果是设备型终端
					userHandler.getList(jS,uDB);
					break;
				default : // 其他类型终端
					logger.warn("[ process ] : 连接 " + session.getId() +mT+ " 未标记任何终端属性");

				}

			}else{
				logger.warn("[ process ] : 连接 " + session.getId() + " 未建立");

			}
			
		}

	 
	}
	
	/** 连接错误响应
	 * @param session 对应的 IoSession
	 * @param cause 错误原因
	 */
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.info("[ exceptionCaught ] : 连接 " + session.getId()+"通信过程中出现错误:[" + cause.getMessage() + "]" + " 异常断开（可能是未完成正常退出，或网络故障所致）");
		
	}
}
