package cn.hn.java.summer.socket.command.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import cn.hn.java.summer.socket.config.ProtocolMessage;

public class ByteEncoder extends MessageToByteEncoder<ProtocolMessage>{

	@Override
	protected void encode(ChannelHandlerContext ctx, ProtocolMessage msg,
			ByteBuf out) throws Exception {
		out.writeBytes(ProtocolMessage.encodeFromMsg(msg));
	}

}
