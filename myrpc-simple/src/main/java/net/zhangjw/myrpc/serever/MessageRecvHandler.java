package net.zhangjw.myrpc.serever;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zhangjw.myrpc.common.MessageRequest;
import net.zhangjw.myrpc.common.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MessageRecvHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(MessageRecvHandler.class);


    private final Map<String, Object> handlerMap;

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        logger.info("MessageRecvHandler------------" + msg + "------------");
        final MessageRequest request = (MessageRequest) msg;

        MessageResponse response = new MessageResponse();

        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Object o = MethodUtils.invokeMethod(serviceBean, methodName, parameters);

        logger.info(o.toString());

        response.setMessageId(request.getMessageId());
        response.setResult(o);
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                logger.info("RPC Server Send message-id respone:" + request.getMessageId());
            }
        });
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught: " + cause);
        ctx.close();
    }
}