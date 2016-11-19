/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.exception.RythmException;
import java.sql.*;

/**
 * Test include parser
 */
public class IncludeParserTest extends TestBase {
    @Test
    public void test() {
        s = r("foo/includeTest.html");
        eqf("foo/includeTest.result");
    }
    
    @Test
    public void testIncludeInlineFunction() {
        s = r("foo/includeInlineFunction.html");
        eqf("foo/includeInlineFunction.result");
    }
    
    @Test
    public void testIncludeInLayoutTemplate() {
        s = r("foo/includeInLayoutTemplate.html");
        eqf("foo/includeInLayoutTemplate.result");
    }
    
    @Test
    public void testIncludeJava() {
        s = r("foo/includeJavaTest.html");
        s=s.trim();
        eq("testString(10)");
    }
    
    @Test
    public void testIncludedRunTimeException() {
      try {
        s = r("foo/includedRunTimeException.html");
      } catch (Throwable th) {
        assertTrue(th instanceof RythmException);
        String msg=th.getMessage();
        assertTrue(msg.contains("caused by java.lang.NullPointerException"));
      }
    }
    
    @Test
    public void testIncludedImport() {
      int bigint = Types.BIGINT;
      s = r("foo/includedImport.html");
      assertTrue(s.contains(""+bigint));
      assertTrue(s.contains(""+Types.BLOB));
    }
    
    @Test
    public void testIncludedCompilerException() {
      try {
        s = r("foo/includedCompilerException.html");
      } catch (Throwable th) {
        assertTrue(th instanceof RythmException);
        String msg=th.getMessage();
        /*logger.warn(msg);
        System.out.println(msg);*/
        assertTrue(msg.contains("Strng cannot be resolved to a type"));
      }
    }

    public static void main(String[] args) {
        run(IncludeParserTest.class);
    }
}
