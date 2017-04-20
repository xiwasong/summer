package cn.hn.java.summer.socket.command;

import org.springframework.stereotype.Component;

import cn.hn.java.summer.socket.command.codec.ICmdDataDecoder;
import cn.hn.java.summer.socket.command.codec.ICmdDataEncoder;
import cn.hn.java.summer.socket.command.codec.JsonDataDecoder;
import cn.hn.java.summer.socket.command.codec.JsonDataEncoder;

@Component
public class JsonCommandHandler extends AbstractCommandHandler {
	
	JsonDataDecoder jsonDataDecoder=new JsonDataDecoder();
	
	JsonDataEncoder jsonDataEncoder=new JsonDataEncoder();

	@Override
	public ICmdDataDecoder getDataDecoder() {
		return jsonDataDecoder;
	}

	@Override
	public ICmdDataEncoder getDataEncoder() {
		return jsonDataEncoder;
	}

}
