package org.rythmengine.template;

import org.rythmengine.utils.Range;

public class GhIssue215 {
    private GhIssue215() {
    }

    public static void foo() {
        TemplateBase.__Itr itr = TemplateBase.__Itr.ofRange(Range.valueOf("1.. 5"));
        System.out.println(itr.size());
    }

    public static void main(String[] args) {
        foo();
    }
}
