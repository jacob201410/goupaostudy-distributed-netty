package com.jtec.netty.nio.ReactorDemo01;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * jtecnettystudy ReactorTest
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/25
 * @Version v1.0
 * @ModificationHistory
 */
public class ReactorTest {

    @Test
    public void testConnect() throws Exception {
        Socket socket = new Socket("127.0.0.1", 7777);
        System.out.println("连接成功!");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // NIO下，不关闭客户端连接socket，服务器端可以收到
        {
//            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
//            printWriter.println("Hi");
        }
        // 必须关闭BIO连接socket，服务才能收到
        {
            socket.getOutputStream().write(new byte[]{'H','i'});
            socket.getOutputStream().flush();
            socket.close();
        }
        byte[] bufArr = new byte[2048];
        System.out.println("准备读取数据~~");

//        while (true) {
//            try {
//                int count = socket.getInputStream().read(bufArr);
//                System.out.println("NIO 方式读取数据:" + new String(bufArr) + " count = " + count);
//                Thread.sleep(1000L);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        String readFromBioServer = bufferedReader.readLine();
        System.out.println("BIO 方式读取数据:" + readFromBioServer);

    }

    @Test
    public void testNioServer() {
        Thread server = new Thread(new Reactor());
        server.start();

        while (true) {
            try {
                Thread.sleep(3000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBioServer() {
        Thread bioServer = new Thread(new BioServer());
        bioServer.start();
        while (true) {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
