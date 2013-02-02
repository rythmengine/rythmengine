package com.greenlaw110.rythm;

import com.alibaba.fastjson.JSON;
import com.greenlaw110.rythm.utils.JSONWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 3/02/13
 * Time: 8:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        String t = "@args int a\n@(a+5)";

        // passing json object
        System.out.println("test json object");
        String p = "{\"a\":13}";
        String s = Rythm.render(t, JSONWrapper.wrap(p));
        System.out.println(s);
        
        // passing json array
        System.out.println();
        System.out.println("test json array");
        p = "[2]";
        s = Rythm.render(t, JSONWrapper.wrap(p));
        System.out.println(s);
    }
}
