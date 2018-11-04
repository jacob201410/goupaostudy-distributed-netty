package com.jtec.netty.bio;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
@Slf4j
public class BIOServer {

    // default port
    private static int DEFAULT_PORT = 7777;

    // singleton for serverSocket
    private static ServerSocket serverSocket;

    //
    public static void start() throws IOException {
        //
        start(DEFAULT_PORT);
    }

    //
    public synchronized static void start(int port) throws IOException {
        // 如果已经有实例存在，则保持单例原则，不再创建实例
        if (serverSocket != null) {
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server startup on port:" + port);

            // 自旋：只要有就socket就可以创建一个线程进行处理
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            System.out.println("socket process failed and has exception:" + e);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("server closed.");
                // for GC
                serverSocket = null;
            }
        }
    }
}
