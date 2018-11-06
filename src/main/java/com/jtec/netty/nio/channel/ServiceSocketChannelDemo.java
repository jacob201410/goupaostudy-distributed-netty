package com.jtec.netty.nio.channel;

import java.io.IOException;

/**
 * jtecnettystudy ServiceSocketChannelDemo
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/4
 * @Version v1.0
 * @ModificationHistory
 */
public class ServiceSocketChannelDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 启动服务端接收事件处理
        Thread thd = new Thread(new TCPEchoServer(8080));
        thd.start();
        Thread.sleep(1000L * 60L);
        thd.interrupt();
    }

}
