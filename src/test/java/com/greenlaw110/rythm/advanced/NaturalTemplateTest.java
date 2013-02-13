package com.greenlaw110.rythm.advanced;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import org.junit.Before;
import org.junit.Test;
import static com.greenlaw110.rythm.utils.NamedParams.*;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

/**
 * Test Natural Template feature
 */
public class NaturalTemplateTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Rythm.shutdown();
    }
    
    @Test
    public void testBasicDirectives() {
        
        String t = "<!-- @args String name, int age -->Hi @name, happy @age!";
        String s = Rythm.render(t, from(p("name", "Green Luo"), p("age", 100)));
        assertEquals("Hi Green Luo, happy 100!", s);
        
        t = "<!-- @if (false) {-->false<!-- } else {-->true<!-- }-->";
        s = Rythm.render(t);
        assertEquals("true", s);
        
        t = "<!-- @for (1..5) {-->@(_)@_sep<!-- } -->";
        s = Rythm.render(t);
        assertEquals("1,2,3,4", s);
    }
    
    @Test
    public void testComments() {
        String t = "<!-- @// abcde -->";
        String s = Rythm.render(t);
        assertEquals("", s);
        
        t = "<!-- @* abc\nxyz@abcd\n*@ -->";
        s = Rythm.render(t);
        assertEquals("", s);
    }
    
    @Test
    public void testLangSwitch() {
        String t = "<!-- @if (true) { --><script>/* @if(true) {*/alert(1);<!-- } --></script><!-- } -->";
        String s = Rythm.render(t);
        System.setProperty(ENGINE_DEBUG_JAVA_SOURCE_ENABLED.getKey(), "false");
        assertEquals("<script>alert(1);</script>", s);
    }
    
    @Test
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "false");
        String t = "<!-- @args String name, int age -->Hi @name, happy @age!";
        String s = Rythm.render(t, from(p("name", "Green Luo"), p("age", 100)));
        assertEquals("<!-- -->Hi Green Luo, happy 100!", s);
        
        t = "<!-- @if (false) {-->false<!-- } else {-->true<!-- }-->";
        s = Rythm.render(t);
        assertEquals("<!-- -->true<!-- -->", s);
        
        t = "<!-- @for (1..5) {-->@(_)@_sep<!-- } -->";
        s = Rythm.render(t);
        assertEquals("<!-- -->1,<!-- -->2,<!-- -->3,<!-- -->4<!--  -->", s);
    }

    public static void main(String[] args) {
        run(NaturalTemplateTest.class);
    }
}
