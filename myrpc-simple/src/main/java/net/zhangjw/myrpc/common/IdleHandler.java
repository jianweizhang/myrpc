package net.zhangjw.myrpc.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(IdleHandler.class);


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("IdleHandler------------" + ctx.channel().remoteAddress() + " Say : " + msg);

        if ("ping".equals(msg.toString())) {
            ctx.channel().writeAndFlush("OK");
        } else if (!"OK".equals(msg.toString())){
            ctx.fireChannelRead(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    //心跳检测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                logger.info("IdleHandler------------" + "READER_IDLE");
                // 超时关闭channel
                ctx.close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                logger.info("IdleHandler------------" + "WRITER_IDLE");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                logger.info("IdleHandler------------" + "ALL_IDLE");
                // 发送心跳
                ctx.channel().writeAndFlush("ping");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}