package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IncludeParser extends KeywordParserFactory {

    private static final String R = "(^%s(%s\\s*((?@()))\\s*))";

    public IncludeParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                int lineNo = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(3);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                s = S.stripBraceAndQuotation(s);
                try {
                    String code = ctx().getCodeBuilder().addIncludes(s, lineNo);
                    return new CodeToken(code, ctx());
                } catch (NoClassDefFoundError e) {
                    raiseParseException("error adding includes: " + e.getMessage() + "\n possible cause: lower/upper case issue on windows platform");
                    return null;
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.INCLUDE;
    }

    public static void main(String[] args) {
        IncludeParser p = new IncludeParser();
        Regex r = p.reg(Rythm.INSTANCE);
        String s = "@include(\"x.y.z,foo.bar\") \n@sayHi(\"green\")";
        if (r.search(s)) {
            p(r);
        }
    }
}
