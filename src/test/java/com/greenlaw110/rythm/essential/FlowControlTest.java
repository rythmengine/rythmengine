package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;
import static com.greenlaw110.rythm.utils.NamedParams.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test @if and @for
 */
public class FlowControlTest extends TestBase {

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
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        t = "@if(@1 < 14) {\n\tkid\n} else if (@1 < 30) {\n\tyong man\n} else {\n\taged\n}";
        s = r(t, 10);
        assertEquals("\tkid", s);
        s = r(t, 28);
        assertEquals("\tyong man", s);
        s = r(t, 200);
        assertEquals("\taged", s);

        t = "@if(@1 < 14) \n\tkid\n@ else if (@1 < 30) \n\tyong man\n@ else \n\taged\n@";
        s = r(t, 10);
        assertEquals("\tkid", s);
        s = r(t, 28);
        assertEquals("\tyong man", s);
        s = r(t, 200);
        assertEquals("\taged", s);
    }

    /**
     * Test @for(int i = 0; i < 100; ++i) style
     */
    @Test
    public void testForLoop1() {
        t = "@for (int i = 0; i < 5; ++i) {@i}";
        s = r(t);
        assertEquals("01234", s);
    }

    /**
     * Test @for (TYPE e: iterable) style
     */
    @Test
    public void testForLoop2() {
        t = "@for (String item: items) {@(item)@item_sep}";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);
    }
    
    @Test
    public void testElementTypeInterference() {
        t = "@for (item: items) {@(item.length())@item_sep}";
        s = r(t, from(p("items", "abc,bc,c".split(","))));
        assertEquals("3,2,1", s);
    }
    
    @Test
    public void testNoTypeAndVar() {
        t = "@for (items) {@(_.length())@_sep}";
        s = r(t, from(p("items", "abc,bc,c".split(","))));
        assertEquals("3,2,1", s);
    }
    
    @Test
    public void testPositionPlaceHolder() {
        t = "@for (String item: @1) {@(item)@item_sep}";
        s = r(t, Arrays.asList("a,b,c".split(",")));
        assertEquals("a,b,c", s);

        t = "@for (item: @1) {@(item.length())@item_sep}";
        s = r(t, Arrays.asList("abc,bc,c".split(",")));
        assertEquals("3,2,1", s);

        t = "@for (@1) {@(_.length())@_sep}";
        s = r(t, Arrays.asList("abc,bc,c".split(",")));
        assertEquals("3,2,1", s);
    }
    
    @Test
    public void testRangeExpression() {
        t = "@for (int i : 1 .. 5) {@i}";
        s = r(t);
        assertEquals("1234", s);
        
        t = "@for (i : 1 .. 5) {@i}";
        s = r(t);
        assertEquals("1234", s);
        
        t = "@for (i in 1 .. 5) {@i}";
        s = r(t);
        assertEquals("1234", s);
        
        t = "@for (1 .. 5) {@_}";
        s = r(t);
        assertEquals("1234", s);

        t = "@for ([1 .. 5]) {@_}";
        s = r(t);
        assertEquals("12345", s);

        t = "@for (1 to 5) {@_}";
        s = r(t);
        assertEquals("1234", s);

        t = "@for (1 till 5) {@_}";
        s = r(t);
        assertEquals("12345", s);
    }
    
    @Test
    public void testDifferentSeparators() {
        t = "@for (String item in items) @(item)@item_sep@";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);

        t = "@for (String item <- items) @(item)@item_sep@";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);
    }
    
    @Test
    public void testDifferentDirectives() {
        t = "@each (String item in items) @(item)@item_sep@";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);

        t = "@forEach (String item in items) @(item)@item_sep@";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);
    }
    
    @Test
    public void testSimpleStyle() {
        t = "@for (int i = 0; i < 5; ++i) @i@";
        s = r(t);
        assertEquals("01234", s);

        t = "@for (String item: items) @(item)@item_sep@";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);

        t = "@for (@1) @(_.length())@_sep@";
        s = r(t, Arrays.asList("abc,bc,c".split(",")));
        assertEquals("3,2,1", s);

        t = "@for (1 till 5) @_@";
        s = r(t);
        assertEquals("12345", s);
    }
    
    @Test
    public void testLineBreaks() {
        t = "abc\n@for (String item: items) { \n\t@(item)@item_sep\n}xyz";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("a,b,c", s);
    }

    public static void main(String[] args) {
        run(FlowControlTest.class);
    }
}
