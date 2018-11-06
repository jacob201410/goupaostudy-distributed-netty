package com.jtec.netty.nio.channel;

import com.jtec.netty.nio.buffer.Buffers;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

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
    public static class TCPEchoServer implements Runnable {

        private InetSocketAddress localAddress;

        public TCPEchoServer(int port) throws IIOException {
            this.localAddress = new InetSocketAddress(port);
        }

        @Override
        public void run() {
            Charset charset = Charset.forName("UTF-8");
            ServerSocketChannel serverSocketChannel = null;
            Selector selector = null;
            Random random = new Random();

            try {
                // create selector instance
                selector = Selector.open();
                // create sevice channel and set config to none blocking
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                // set connect max counts
                serverSocketChannel.bind(localAddress, 100);
                // tcp
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            } catch (IOException e) {
                System.out.println("server start failed");
                return;
            }
            System.out.println("server start with address: " + localAddress);

            while(!Thread.currentThread().isInterrupted()) {
                int n = 0;
                try {
                    n = selector.select();
                } catch (IOException e) {
                    System.out.println("selector select() invoke has an exception:" + e.getMessage());
                }
                if (n == 0) {
                    continue;
                }
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                // 循环获取selector
                SelectionKey key = null;
                while(iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    // 事件驱动：对链接事件
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = null;
                        try {
                            socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                        } catch (IOException e) {
                            System.out.println("serverSocketChannel.accept has an exception:" + e.getMessage());
                        }

                        try {
                            serverSocketChannel.register(selector, SelectionKey.OP_READ, new Buffers(256,256));
                        } catch (ClosedChannelException e) {
                            System.out.println("serverSocketChannel.register has an exception:" + e.getMessage());
                        }
                        try {
                            System.out.println("accept from " + socketChannel.getRemoteAddress());
                        } catch (IOException e) {
                            System.out.println("socketChannel.getRemoteAddress has an exception:" + e.getMessage());
                        }
                    }
                    // 事件驱动：对可读事件
                    if (key.isReadable()) {
                        Buffers buffers = (Buffers) key.attachment();
                        ByteBuffer readBuf = buffers.getReadBuffer();
                        ByteBuffer writeBuf = buffers.getWriteBuffer();
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        try {
                            socketChannel.read(readBuf);
                        } catch (IOException e) {
                            System.out.println("socketChannel.read has an exception: " + e.getMessage());
                        }
                        readBuf.flip();

                        CharBuffer charBuf = charset.decode(readBuf);
                        System.out.println("chrBuf input:" + charBuf.array());

                        try {
                            writeBuf.put("echo from service: ".getBytes(charset.name()));
                            writeBuf.put(writeBuf);
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("writeBuf.put has an exception: " + e.getMessage());
                        }

                        // 服务端完成读操作
                        readBuf.clear();
                        int interestOps = key.interestOps() | SelectionKey.OP_WRITE;
                        System.out.println("service interestOps" + interestOps);
                        key.interestOps(interestOps);
                    }

                    // 事件驱动：对可写事件
                    if (key.isWritable()) {
                        Buffers buffers = (Buffers) key.attachment();
                        ByteBuffer writeBuf = buffers.getWriteBuffer();
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        writeBuf.flip();
                        int length = 0;
                        while(writeBuf.hasRemaining()) {
                            try {
                                length = socketChannel.write(writeBuf);
                            } catch (IOException e) {
                                System.out.println("socketChannel.write has an exception:" + e.getMessage());
                            }
                            if (length == 0) {
                                break;
                            } else {
                                writeBuf.compact();
                                key.interestOps(key.interestOps() & SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
                // final execute
                if (key != null) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        System.out.println("key.channel().close has an exception:" + e.getMessage());
                    }
                }
            }
            try {
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    System.out.println("selector close has an exception:" + e.getMessage());
                } finally {
                    System.out.println("Server close..");
                }
            }
        }
    }

    public static void main(String[] args) throws IIOException {
        // 启动服务端接收事件处理
        Thread thd = new Thread(new TCPEchoServer(8080));
        thd.start();
        try {
            Thread.sleep(1000L * 60L * 10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thd.interrupt();
    }

}
