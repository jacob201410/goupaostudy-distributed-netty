package com.jtec.netty.nio.channel;

import com.jtec.netty.nio.buffer.Buffers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * jtecnettystudy TCPEchoClient
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/4
 * @Version v1.0
 * @ModificationHistory
 */
public class ClientSocketChannelDemo {

    public static void main(String[] args) throws InterruptedException {
        String REMOTE_HOST = "127.0.0.1";
        final int REMOTE_PORT = 8080;
        InetSocketAddress remoteAddress = new InetSocketAddress(REMOTE_HOST, REMOTE_PORT);

        Thread thdA = new Thread(new TCPEchoClient("Thread-A", remoteAddress));
        Thread thdB = new Thread(new TCPEchoClient("Thread-B", remoteAddress));
        Thread thdC = new Thread(new TCPEchoClient("Thread-C", remoteAddress));
        Thread thdD = new Thread(new TCPEchoClient("Thread-D", remoteAddress));

        thdA.start();
        thdB.start();
        Thread.sleep(5000L);

        thdA.interrupt();
        thdC.start();
        Thread.sleep(5000L);
        thdC.interrupt();
        thdD.start();
    }
}
