package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.ParserDispatcher;
import com.greenlaw110.rythm.internal.parser.build_in.*;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateTokenizer implements Iterable<TextBuilder> {
    ILogger logger = Logger.get(TemplateTokenizer.class);
    private IContext ctx;
    private List<IParser> parsers = new ArrayList<IParser>();
    private int lastCursor = 0;

    public TemplateTokenizer(IContext context) {
        ctx = context;
        RythmEngine engine = ctx.getEngine();
        RythmConfiguration conf = engine.conf();
        if (conf.enableSmartEscape() && engine.extensionManager().hasTemplateLangs()) {
            parsers.add(new LangBlockStartSensor(ctx));
            parsers.add(new LangBlockEndSensor(ctx));
        }
        if (conf.enableNaturalTemplate() && engine.extensionManager().hasTemplateLangs()) {
            parsers.add(new DirectiveCommentStartSensor(ctx));
            parsers.add(new DirectiveCommentEndSensor(ctx));
        }
        parsers.add(new ParserDispatcher(ctx));
        parsers.add(new BlockCloseParser(ctx));
        parsers.add(new ScriptParser(ctx));
        parsers.add(new StringTokenParser(ctx));
        // add a fail through parser to prevent unlimited loop
        parsers.add(new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                TemplateParser p = (TemplateParser) ctx();
                if (lastCursor < p.cursor) return null;
                //logger.warn("fail-through parser reached. is there anything wrong in your template? line: %s", ctx.currentLine());
                String oneStep = p.getRemain().substring(0, 1);
                p.step(1);
                return new Token.StringToken(oneStep, p);
            }
        });
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
                for (IParser p : parsers) {
                    TextBuilder t = p.go();
                    if (null != t) {
                        lastCursor = ((TemplateParser) ctx).cursor;
                        return t;
                    }
                }
                throw new RuntimeException("Internal error");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
}
