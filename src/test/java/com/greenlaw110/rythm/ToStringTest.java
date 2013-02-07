package com.greenlaw110.rythm;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/07/12
 * Time: 9:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToStringTest {
    private String foo;
    public int bar;

    public String getFoo() {
        return foo;
    }

    public ToStringTest(String foo, int bar) {
        this.foo = foo;
        this.bar = bar;
    }

    @Override
    public String toString() {
        return Rythm.toString(this);
    }

    public static void main(String[] args) {
        ToStringTest tst = new ToStringTest("bar", 5);
        String s = Rythm.toString(tst);
        System.out.println(s);
    }
}
