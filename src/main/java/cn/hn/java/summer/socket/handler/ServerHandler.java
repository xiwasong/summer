package cn.hn.java.summer.socket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hn.java.summer.socket.command.LoginIdCommandHandler;
import cn.hn.java.summer.socket.config.IProtocolMessage;

@Component
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = Logger.getLogger(ServerHandler.class.getName());

	static final int LOGINID_LENGTH=32;
	@Autowired
	private LoginIdCommandHandler commandHandler;
	
//    @Autowired
//    private ChannelRepository channelRepository;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*
    	Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");

        ctx.fireChannelActive();
        logger.debug(ctx.channel().remoteAddress());
        String channelKey = ctx.channel().remoteAddress().toString();
        channelRepository.put(channelKey, ctx.channel());

        ctx.writeAndFlush("Your channel key is " + channelKey + "\n\r");

        logger.debug("Binded Channel Count is " + this.channelRepository.size());
        System.out.println("binded: "+this.channelRepository.size());
        */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	try{
	    	if(msg instanceof IProtocolMessage){
				IProtocolMessage proMsg=(IProtocolMessage) msg;
				//无loginId段
				if(proMsg.getLength()==0 || proMsg.getLength()<LOGINID_LENGTH){
					logger.error("Command format is not correct, the data area contains at least loginId!");
					return;
				}
				final Channel channel= ctx.channel();
				//输出调用结果
				ChannelFuture f= channel.writeAndFlush(HandlerProcess.invockCommand(commandHandler, proMsg));
				//短连接
				if(proMsg.getConnectType()==IProtocolMessage.CONNETTYPE_SHORT){
					//输出后关闭通道
					f.addListener(new GenericFutureListener<Future<? super Void>>() {
						@Override
						public void operationComplete(Future<? super Void> future)
								throws Exception {
							channel.close();
						}
					});
				}
				
			}
	
    	}finally{
    		ReferenceCountUtil.release(msg);
    	}

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
    	/*
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx);

        String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);

        logger.debug("Binded Channel Count is " + this.channelRepository.size());
        */
    }

    /*
    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
    */
}