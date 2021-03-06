package com.jtec.netty.nettyserver.demo00;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * jtecnettystudy NettyServer
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/10
 * @Version v1.0
 * @ModificationHistory
 */
public class NettyServer {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 6666;

    private static final int BIZ_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZ_THREAD_SIZE = 100;

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZ_GROUP_SIZE);
    private static final EventLoopGroup workGroup = new NioEventLoopGroup(BIZ_THREAD_SIZE);

    private static final int INIT_OFFSET = 0;
    private static final int INT_VALUE_FOUR = 4;

    public static void start() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<Channel>() {
            // 核心初始化流程
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                        INIT_OFFSET,INT_VALUE_FOUR,INIT_OFFSET,INT_VALUE_FOUR));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                // 最后加载入自己的业务handler
                pipeline.addLast(new MyBizServerHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(IP, PORT).sync();
        channelFuture.channel().closeFuture().sync();
        System.out.println("server started.");
    }

    protected static void shutdown() {
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Netty Server startup...");
        NettyServer.start();
    }

}
