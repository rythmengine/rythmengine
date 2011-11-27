package com.greenlaw110.rythm.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

public class TemplateTokenizer implements Iterable<TextBuilder> {
    private IContext ctx;
    private List<IParser> Parsers = new ArrayList<IParser>();
    public TemplateTokenizer(String template, IContext context) {
        ctx = context;
//        Parsers.add(new CommentParser(ctx));
//        Parsers.add(new ArgParser(ctx));
//        Parsers.add(new DialectParser(ctx));
//        Parsers.add(new ImportParser(ctx));
//        Parsers.add(new ForParser(ctx));
//        Parsers.add(new IfElseParser(ctx));
//        Parsers.add(new BlockCloseParser(ctx));
//        Parsers.add(new EvaluatorParser(ctx));
//        Parsers.add(new StringTokenParser(ctx));
    }
    @Override
    public Iterator<TextBuilder> iterator() {
        return new Iterator<TextBuilder>() {

            @Override
            public boolean hasNext() {
                return ctx.hasRemain();
            }

            @Override
            public TextBuilder next() {
                for (IParser p: Parsers) {
                    TextBuilder t = p.go();
                    if (null != t) {
                        return t;
                    }
                }
                String s = ctx.getRemain();
                ctx.step(s.length());
                return new Token(s, ctx);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }
}
