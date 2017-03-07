package com.owen.base.http;



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.base.http.core.HttpHandler;
import com.owen.base.http.core.HttpRequestMessage;
import com.owen.base.http.core.HttpResponseMessage;
import com.owen.chat.UserMgr;
import com.owen.conf.HttpMethod;






public class HttpSerHandler  extends IoHandlerAdapter{
    private HttpHandler handler;  

    public HttpHandler getHandler() {  
        return handler;  
    }  
  
    public void setHandler(HttpHandler handler) {  
        this.handler = handler;  
    }  
    
    	
    
	static Logger logger = LoggerFactory.getLogger(HttpSerHandler.class);

	/** 连接新建响应
	 * @param session 新建的连接Session
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("[ sessionOpened ] : 连接 " + session.getId() + " 准备创建");
		// set idle time to 60 seconds
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
	}
	
	/** 连接关闭响应
	 * @param session 即将关闭的连接Session
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("[ sessionOpened ] : 连接 " + session.getId() + " 准备关闭");
	}
	
	/** 收到信息的动作
	 * @param session 对应的 IoSession
	 * @param message 收到的信息
	 */
	@Override
	public void messageReceived(IoSession session, Object params)  {
		HttpRequestMessage request = (HttpRequestMessage) params;
		if(request != null)
		{	
			
			String str = request.getParameter("data");
		
			str = URLDecoder.decode(str);
			

			if(str != null && str.equals("") == false)
			{
				JSONObject jS;
				try {
					jS = new JSONObject(str);//接受到的数据
					logger.warn("[messageReceived]"+str);		
					HttpResponseMessage response = new HttpResponseMessage();
				//	response.appendBody("success");
					response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
					if(jS.has("m"))
					{
						int method = jS.getInt("m");
						if(method == HttpMethod.getOnline)
						{
							JSONObject obj = new JSONObject();
							obj.put("number", UserMgr.getInstance().getOnlineSize());
							response.appendBody(obj.toString());
						}else{
							if(UserMgr.getInstance().handerMsg(jS))
							{
								response.appendBody("true");
							}else{
								response.appendBody("false");							
							};	
						}
						
					}else{
						logger.info("[ messageReceived ] : 接收到错误消息"+str);	
						response.appendBody("false");

					}
					session.write(response).addListener(IoFutureListener.CLOSE);  
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("[ messageReceived ] : 数据格式类型错误"+str);	
				}
				
				
			}else{
				HttpResponseMessage response = new HttpResponseMessage();
				response.appendBody("success");
				response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
				session.write(response).addListener(IoFutureListener.CLOSE);  

			}
		}else{
			HttpResponseMessage response = new HttpResponseMessage();
			response.appendBody("fail");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
			session.write(response).addListener(IoFutureListener.CLOSE);  

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
