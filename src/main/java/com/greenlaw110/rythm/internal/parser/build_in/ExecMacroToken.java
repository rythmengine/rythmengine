package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 19/07/12
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExecMacroToken extends CodeToken {
    public ExecMacroToken(String macro, IContext context, int line) {
        super(macro, context);
        this.line = line;
    }

    @Override
    public void output() {
        CodeBuilder cb = ctx.getCodeBuilder();
        if (!cb.hasMacro(s)) {
            throw new ParseException(ctx.getEngine(), ctx.getTemplateClass(), line, "Cannot find macro definition for \"%s\"", s);
        }
        List<TextBuilder> list = cb.getMacro(s);
        for (TextBuilder tb : list) {
            tb.build();
        }
    }
}
