package com.jtec.netty.bio;

import java.io.IOException;
import java.util.Random;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable () {

            @Override
            public void run() {
                try {
                    BIOServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(100L);

        char[] op = {'+','-','*','/'};

        final Random random = new Random(System.currentTimeMillis());
        new Thread(new Runnable() {

            @Override
            public void run() {
                final int opLength = op.length;
                while(true) {
                    String expression = random.nextInt(10) + ""
                            + op[random.nextInt(opLength)] + (random.nextInt(10) + 1);
                    Client.send(expression);
                    try {
                        Thread.sleep((random.nextInt(1000)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
