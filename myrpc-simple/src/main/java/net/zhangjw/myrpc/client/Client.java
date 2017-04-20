package net.zhangjw.myrpc.client;

import com.google.common.net.HostAndPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.zhangjw.myrpc.common.IdleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

/**
 * Created on 2016/11/10.
 */
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    volatile private static Client client;
    private final static int parallel = 4;
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static MessageSendHandler messageSendHandler = null;


    public static Client getInstance() {
        if (client == null) {
            synchronized (Client.class) {
                if (client == null) {
                    client = new Client();
                }
            }
        }
        return client;
    }

    public void start(String address) {
        HostAndPort hostAndPort = HostAndPort.fromString(address);
        final InetSocketAddress remoteAddr = new InetSocketAddress(hostAndPort.getHostText(), hostAndPort.getPort());

        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

//                pipeline.addLast("idle", new IdleHandler());

                pipeline.addLast(new MessageSendHandler());
            }
        });

        ChannelFuture channelFuture = b.connect(remoteAddr);

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    messageSendHandler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
                    logger.info("Netty RPC Client start success!");
                }
            }
        });
    }

    MessageSendHandler getMessageSendHandler() throws InterruptedException {
        return messageSendHandler;
    }
}
