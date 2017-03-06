package com.owen.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.base.http.HttpSerHandler;
import com.owen.base.http.core.HttpServerProtocolCodecFactory;
import com.owen.conf.ServerConfig;
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
		
		loadSystemConfig();
		ExAcceptorParams ep = new ExAcceptorParams(); 
		ep.setBinaryFormat(BinaryFormat.UTF8); 
		ep.setDataFormat(DataFormat.JSON); 
		ep.setPolicyFormat(PolicyFormat.WEB_SOCKET);

		ep.setBindPort(ServerConfig.Port); 
		
		ep.setAliveInterval(30);
		ep.setAliveTimeout(300);
		ep.setHandler(new SessionHandler());
		ep.setPassiveMode(false); 
		new ExAcceptorContainer(ep); 
		
		logger.info("聊天服务启动绑定端口"+ServerConfig.Port);
		
		
		try {
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getFilterChain().addLast(
					"protocolFilter",
					new ProtocolCodecFilter(
							new HttpServerProtocolCodecFactory()));
			acceptor.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newCachedThreadPool())); // 加入 ExecutorFilter，增强性能

			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			acceptor.setHandler(new HttpSerHandler());	
			acceptor.bind(getHttpAddressList());
			logger.info("[main] http端口启动成功---"+getHttpAddressList().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		
//		ChatTimeServer servReplay = new ChatTimeServer();
//        Thread threadReplay = new Thread(servReplay, "Chat Update Service");
//        threadReplay.start();
	}
	/**
	 * 获取服务器HttpIP数组
	 * @return Http ip数组
	 * */
	public static ArrayList<InetSocketAddress> getHttpAddressList(){
		ArrayList<InetSocketAddress> arr = new ArrayList<InetSocketAddress>();
		for(int i = 0;i<1;i++)
		{
			InetSocketAddress iA = new InetSocketAddress("0.0.0.0", ServerConfig.HttpPort);
			arr.add(iA);
			
		}
		return arr;
	} 
	
	/**加载系统配置文件*/
	private static void loadSystemConfig() {
		// TODO Auto-generated method stub
        Properties po = getProperties("/config.properties");
     
        ServerConfig.RedisAddress = po.getProperty("RedisAddress");
        ServerConfig.RedisPort = Integer.parseInt(po.getProperty("RedisPort"));
        ServerConfig.Port = Integer.parseInt(po.getProperty("Port"));
        ServerConfig.HttpPort = Integer.parseInt(po.getProperty("HttpPort"));
        ServerConfig.RedisPassword = po.getProperty("RedisPassword");

    	
    	
    	
	}


	/** 获取指定名称的配置文件
	 * @param refPropertiesName 配置文件名称（在 conf 目录下）
	 * @return 
	 */
	public static Properties getProperties(String refPropertiesName) {
		Properties curRtn = null;
		String curName = System.getProperties().getProperty("user.dir")
			+ System.getProperties().getProperty("file.separator") + "conf" // 都配置在 conf 目录下
			+ refPropertiesName;
		try {
			FileInputStream curIS = new FileInputStream(curName);
			if (curIS != null) {
				curRtn = new Properties();
				curRtn.load(curIS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			curRtn = null;
		}
		return curRtn;
	}
	
	
}
