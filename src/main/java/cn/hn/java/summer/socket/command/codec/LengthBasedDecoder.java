package cn.hn.java.summer.socket.command.codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import cn.hn.java.summer.socket.config.IProtocolMessage;

/**
 * 
 * @author sjg
 * 2016年12月22日 下午3:37:37
 *
 */
public class LengthBasedDecoder extends LengthFieldBasedFrameDecoder {
    private static final int MAX_FRAME_LENGTH = 99999;
    private static final int LENGTH_FIELD_OFFSET = IProtocolMessage.CONNECTTYPE_LENGTH+IProtocolMessage.COMMANDNAME_LENGTH;
    private static final int LENGTH_FIELD_LENGTH = IProtocolMessage.DATALENGTH_LENGTH;

    public LengthBasedDecoder() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        return Long.parseLong(buf.getCharSequence(offset, length, StandardCharsets.UTF_8).toString());
    }
}
