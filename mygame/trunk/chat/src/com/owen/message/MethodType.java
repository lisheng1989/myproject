package com.owen.message;

/**
 * @ClassName MethodType
 * @Description 命令类型
 * @author Sammy Zhong
 * @date 2012-06-13
 * @version 1.0.0
 */
public class MethodType {
	
	
	// ------------------------------------------------------------------ 登陆
	/** 客户端登录 */
	public static final int CLIENT_LOGIN = 0;
	
	/** 服务端对于客户端登陆的回复 */
	public static final int SERVER_LOGIN_REPLY = 1000;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int SERVER_LOGIN_INFORM = 2000;
	
	
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int SERVER_NEWADD_INFORM = 2001;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int SERVER_NEWREMOVE_INFORM = 2002;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int CLIENT_CHAT = 3;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int SERVER_CHAT_INFORM = 2003;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int CLIENT_GETLIST = 4;
	
	/** 服务端对于其他客户端登陆的通知 */
	public static final int SERVER_GETLIST_INFORM = 2004;
	
}
