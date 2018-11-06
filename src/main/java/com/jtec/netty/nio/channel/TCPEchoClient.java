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
public class TCPEchoClient implements Runnable {

    private String name;

    private InetSocketAddress remoteAddress;

    private Random random = new Random();

    public TCPEchoClient(String name, InetSocketAddress remoteAddress) {
        this.name = name;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void run() {

        Charset charset = Charset.forName("UTF-8");

        Selector selector;

        try {
            SocketChannel socketChannel = SocketChannel.open();

            socketChannel.configureBlocking(false);

            selector = Selector.open();
            // 按位或：001 | 100 = 101 = 5
            int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            System.out.println("client interestSet:" + interestSet);
            socketChannel.register(selector, interestSet, new Buffers(256, 256));

            socketChannel.connect(remoteAddress);

            while(!socketChannel.finishConnect()) {

            }

            System.out.println(name + " finished connection.");

        } catch (IOException e) {
            System.out.println("client connect failed and has an exception:" + e.getMessage());
            return;
        }

        int i = 1;
        while(!Thread.currentThread().isInterrupted()) {
            try {
                selector.select();
            } catch (IOException e) {
                System.out.println("selector.select has an exception: " + e.getMessage());
            }

            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();

            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // another channel for next time
                iterator.remove();

                Buffers buffers = (Buffers)key.attachment();
                ByteBuffer readBuf = buffers.getReadBuffer();
                ByteBuffer writeBuf = buffers.getWriteBuffer();

                SocketChannel socketChannel = (SocketChannel) key.channel();

                // event : read
                if(key.isReadable()) {
                    try {
                        socketChannel.read(readBuf);
                    } catch (IOException e) {
                        System.out.println("socketChannel.read has an exception: " + e.getMessage());
                    }
                    readBuf.flip();
                    CharBuffer charBuf = charset.decode(readBuf);
                    System.out.println(charBuf.array());
                    readBuf.clear();
                }

                // event : write
                if (key.isWritable()) {
                    try {
                        writeBuf.put((name + i).getBytes(charset.name()));
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("writeBuf.put has an exception: " + e.getMessage());
                    }
                    writeBuf.flip();

                    try {
                        socketChannel.write(writeBuf);
                    } catch (IOException e) {
                        System.out.println("socketChannel.write has an exception: " + e.getMessage());
                    }

                    writeBuf.clear();
                    i++;
                }
            }
        }
    }

}
