package com.owen.base.http.core;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
/**
 * @author owen
 * */
public class HttpServerProtocolCodecFactory implements ProtocolCodecFactory {
	
	/** 调用的编码类 */
	private ProtocolEncoder encoder;
	
	/** 调用的解码类 */
	private ProtocolDecoder decoder;
	
	public HttpServerProtocolCodecFactory() {
//		super.addMessageDecoder(HttpRequestDecoder.class);
//		super.addMessageEncoder(HttpResponseMessage.class,
//				HttpResponseEncoder.class);
		this.encoder = new HttpResponseEncoder();
		this.decoder = new HttpRequestDecoder();
		
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}

}
