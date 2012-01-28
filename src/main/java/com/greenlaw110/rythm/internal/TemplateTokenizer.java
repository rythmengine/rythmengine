package com.greenlaw110.rythm.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.ParserDispatcher;
import com.greenlaw110.rythm.internal.parser.build_in.BlockCloseParser;
import com.greenlaw110.rythm.internal.parser.build_in.ScriptParser;
import com.greenlaw110.rythm.internal.parser.build_in.StringTokenParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.util.TextBuilder;

public class TemplateTokenizer implements Iterable<TextBuilder> {
    ILogger logger = Logger.get(TemplateTokenizer.class);
    private IContext ctx;
    private List<IParser> parsers = new ArrayList<IParser>();
    public TemplateTokenizer(String template, IContext context) {
        ctx = context;
        parsers.add(new ParserDispatcher(ctx));
        parsers.add(new BlockCloseParser(ctx));
        parsers.add(new ScriptParser(ctx));
        parsers.add(new StringTokenParser(ctx));
        // add a fail through parser to prevent unlimited loop
        parsers.add(new ParserBase(ctx) {
            private int lastCursor = 0;
            @Override
            public TextBuilder go() {
                TemplateParser p = (TemplateParser)ctx();
                if (lastCursor < p.cursor) return null;
                logger.warn("fail-through parser reached. is there anything wrong in your template?");
                String oneStep = p.getRemain().substring(0, 1);
                p.step(1);
                lastCursor = p.cursor;
                return new Token(oneStep, p);
            }
        });
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
                for (IParser p: parsers) {
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
