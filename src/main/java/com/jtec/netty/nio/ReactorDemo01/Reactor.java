package com.jtec.netty.nio.ReactorDemo01;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * jtecnettystudy Reactor
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/25
 * @Version v1.0
 * @ModificationHistory
 */
public class Reactor implements Runnable {

    private int id = 100001;

    private final static int bufferSize = 2048;

    @Override
    public void run() {
        init();
    }

    public void init() {
        try {
            // 创建服务端通道与选择器
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 7777);
            socketChannel.socket().bind(inetSocketAddress);
            // 设置通道NIO，注册于选择器之上
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT).attach(id++);
            System.out.println("Server started ... port: 7777." + inetSocketAddress.getAddress());
            listener(selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listener(Selector selector) {
        try {
            // 自旋
            while (true) {
                Thread.sleep(1000L);
                // 阻塞直到就绪事件为止
                selector.select();
                Set<SelectionKey> readySelectionKey = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readySelectionKey.iterator();
                // 遍历selectionKey
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    Object currentSelectionKeyObj = selectionKey.attachment();
                    // 判断当前的selectionKey进入事件
                    if (selectionKey.isAcceptable()) {
                        System.out.println(currentSelectionKeyObj + " - 接收到请求事件.");
                        // 获取通道，接受连接
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        // 设置BIO，注册读写数据事件，有消息触发时才能捕获
                        serverSocketChannel.accept()
                                .configureBlocking(false)
                                .register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE)
                                .attach(id++);
                        System.out.println(currentSelectionKeyObj + " - 已经建立连接.");
                    }

                    // 读取数据
                    if (selectionKey.isReadable()) {
                        System.out.println(currentSelectionKeyObj + " - 读取数据事件.");
                        SocketChannel clientScoketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer receiveBuf = ByteBuffer.allocate(bufferSize);
                        clientScoketChannel.read(receiveBuf);
                        System.out.println(currentSelectionKeyObj + " - 已经读取到数据:" + generateStrForByteBuffer(receiveBuf));
                    }

                    // 写入数据
                    if (selectionKey.isWritable()) {
                        System.out.println(currentSelectionKeyObj + " - 写入数据事件.");
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer sendBuffer = ByteBuffer.allocate(bufferSize);
                        String sendMsg = "Hello!/n";
                        sendBuffer.put(sendMsg.getBytes());
                        // 反转当前的buffer，用于重新定位，
                        // 读写指针指到缓存头部，并且设置了最多只能读出之前写入的数据长度(而不是整个缓存的容量大小)
                        sendBuffer.flip();
                        clientChannel.write(sendBuffer);
                    }

                    if (selectionKey.isConnectable()) {
                        System.out.println(currentSelectionKeyObj + " - 连接事件");
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            System.out.println("Error - " + e.getMessage());
        } finally {
            // TODO STREAM CLOSE
        }
    }

    public static String generateStrForByteBuffer(ByteBuffer buffer) {
        StringBuffer sbf = new StringBuffer(bufferSize);
        try {
            for (int i = 0; i < buffer.position(); i++) {
                sbf.append((char)buffer.get(i));
            }
        } catch (Exception e) {
            System.out.println("generateStringForByteBuffer failed.");
        }
        return sbf.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBufferSize() {
        return bufferSize;
    }

}
