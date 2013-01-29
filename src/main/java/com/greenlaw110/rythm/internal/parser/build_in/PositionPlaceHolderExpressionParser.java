package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.dialect.SimpleRythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @1, @2 etc, which is used to denote the position of
 * the variable used in the template. This is best suited to
 * passing parameters by position
 */
public class PositionPlaceHolderExpressionParser extends CaretParserFactoryBase {

    protected String patternStr() {
        return "^%s([0-9]+)";
    }
    

    @Override
    public IParser create(final IContext ctx) {
        String caret_ = ctx.getDialect().a();
        Regex r = new Regex(String.format(patternStr(), caret_));
        if (!r.search(ctx.getRemain())) return ParserBase.NULL_INST;
        ctx.step(r.stringMatched().length());
        String pos = r.stringMatched(1);
        final String vname = "__v_" + pos; // be different from CodeBuilder's varName generation: __v[0-9]+
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                return new ExpressionParser.ExpressionToken(vname, ctx);
            }
        };
    }

    public static void main(String[] args) {
        Regex r = new Regex("^@([0-9]+)");
        String s = "@1 dfs";
        p(s, r);
    }
}
