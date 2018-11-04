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
            outputStream = new PrintWriter(socket.getOutputStream());
            String expression;
            String result;
            while (true) {
                if ((expression = inputStream.readLine()) == null) {
                    break;
                }
                log.info("Server received info:", expression);
                result = Calculator.cal(expression);
                outputStream.println(result);
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.info("Server run has an exception:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    log.info("inputStream close has an exception:", e);
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
                    log.info("socket close has an exception:", e);
                }
                socket = null;
            }
        }


    }
}
