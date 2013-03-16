/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static com.greenlaw110.rythm.utils.NamedParams.from;
import static com.greenlaw110.rythm.utils.NamedParams.p;

/**
 * Test @for parser
 */
public class ForParserTest extends TestBase {
    @Before
    public void configure() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
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
    
    @Test
    public void testForLoop1WithLineBreak() {
        t = "abc\n@for (int i = 0; i < 5; ++i) {\n\t@i\n}\n123";
        s = r(t);
        assertEquals("abc\n\t0\n\t1\n\t2\n\t3\n\t4\n123", s);
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
    public void testForWithLineBreaks() {
        t = "abc\n@for (String item: items) { \n\t@(item)@item_sep\n}xyz";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("abc\n\ta,\n\tb,\n\tc\nxyz", s);

        t = "abc\n@for (String item: items) { \n\t@(item)@item_sep\n}  \nxyz";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("abc\n\ta,\n\tb,\n\tc\nxyz", s);
    }
        
    @Test
    public void testForWithLineBreaks2() {
        t = "abc@for (String item: items) {@(item)@item_sep}xyz";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("abca,b,cxyz", s);

        t = "abc@for (String item: items) {@(item)@item_sep\n}  \nxyz";
        s = r(t, from(p("items", "a,b,c".split(","))));
        assertEquals("abca,\nb,\nc\nxyz", s);
    }
    
    @Test
    public void testSmartIterator() {
        t = "@for(@1){@__sep}";
        s = r(t, "a, b, c");
        assertEquals("a,b,c",s);

        s = r(t, "a : b:c");
        assertEquals("a,b,c",s);

        s = r(t, "a; b; c");
        assertEquals("a,b,c",s);
        
        s = r(t, "a - b - c");
        assertEquals("a,b,c",s);
        
        s = r(t, "a_b_c");
        assertEquals("a,b,c",s);
        
        s = r(t, "a:1,b:2;x:10,y:12");
        assertEquals("a:1,b:2,x:10,y:12", s);
    }
    
    @Test
    public void testLoopVarSeparator() {
        t = "@for(\"a:b:c\"){@__sep}";
        s = r(t);
        assertEquals("a,b,c", r(t));
        
        t = "@for(\"a:b:c\"){@_ @_sep}";
        s = r(t);
        assertEquals("a ,b ,c ", r(t));

        t = "@for(s in \"a:b:c\"){@s__sep}";
        s = r(t);
        assertEquals("a,b,c", r(t)); 

        t = "@for(s in \"a:b:c\"){@s @s_sep}";
        s = r(t);
        assertEquals("a ,b ,c ", r(t));
        
        t = "@for(\"a:b:c\"){@__utils.sep(\"|\")}";
        s = r(t);
        assertEquals("a|b|c", r(t));

        t = "@for(\"a:b:c\"){@(_)@_utils.sep(\"|\")}";
        s = r(t);
        assertEquals("a|b|c", r(t));
    }
    
    @Test
    public void testLoopVarSize() {
        t = "@for(1..5){@(_)/@(_size)@_sep}";
        assertEquals("1/4,2/4,3/4,4/4", r(t));
    }
    
    @Test
    public void testLoopVarParity() {
        t = "@for(1..5){@(_):@(_parity)@_sep}";
        assertEquals("1:odd,2:even,3:odd,4:even", r(t));
        
        t = "@for(1..5){@(_):@(_isOdd ? 1 : 0)@_sep}";
        assertEquals("1:1,2:0,3:1,4:0", r(t));
    }
    
    @Test
    public void testLoopVarFirstLast() {
        t = "@for(1..5){@(_):@if(_isFirst){[f]}else if(_isLast){[l]}else{[]}@_sep}";
        assertEquals("1:[f],2:[],3:[],4:[l]", r(t));
    }
    
    @Test
    public void testLoopVarIndex() {
        t = "@for(\"a,b,c\")@_|@(_index)@_sep@";
        assertEquals("a|1,b|2,c|3", r(t));
    }
    
    @Test
    public void testElse() {
        t = "@for(@1){@__sep}else{empty list}";
        assertEquals("empty list", r(t, Arrays.asList(new String[]{})));
        assertEquals("a,b,c", r(t, Arrays.asList("a,b,c".split(","))));
    }
    
    @Test
    public void testShortNotation() {
        t = "@for(@1)@__sep @else empty list@";
        assertEquals("a,b,c", r(t, Arrays.asList("a,b,c".split(","))));
        assertEquals("empty list", r(t, Arrays.asList(new String[]{})));
    }
    
    @Test
    public void testElseWithLineBreaks() {
        t = "abc\n\t@for(@1){\n\t\t@__sep\n\t} else {\n\t\tempty list\n\t}\n123";
        assertEquals("abc\n\t\tempty list\n123", r(t, ""));
        assertEquals("abc\n\t\ta,\n\t\tb,\n\t\tc\n123", r(t, "a,b,c"));
    }
    
    @Test
    public void testBreak() {
        t = "@for(int i in 1..10){@(i)@if(i > 3){@break}}";
        eq("1234");

        t = "@for(int i in 1..10){@(i)@if(i > 3){\n\t@break\n}}";
        s = r(t);
        eq("1234");

        t = "@for(int i in 1..10){@(i)@if(i > 3){\n\toverflow...\n\t@break\n}}";
        s = r(t);
        eq("1234\toverflow...");
    }
    
    @Test
    public void testContinue() {
        t = "@for(int i in 1..10){@if((i % 2) == 0){@continue}@i}";
        eq("13579");

        t = "@for(int i in 1..10){@if((i % 2) == 0){\n\t@continue\n}@i}";
        s = r(t);
        eq("13579");

        t = "@for(int i in 1..10){@if((i % 2) == 0){\n\tE\n\t@continue\n}@i}";
        s = r(t);
        eq("1\tE3\tE5\tE7\tE9");
    }

    /**
     * bug: @for(T<String, String> x: itr) {...} coz trouble b/c space
     * between , and String
     */
    @Test
    public void test2() {
        t = "@args Set<Map<String,String>> mset;@for(Map<String, String> m: mset){123}";
        s = r(t, Collections.EMPTY_SET);
        eq("");
    }
    
    @Test
    public void test3() {
        t = "@args List items\n     @for(Object item: items) {\n@{}}";
        s = r(t, Collections.EMPTY_LIST);
        eq("\n");
    }
    
    @Test
    public void test4() {
        t = "@for(\"a:b:c\"){@(_)@_utils.sep(\"|\")}";
        s = r(t);
        assertEquals("a|b|c", r(t));
        t = "@for(channels){@__utils.sep(\"|\")}";
        s = r(t, from(p("channels", "a,b".split(","))));
        eq("a|b");
    }
    
    @Test
    public void test5() {
        t = "@for(items){@__sep}";
        s = r(t, from(p("items", Collections.EMPTY_LIST)));
        eq("");
    }
    
    @Test
    public void testJoin() {
        t = "@for (int i = 0; i < 5; ++i).join('\n') {@i}";
        s = r(t);
        assertEquals("0\n1\n2\n3\n4", s);

        t = "@for(items).join('\n'){@_}";
        s = r(t, from(p("items", new Integer[]{1,2})));
        eq("1\n2");
        
        t = "@for(1..5).join(){@_}";
        s = r(t);
        eq("1,2,3,4");
        
        t = "@for(1..5).join(@1){@_}";
        s = r(t, "|");
        eq("1|2|3|4");
    }

    public static void main(String[] args) {
        run(ForParserTest.class);
    }
}
