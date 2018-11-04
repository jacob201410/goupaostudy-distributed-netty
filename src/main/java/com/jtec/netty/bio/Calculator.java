package com.jtec.netty.bio;

public class Calculator {

    public static int cal(String paramStr) throws Exception {

        char op = paramStr.charAt(1);
        switch (op) {
            case '+':
                return (paramStr.charAt(0) - 48) + (paramStr.charAt(2) - 48);
            case '-':
                return (paramStr.charAt(0) - 48) - (paramStr.charAt(2) - 48);
            case '*':
                return (paramStr.charAt(0) - 48) * (paramStr.charAt(2) - 48);
            case '/':
                return (paramStr.charAt(0) - 48) / (paramStr.charAt(2) - 48);
            default:
                throw new Exception("Calculator error");
        }
    }

}
