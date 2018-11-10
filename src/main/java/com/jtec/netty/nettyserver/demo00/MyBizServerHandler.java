package com.jtec.netty.nettyserver.demo00;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * jtecnettystudy MyBizServerHandler
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/10
 * @Version v1.0
 * @ModificationHistory
 */
public class MyBizServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("my biz handler channel active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server receive message:" + msg);
        ctx.channel().writeAndFlush("accept message " + msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("get exception:" + cause.getMessage());
    }
}
