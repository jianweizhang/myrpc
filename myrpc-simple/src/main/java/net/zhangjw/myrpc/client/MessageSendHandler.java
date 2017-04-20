package net.zhangjw.myrpc.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zhangjw.myrpc.common.MessageRequest;
import net.zhangjw.myrpc.common.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;


public class MessageSendHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(MessageSendHandler.class);

    private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<>();

    volatile private Channel channel;
    private SocketAddress remoteAddr;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemoteAddr() {
        return remoteAddr;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remoteAddr = this.channel.remoteAddress();
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("MessageSendHandler------------" + msg);
        MessageResponse response = (MessageResponse) msg;

        String messageId = response.getMessageId();

        MessageCallBack callBack = mapCallBack.get(messageId);
        if (callBack != null) {
            mapCallBack.remove(messageId);
            callBack.over(response);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("MessageSendHandler: " + cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public MessageCallBack sendRequest(MessageRequest request) {
        MessageCallBack callBack = new MessageCallBack(request);

        mapCallBack.put(request.getMessageId(), callBack);

        channel.writeAndFlush(request);
        return callBack;
    }

    public void removeFromCallBackMap(String messageId){
        mapCallBack.remove(messageId);
    }
}
