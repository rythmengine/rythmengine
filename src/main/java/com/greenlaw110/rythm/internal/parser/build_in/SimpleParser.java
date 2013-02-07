package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * parse @__simple__: mark the current template is simple template
 */
@Deprecated
public class SimpleParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.SIMPLE;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @__simple__ statement");
                }
                step(r.stringMatched().length());
                //ctx().getCodeBuilder().setSimpleTemplate(ctx().currentLine());
                return new CodeToken("", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s(?@())?)\\s+";
    }

    public static void main(String[] args) {
        Regex r = new SimpleParser().reg(Rythm.INSTANCE);
        if (r.search("@__simple__() ad")) {
            p(r, 3);
        }
    }

}
