package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @renderBody - callback to tag body
 */
public class RenderBodyParser extends KeywordParserFactory {
    @Override
    public Keyword keyword() {
        return Keyword.RENDER_BODY;
    }

    private static class RenderBodyToken extends CodeToken {
        protected InvokeTagParser.ParameterDeclarationList params;
        RenderBodyToken(InvokeTagParser.ParameterDeclarationList params, IContext ctx) {
            super("", ctx);
            this.params = params;
        }

        @Override
        public void output() {
            pline("{");
            ptline("com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null; ");
            if (params.pl.size() > 0) {
                ptline("_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList();");
                for (int i = 0; i < params.pl.size(); ++i) {
                    InvokeTagParser.ParameterDeclaration pd = params.pl.get(i);
                    //if (i == 0 && pd.nameDef == null) pd.nameDef = "arg";
                    pt("_pl.add(\"").p(pd.nameDef == null ? "" : pd.nameDef).p("\",").p(pd.valDef).p(");");
                    pline();
                }
            }
            ptline("_pTagBody(_pl, _out);");
            pline("}");
        }
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @renderBody statement. Correct usage: @renderBody(params...)");
                }
                step(r.stringMatched().length());
                String paramS = r.stringMatched(3);
                InvokeTagParser.ParameterDeclarationList params = new InvokeTagParser.ParameterDeclarationList();
                InvokeTagParser.InvokeTagToken.parseParams(paramS, params);
                return new RenderBodyToken(params, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s\\s*((?@()))\\s*)";
    }

    public static void main(String[] args) {
        Regex r = new RenderBodyParser().reg(Rythm.INSTANCE);
        if (r.search("@renderBody(ab: 1, foo.bar())"));
        p(r, 6);
    }

}
