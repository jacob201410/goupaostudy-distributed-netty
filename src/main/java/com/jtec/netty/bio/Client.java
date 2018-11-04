package com.jtec.netty.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class Client {

    private static int DEFAULT_SERVER_PORT = 7777;

    private static String DEFAULT_SERVER_IP = "127.0.0.1";

    public static void send(String expression) {

        send(DEFAULT_SERVER_PORT, expression);
    }

    private static void send(int port, String expression) {

        log.info("expression: " + expression);
        Socket socket = null;
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;

        try {
            socket = new Socket(DEFAULT_SERVER_IP, port);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(expression);
            System.out.println("read result:" + inputStream.readLine());
        } catch (IOException e) {
            System.out.println("send has an exception:" + e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    System.out.println("inputStream close has an exception:" + e);
                }
                // for GC
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("socket close has an exception:" + e);
                }
                socket = null;
            }
        }

    }


}