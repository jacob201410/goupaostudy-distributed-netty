package com.jtec.netty.nettyserver.demo00;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * jtecnettystudy NettyClient
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/10
 * @Version v1.0
 * @ModificationHistory
 */
public class NettyClient implements Runnable {

    private static final String REMOTE_IP = "127.0.0.1";
    private static final int REMOTE_PORT = 6666;

    private static final int INIT_OFFSET = 0;
    private static final int CORE_POWER_NUMBER = 2 * 2;

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, INIT_OFFSET,CORE_POWER_NUMBER,INIT_OFFSET,CORE_POWER_NUMBER));
                            pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            // 最后加载入自己的业务handler
                            pipeline.addLast("handler", new MyBizClientHandler());
                        }
                    });
            for (int i = 0; i < 1000; i++) {
                ChannelFuture future = bootstrap.connect(REMOTE_IP, REMOTE_PORT).sync();
                future.channel().writeAndFlush("Hi, NettyServer, this is " + Thread.currentThread().getName() + " - " + i);
            }

        } catch (Exception e) {
            System.out.println("exception : " + e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new NettyClient() + " thread name " + i).start();
        }
    }
}
