package com.owen.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.socket.mina2.codec.BinaryFormat;
import com.owen.socket.mina2.codec.DataFormat;
import com.owen.socket.mina2.codec.PolicyFormat;
import com.owen.socket.mina2.nio.ExAcceptorContainer;
import com.owen.socket.mina2.nio.ExAcceptorParams;




public class ChatServer {
	static Logger logger = LoggerFactory.getLogger(ChatServer.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		ExAcceptorParams ep = new ExAcceptorParams(); 
		ep.setBinaryFormat(BinaryFormat.UTF8); 
		ep.setDataFormat(DataFormat.JSON); 
		ep.setPolicyFormat(PolicyFormat.WEB_SOCKET);

		ep.setBindPort(8080); 
		
		ep.setAliveInterval(30);
		ep.setAliveTimeout(300);
		ep.setHandler(new SessionHandler());
		ep.setPassiveMode(false); 
		new ExAcceptorContainer(ep); 
		
		logger.info("聊天服务启动绑定端口8080");
		
//		ChatTimeServer servReplay = new ChatTimeServer();
//        Thread threadReplay = new Thread(servReplay, "Chat Update Service");
//        threadReplay.start();
	}
}
