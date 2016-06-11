/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.advanced;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.rythmengine.extension.ICodeType;
import org.junit.Before;
import org.junit.Test;

import static org.rythmengine.conf.RythmConfigurationKey.*;
import static org.rythmengine.utils.NamedParams.from;
import static org.rythmengine.utils.NamedParams.p;

/**
 * Test Natural Template feature
 */
public class NaturalTemplateTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.getProperties().put(DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
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
        System.setProperty(ENGINE_OUTPUT_JAVA_SOURCE_ENABLED.getKey(), "false");
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
