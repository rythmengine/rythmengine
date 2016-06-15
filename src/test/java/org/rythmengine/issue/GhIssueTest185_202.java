/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import java.io.StringWriter;

import models.GH185Model;

import org.junit.Ignore;
import org.junit.Test;
import org.rythmengine.Rythm;
import org.rythmengine.TestBase;

/**
 * Test Github Issues
 */
public class GhIssueTest185_202 extends TestBase {
    boolean debug=false;
    
    @Test
    public void test185() {
        GH185Model model = new GH185Model("bar");
        t = "@args models.GH185Model model\n@if (model.foo@) {@model.foo@}";
        s = r(t, model);
        eq("bar");
    }

    @Test
    public void test188() {
        t = "gh188/foo.txt";
        s = r(t);
        eq("good");
    }

    @Test
    public void test193() {
        t = "@if(true) {a} else { \nb\n}";
        s = r(t);
        eq("a");
    }

    @Test
    @Ignore
    public void test194() {
        // This is known issue.
        // The workaround could be use ESCAPED WORD
        t = "@verbatim{\\}";
        s = r(t);
        eq("\\");
    }

    @Test
    @Ignore
    public void test201() {
        // Known issue
        t = "gh201/gh201.txt";
        StringWriter sw = new StringWriter();
        Rythm.engine().render(sw, t);
        s = sw.toString();
        contains("header");
        contains("inner");
        contains("footer");
    }

    @Test
    public void test202() {
        t = "@def echo(String s){@s}@echo(\"okay?\")";
        eq("okay?");
    }
}
