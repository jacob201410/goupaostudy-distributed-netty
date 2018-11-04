package com.jtec.netty.bio;


import io.netty.util.internal.StringUtil;

import java.util.Date;
import java.util.Random;

public class SayHi {

    public static String justHi(String someOne) throws Exception {
        String[] hiArr = {" say : Hi, due~ on[", " say : What's up, due~ on[", " say : Oh boy, look u, you perfect tonight~ on["};
        Random random = new Random();
        if (!StringUtil.isNullOrEmpty(someOne)) {
            StringBuffer sbf = new StringBuffer();
            sbf.append(someOne);
            sbf.append(hiArr[random.nextInt(hiArr.length)]);
            sbf.append(new Date());
            sbf.append("].");
            return sbf.toString();
        }
        return "that's so lonely, noOne talking tonight...";
    }

}
