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

        Charset cs = Charset.forName("UTF-8");
        Selector selector = null;
        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            selector = Selector.open();
            int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            sc.register(selector, interestSet, new Buffers(256, 256));
            sc.connect(remoteAddress);
            while(!sc.finishConnect()) {

            }
        } catch (IOException e) {
            System.out.println("client connect failed");
            return;
        }

        try {
            int i = 1;
            while(!Thread.currentThread().isInterrupted()) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> ki = keySet.iterator();
                while(ki.hasNext()) {
                    SelectionKey key = ki.next();
                    ki.remove();
                    Buffers buffers = (Buffers) key.attachment();
                    ByteBuffer readBuffer = buffers.getReadBuffer();
                    ByteBuffer writeBuffer = buffers.getWriteBuffer();
                    SocketChannel sc = (SocketChannel) key.channel();
                    if(key.isReadable()) {
                        sc.read(readBuffer);
                        readBuffer.flip();
                        CharBuffer cb = cs.decode(readBuffer);
                        System.out.println(cb.array());
                        readBuffer.clear();
                    }
                    if(key.isWritable()) {
                        writeBuffer.put((name + " - " + i).getBytes("UTF-8"));
                        writeBuffer.flip();
                        sc.write(writeBuffer);
                        writeBuffer.clear();
                        i++;
                    }
                }
                Thread.sleep(1000 + random.nextInt(1000));
            }
        } catch(InterruptedException e){
            System.out.println(name + " is interrupted");
        } catch(IOException e) {
            System.out.println(name + " encounter a connect error");
        } finally {
            try {
                selector.close();
            } catch (IOException e1) {
                System.out.println(name + " close selector failed");
            } finally {
                System.out.println(name + "  closed");
            }
        }

    }

}
