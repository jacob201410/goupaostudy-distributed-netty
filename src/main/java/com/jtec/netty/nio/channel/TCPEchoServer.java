package com.jtec.netty.nio.channel;

import com.jtec.netty.nio.buffer.Buffers;

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
 * jtecnettystudy TCPEchoServer
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/6
 * @Version v1.0
 * @ModificationHistory
 */
public class TCPEchoServer implements Runnable {

    private InetSocketAddress localAddress;

    public TCPEchoServer(int port) throws IOException {
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

        try {
            // 自旋
            while(!Thread.currentThread().isInterrupted()) {
                int n = selector.select();
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
                    try {
                        // 事件驱动：对链接事件
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);

                            int interestSet = SelectionKey.OP_READ;
                            socketChannel.register(selector, interestSet, new Buffers(256,256));
                            System.out.println("accept from " + socketChannel.getRemoteAddress());
                        }
                        // 事件驱动：对可读事件
                        if (key.isReadable()) {
                            Buffers buffers = (Buffers) key.attachment();
                            ByteBuffer readBuf = buffers.getReadBuffer();
                            ByteBuffer writeBuf = buffers.getWriteBuffer();
                            SocketChannel socketChannel = (SocketChannel) key.channel();

                            socketChannel.read(readBuf);
                            readBuf.flip();

                            CharBuffer charBuf = charset.decode(readBuf);
                            System.out.println(charBuf.array());
                            readBuf.rewind();

                            writeBuf.put("echo from service: ".getBytes(charset.name()));
                            writeBuf.put(readBuf);

                            // 服务端完成读操作
                            readBuf.clear();

                            int interestOps = key.interestOps() | SelectionKey.OP_WRITE;
                            key.interestOps(interestOps);
                        }

                        // 事件驱动：对可写事件
                        if (key.isWritable()) {
                            Buffers buffers = (Buffers) key.attachment();
                            ByteBuffer writeBuf = buffers.getWriteBuffer();
                            writeBuf.flip();
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            int length = 0;
                            while(writeBuf.hasRemaining()) {
                                length = socketChannel.write(writeBuf);
                                if (length == 0) {
                                    break;
                                }
                            }
                            writeBuf.compact();
                            if(length != 0) {
                                key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                            }
                        }
                    } catch (IOException e) {
                        finalCloseKey(key);
                    }
                }
                Thread.sleep(random.nextInt(500));
            }
        } catch(InterruptedException e) {
            System.out.println("serverThread is interrupted");
        } catch(IOException e1) {
            System.out.println("serverThread selecotr error");
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                System.out.println("selector close has an exception:" + e.getMessage());
            } finally {
                System.out.println("Server close..");
            }
        }
    }

    private static void finalCloseKey(SelectionKey key) {
        if (key != null) {
            key.cancel();
            try {
                key.channel().close();
            } catch (IOException e) {
                System.out.println("key.channel().close has an exception:" + e.getMessage());
            }
        }
    }
}
