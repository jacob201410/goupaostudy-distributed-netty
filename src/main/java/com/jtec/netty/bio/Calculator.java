package com.jtec.netty.bio;

public class Calculator {

    public static String cal(String paramStr) {
        return Thread.currentThread().getName() + paramStr;
    }

}
