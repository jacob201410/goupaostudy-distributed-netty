package com.jtec.netty.bio;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class ServerHandler implements Runnable {

    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            String expression;
            int result;
            String talkRoomMsg;
            while (true) {
                if ((expression = inputStream.readLine()) == null) {
                    break;
                }
                System.out.println("Server received info:" + expression);
//                result = Calculator.cal(expression);
//                outputStream.println(result);
                talkRoomMsg = SayHi.justHi(expression);
                outputStream.println(talkRoomMsg);
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Server run has an exception:" + e.getMessage());
        } finally {
            Client.letMeCloseIOStream(inputStream, outputStream, socket);
        }


    }
}
