package com.jtec.netty.nio.ReactorDemo01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * jtecnettystudy BioServer
 *
 * @Description TODO
 * @Author Jacob.Eli.George
 * @Date 2018/11/25
 * @Version v1.0
 * @ModificationHistory
 */
public class BioServer implements Runnable {

    private final static String SERVER_HOST = "127.0.0.1";

    private final static int PORT = 7777;

    @Override
    public void run() {
        System.out.println("BIO server started.");
        ServerSocket serverSocket = null;
        try {
            // 创建ServerSocket监听端口7777上的事件请求
            serverSocket = new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("can not listen to:" + e);
        }
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error" + e);
        }
        String line;
        BufferedReader is = null;
        try {
            // 从socket获取输入流，并构造相应的BufferedReader对象
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Client:" + is.readLine());
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            line = "hello";
//            while (!line.equals("bye")) {
                // 如果该字符串为"bye",
                os.println(line);
                os.flush();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
