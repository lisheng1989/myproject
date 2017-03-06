package com.owen.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.chat.UserMgr;

public class ChatTimeServer  implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ChatTimeServer.class);

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000); // 绾跨▼浼戠湢 60 绉�
				UserMgr.getInstance().repCount();
				logger.info("[ run ] : 轮询服务");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
