package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import org.junit.Test;

/**
 * Test inline and block comment
 */
public class CommentTest extends TestBase {
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
