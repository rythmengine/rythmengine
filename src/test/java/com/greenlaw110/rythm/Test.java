package com.greenlaw110.rythm;

import com.greenlaw110.rythm.spi.Transformer;

@Transformer
public class Test {

    public static Integer dbl(Integer i) {
        return i * 2;
    }

    public static String dbl(String s) {
        if (null == s) return "";
        return s + s;
    }

    public static String dbl(Object o) {
        if (null == o) return "";
        return dbl(o.toString());
    }

    public static void main(String[] args) {
        Rythm.engine().registerJavaExtension(Test.class);
        String t = "@args String s, int i\n" +
                "double of \"@s\" is \"@s.dbl()\",\n " +
                "double of [@i] is [@i.dbl().format(\"0000.00\")]";
        String s = Rythm.render(t, "Java", 99);
        System.out.println(s);
    }
}
