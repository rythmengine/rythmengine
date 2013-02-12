package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;
import static com.greenlaw110.rythm.utils.NamedParams.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test @if parser
 */
public class IfParserTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
    }

    @Test
    public void testIf() {
        t = "@if(true) {true}";
        s = r(t);
        assertEquals("true", s);
        
        t = "@if(true) true@";
        assertEquals("true", s);
    }
    
    @Test
    public void testIfNot() {
        t = "@ifNot(false) {true}";
        s = r(t);
        assertEquals("true", s);
        
        t = "@ifNot(true) true@";
        s = r(t);
        assertEquals("", s);
    }
    
    @Test
    public void testIfElse() {
        t = "@if(@1) {true} else {false}";
        s = r(t, true);
        assertEquals("true", s);
        s = r(t, false);
        assertEquals("false", s);
        
        t = "@if(@1) true@ else false@";
        s = r(t, true);
        assertEquals("true", s);
        s = r(t, false);
        assertEquals("false", s);
    }

    @Test
    public void testIfNotElse() {
        t = "@ifNot(@1) {true} else {false}";
        s = r(t, true);
        assertEquals("false", s);
        s = r(t, false);
        assertEquals("true", s);
        
        t = "@ifNot(@1) true@ else false@";
        s = r(t, true);
        assertEquals("false", s);
        s = r(t, false);
        assertEquals("true", s);
    }
    
    @Test
    public void testIfElseIf() {
        t = "@if(@1 < 14) {kid} else if (@1 < 30) {yong man} else {aged}";
        s = r(t, 10);
        assertEquals("kid", s);
        s = r(t, 28);
        assertEquals("yong man", s);
        s = r(t, 200);
        assertEquals("aged", s);

        t = "@if(@1 < 14) kid@ else if (@1 < 30) yong man@ else aged@";
        s = r(t, 10);
        assertEquals("kid", s);
        s = r(t, 28);
        assertEquals("yong man", s);
        s = r(t, 200);
        assertEquals("aged", s);
    }
    
    @Test
    public void testIfWithLineBreak() {
        t = "abc\n@if(@1 < 14) {\n\tkid\n} else if (@1 < 30) {\n\tyong man\n} else {\n\taged\n} \nabc";
        s = r(t, 10);
        assertEquals("abc\n\tkid\nabc", s);
        s = r(t, 28);
        assertEquals("abc\n\tyong man\nabc", s);
        s = r(t, 200);
        assertEquals("abc\n\taged\nabc", s);
        
        t = "@if(@1 < 14) \n\tkid\n@ else if (@1 < 30) \n\tyong man\n@ else \n\taged\n@\n 123";
        s = r(t, 10);
        assertEquals("\tkid\n 123", s);
        s = r(t, 28);
        assertEquals("\tyong man\n 123", s);
        s = r(t, 200);
        assertEquals("\taged\n 123", s);
    }
    
    public static void main(String[] args) {
        run(IfParserTest.class);
    }
}
