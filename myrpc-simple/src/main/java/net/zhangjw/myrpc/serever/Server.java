package net.zhangjw.myrpc.serever;

import com.google.common.net.HostAndPort;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import net.zhangjw.myrpc.common.IdleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2016/11/9.
 */
public class Server {
    private Logger logger = LoggerFactory.getLogger(Server.class);

    private static ConcurrentHashMap<String, Object> handerMap = new ConcurrentHashMap<>();
    private static Server server;

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public ConcurrentHashMap<String, Object> getHanderMap() {
        return handerMap;
    }

    public void start(final String address) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadFactory threadRpcFactory = new DefaultThreadFactory("my rpc");

                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup(4, threadRpcFactory, SelectorProvider.provider());

                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
//                                pipeline.addLast("ping", new IdleStateHandler(60, 15, 8, TimeUnit.SECONDS));//心跳
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                pipeline.addLast(new LengthFieldPrepender(4));
                                pipeline.addLast(new ObjectEncoder());
                                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

//                                pipeline.addLast("idle", new IdleHandler());

                                pipeline.addLast(new MessageRecvHandler(handerMap));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                HostAndPort hostAndPort = HostAndPort.fromString(address);
                try {
                    ChannelFuture future = bootstrap.bind(hostAndPort.getPort()).sync();
                    logger.info("Netty RPC Server start success!");
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
