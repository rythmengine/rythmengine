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
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.extension.ICodeType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test inline and block comment
 */
public class CommentTest extends TestBase {

    @Before
    public void setUp() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
    }

    @Test
    public void testInlineComment() {
        t = "abc@//adfiauoprquwreqw\nxyz";
        s = r(t);
        assertEquals("abc\nxyz", s);
    }
    
    @Test
    public void testDirectiveInsideInlineComment() {
        t = "abc@//addfa @for loop dafd\nxyz";
        s = r(t);
        assertEquals("abc\nxyz", s);
    }
    
    @Test
    public void testInlineCommentInsideDirectiveComment() {
        System.setProperty(RythmConfigurationKey.FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "true");
        t = "abc<!-- @//addfa @for loop dafd -->\nxyz";
        s = r(t);
        assertEquals("abc\nxyz", s);
    }
    
    @Test
    public void testBlockComment() {
        t = "abc@**\n * Special notes to this @for loop\n *\n * Rythm do it's best to speculate the Type of the iterating element \n *@xyz";
        s = r(t);
        assertEquals("abcxyz", s);
    }

    @Test
    public void testBlockCommentWithLineBreaks() {
        t = "abc\n@**\n * Special notes to this @for loop\n *\n * Rythm do it's best to speculate the Type of the iterating element \n *@xyz";
        s = r(t);
        assertEquals("abcxyz", s);
    }
    
    @Test
    public void testBlockCommentInsideDirectiveComment() {
        System.setProperty(RythmConfigurationKey.FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "true");
        t = "abc\n<!-- @**\n * Special notes to this @for loop\n *\n * Rythm do it's best to speculate the Type of the iterating element \n *@-->\nxyz";
        s = r(t);
        assertEquals("abc\nxyz", s);
    }
    
    @Test
    public void testRemoveSpaceTillLastLineBreak() {
        t = "abc\n\t   @//xyzd daf\n\t123";
        s = r(t);
        assertEquals("abc\n\t123",s);
        
        t = "abc\t @//xyzd daf\n\t123";
        s = r(t);
        assertEquals("abc\t \n\t123",s );
        
        t = "abc\n\t   @**\n *\n * abc\n *@\n123";
        s = r(t);
        assertEquals("abc\n123", s);
        
        t = "abc\t   @**\n *\n * abc\n *@\n123";
        s = r(t);
        assertEquals("abc\t   \n123", s);
    }

    public static void main(String[] args) {
        run(CommentTest.class);
    }
}
