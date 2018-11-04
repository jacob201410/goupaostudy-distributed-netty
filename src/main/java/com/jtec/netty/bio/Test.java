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

        // method 1 - 调用算术运算业务逻辑
        char[] op = {'+','-','*','/'};

        final Random random = new Random(System.currentTimeMillis());
        new Thread(new Runnable() {

            @Override
            public void run() {
                final int opLength = op.length;
                while(true) {
                    String expression = random.nextInt(10) + ""
                            + op[random.nextInt(opLength)]
                            + (random.nextInt(10) + 1);
                    Client.send(expression);
                    letMeSleep(random);
                }
            }
        });//.start();

        // method 2 - let's talk for google+ talk
        String[] nameArr = {"Amye","Bob","Clark","Elan","Dick"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int familySize = nameArr.length;
                while (true) {
                    String talker = nameArr[random.nextInt(familySize)];
                    Client.send(talker);
                    letMeSleep(random);
                }
            }
        }).start();
    }

    private static void letMeSleep(Random random) {
        final int sleepGap = 1000;
        try {
            Thread.sleep((random.nextInt(sleepGap)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
