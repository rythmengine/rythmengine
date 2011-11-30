package com.greenlaw110.rythm.ut;

import com.greenlaw110.rythm.internal.MockCodeBuilder;
import com.greenlaw110.rythm.internal.MockContext;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.util.TextBuilder;

public class UnitTest extends org.junit.Assert {
    
    protected DialectBase d = new Rythm();
    protected MockContext c;
    protected MockCodeBuilder b;
    
    protected void setup(String template) {
        b = new MockCodeBuilder(template, "test");
        c = new MockContext(b);
    }
    
    protected void call(TextBuilder b) {
        if (b instanceof Directive) {
            ((Directive)b).call();
        }
    }
    
}
