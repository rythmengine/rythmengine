/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.render_mode.sandbox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rythmengine.Rythm;
import org.rythmengine.Sandbox;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.exception.ParseException;
import org.rythmengine.exception.RythmException;

/**
 * Test sandbox feature
 */
public class SandboxTest extends TestBase {

    private void _run(String tmpl) throws Throwable {
        try {
            Rythm.sandbox().render(tmpl);
        } catch (RythmException re) {
            Throwable t = re.getCause();
            if (t == null) {
                t = re;
                if (t instanceof ParseException && t.getMessage().contains("Access to restricted class")){
                    throw new SecurityException(t);
                }
            }
            throw t;
        }
    }

    @Test(expected = SecurityException.class)
    public void testSecurityViolation() throws Throwable {
        _run("@{System.exit(1)}");
    }
    
    @Test(expected = SecurityException.class)
    public void testSecurityViolation1() throws Throwable {
        _run("@System.getProperty(\"xxx\")");
    }
    
    @Test(expected = SecurityException.class)
    public void testSecurityViolation3() throws Throwable {
        _run("@{Runtime r = Runtime.getRuntime();r.exit(1);}");
    }
    
    @Test(expected = SecurityException.class)
    public void testSecurityViolation4() throws Throwable {
        _run("@{RythmEngine re = new RythmEngine();}");
    }

    @Test(expected = SecurityException.class)
    public void testTimeout() throws Throwable {
        Rythm.debug = true;
        //System.getProperties().put(RythmConfigurationKey.SANDBOX_TIMEOUT.getKey(), 1000 * 60 * 60);
        _run("@if (true) {@for(;;){}}");
    }

    @Test(expected = SecurityException.class)
    public void testFileIO() throws Throwable {
        _run("@import java.io.*;@{String s = org.rythmengine.utils.IO.readContentAsString(new File(\"pom.xml\"))} @(new java.io.File(\".\").getAbsolutePath())");
    }
    
    @Test
    public void testTmpFileIO() throws Throwable {
        _run("@import java.io.*, org.rythmengine.utils.*;@{IO.writeContent(\"xxxx\", new File(System.getProperty(\"java.io.tmpdir\"), \"foo.txt\"))} @(IO.readContentAsString(new File(System.getProperty(\"java.io.tmpdir\"), \"foo.txt\"))");
    }
    
    private static final String SEC_CODE = "RYTHM_TEST";
    
    @Before
    public void setup() {
        System.setProperty(RythmConfigurationKey.SANDBOX_SECURE_CODE.getKey(), SEC_CODE);
    }
    
    @After
    public void teardown() {
        Sandbox.turnOffSandbox(SEC_CODE);
    } 

    public static void main(String[] args) {
         run(SandboxTest.class);
    }
    
}
