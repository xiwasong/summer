package cn.hn.java.summer.socket.command.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.exception.CodeBusinessException;
import cn.hn.java.summer.socket.config.ProtocolMessage;

public class ByteDecoder extends MessageToMessageDecoder<byte[]> {
	private Log logger=LogFactory.getLog(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws CodeBusinessException {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();
		logger.info("clientIP:"+clientIP);
    	logger.info("receive data:\n"+new String(msg));
    	ProtocolMessage pm=ProtocolMessage.decodeFromBytes(msg);
    	out.add(pm);
    }
}
