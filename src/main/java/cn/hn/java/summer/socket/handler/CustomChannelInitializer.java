package cn.hn.java.summer.socket.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hn.java.summer.socket.command.codec.ByteDecoder;
import cn.hn.java.summer.socket.command.codec.ByteEncoder;
import cn.hn.java.summer.socket.command.codec.LengthBasedDecoder;

@Component
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ChannelInboundHandlerAdapter somethingServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthBasedDecoder())
        .addLast(new ByteArrayDecoder())
        .addLast(new ByteArrayEncoder())
        .addLast(new ByteDecoder())
        .addLast(new ByteEncoder())
        .addLast(somethingServerHandler);
    }
}