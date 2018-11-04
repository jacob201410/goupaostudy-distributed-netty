package com.jtec.netty.bio;


import io.netty.util.internal.StringUtil;

import java.util.Date;
import java.util.Random;

public class SayHi {

    public static String justHi(String someOne) throws Exception {
        String firstWord = "[";
        String secWord = "] - ";
        String finalWord = " say : '";
        String[] hiArr = {"Hi, dues~'", "What's up, due~'", "Oh boy, look u, you perfect tonight~'", "Did you believe, I having date with that pretty lady.", "OMG, Congratulation! Your desert."};
        Random random = new Random();
        if (!StringUtil.isNullOrEmpty(someOne)) {
            StringBuffer sbf = new StringBuffer();
            sbf.append(firstWord);
            sbf.append(new Date());
            sbf.append(secWord);
            sbf.append(firstWord);
            sbf.append(someOne);
            sbf.append(secWord);
            sbf.append(finalWord);
            sbf.append(hiArr[random.nextInt(hiArr.length)]);
            return sbf.toString();
        }
        return "that's so lonely, noOne talking tonight...";
    }

}
