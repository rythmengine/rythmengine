package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

import org.junit.Before;
import org.junit.Test;

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
    
}
