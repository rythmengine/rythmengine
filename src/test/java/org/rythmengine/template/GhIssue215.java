/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

import org.rythmengine.utils.Range;

/**
 * Test for issue as outlined in:
 * https://github.com/rythmengine/rythmengine/issues/215
 * @author wf
 *
 */
public class GhIssue215 {

    private GhIssue215() {
    }

    @SuppressWarnings("rawtypes")
    public static void foo() {
        TemplateBase.__Itr itr = TemplateBase.__Itr.ofRange(Range.valueOf("1.. 5"));
        System.out.println(itr.size());
    }

    public static void main(String[] args) {
        foo();
    }
}
