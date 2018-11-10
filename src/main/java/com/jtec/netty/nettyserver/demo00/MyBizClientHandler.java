package com.jtec.netty.nettyserver.demo00;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * jtecnettystudy MyBizClientHandler
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/10
 * @Version v1.0
 * @ModificationHistory
 */
public class MyBizClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client receive message :" + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("get exception:" + cause.getMessage());
    }
}
