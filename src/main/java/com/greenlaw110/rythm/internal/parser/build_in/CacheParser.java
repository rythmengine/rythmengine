package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Parse @cacheFor("1m")
 */
public class CacheParser extends KeywordParserFactory {

    private static final Pattern P_INT = Pattern.compile("\\-?[0-9\\*\\/\\+\\-]+");
    public static void validateDurationStr(String d, IContext ctx) {
        if ("null".equals(d)) return;
        if ((d.startsWith("\"") && d.endsWith("\""))) {
            String s = S.stripQuotation(d);
            try {
                ctx.getEngine().durationParser.parseDuration(s);
            } catch (Exception e) {
                raiseParseException(ctx, "Invalid time duration: %s", d);
            }
        } else {
            if (!P_INT.matcher(d).matches()) {
                raiseParseException(ctx, "Invalid time duration: %s. int(second) or string expected. String must be double quoted", d);
            }
        }
    }

    /*
    {
      String s = _engine().cached("key", 1, foo.bar());
      if (null != s) {
        p(s);
      } else {
        StringBuilder sbOld = getOut();
        StringBuilder sbNew = new StringBuilder()
        setOut(sbNew);
        ...
        s = sbNew.toString();
        setOut(sbOld);
        _engine().cache("key", s, duration, 1, foo.bar());
        p(s)
      }
    }
     */
    private static class CacheToken extends BlockCodeToken {
        private String args;
        private String duration;
        private int startIndex;
        private int endIndex;
        private String key;
        CacheToken( String duration, String args, IContext ctx) {
            super("", ctx);
            this.duration = S.isEmpty(duration) ? "null" : duration;
            // check if duration is valid
            validateDurationStr(this.duration, ctx);
            this.args = args;
            this.startIndex = ctx.cursor();
        }

        @Override
        public void output() {
            p("{");
            pline();
            pt("java.io.Serializable s = _engine().cached(\"").p(key).p("\"").p(args).p(");");
            pline();
            pt("if (null != s) {");
            pline();
            p2t("p(s);");
            pline();
            pt("} else {");
            pline();
            p2t("StringBuilder sbOld = getOut();");
            pline();
            p2t("StringBuilder sbNew = new StringBuilder();");
            pline();
            p2t("setOut(sbNew);");
            pline();
        }

        @Override
        public String closeBlock() {
            endIndex = ctx.cursor();
            String body = ctx.getTemplateSource(startIndex, endIndex);
            String tmplName = ctx.getTemplateClass().name();
            String keySeed = body + tmplName;
            key = UUID.nameUUIDFromBytes(keySeed.getBytes()).toString();
            StringBuilder sbOld = getOut();
            StringBuilder sbNew = new StringBuilder();
            setOut(sbNew);
            p2t("s = sbNew.toString();");
            pline();
            p2t("setOut(sbOld);");
            pline();
            p2t("_engine().cache(\"").p(key).p("\",s,").p(duration).p(args).p(");");
            pline();
            p2t("p(s);");
            pline();
            pt("}");
            pline();
            p("}");
            pline();
            String s = sbNew.toString();
            setOut(sbOld);
            return s;
        }
    }

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @cacheFor statement. Correct usage: @cacheFor (\"duration_string\") {cache block}");
                }
                String key = UUID.nameUUIDFromBytes(remain().getBytes()).toString();
                ctx.step(r.stringMatched().length());
                String s = r.stringMatched(2); // ("1m", 1, bar.foo())
                s = S.stripBrace(s); // "1m", 1, bar.foo()
                String[] sa = s.split(",");
                String duration = null;
                if (sa.length > 0) duration = sa[0]; // "1m"
                String args = "";
                if (sa.length > 1) {
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 1; i < sa.length; ++i) {
                        sb.append(",").append(sa[i]);
                    }
                    args = sb.toString();
                }
                return new CacheToken(duration, args, ctx());
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.CACHE;
    }

    @Override
    protected String patternStr() {
        //return "(%s(%s\\s+\\(.*\\)(\\s*\\{)?)).*";
        return "^(^%s%s\\s*((?@()))(\\s*\\{)?)";
    }

    public static void main(String[] args) {
        Regex r = new CacheParser().reg(Rythm.INSTANCE);
        String s = "@cacheFor(\"1m\", 1, bar.foo())ab";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
    }

}
