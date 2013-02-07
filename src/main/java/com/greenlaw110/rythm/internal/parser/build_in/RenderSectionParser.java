package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @render section|content
 */
public class RenderSectionParser extends KeywordParserFactory {

    public class DefaultSectionToken extends BlockCodeToken {
        private String section;
        public DefaultSectionToken(String section, IContext context) {
            super(null, context);
            if (S.isEmpty(section)) {
                section = "__CONTENT__";
            }
            this.section = section;
        }

        @Override
        public void output() {
            p2t("_startSection(\"").p(section).p("\");");
            pline();
        }

        @Override
        public String closeBlock() {
            StringBuilder sbNew = new StringBuilder();
            StringBuilder sbOld = getOut();
            setOut(sbNew);
            p2tline("_endSection(true);");
            if ("__CONTENT__".equals(section)) {
                p2tline("_pLayoutContent();");
            } else {
                p2t("_pLayoutSection(\"").p(section).p("\");");
                pline();
            }
            setOut(sbOld);
            return sbNew.toString();
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.RENDER_SECTION;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String section = m.group(4);
                s = remain();
                Matcher m0 = InvokeTagParser.P_HEREDOC_SIMBOL.matcher(s);
                Matcher m1 = InvokeTagParser.P_STANDARD_BLOCK.matcher(s);
                if (m0.matches()) {
                    ctx().step(m0.group(1).length());
                    return new DefaultSectionToken(section, ctx());
                } else if (m1.matches()) {
                    ctx().step(m1.group(1).length());
                    return new DefaultSectionToken(section, ctx());
                } else {
                    String code = S.isEmpty(section) ? "_pLayoutContent();" : "_pLayoutSection(\"" + section + "\");";
                    return new CodeToken(code, ctx());
                }
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s\\s*[\\s\\(]\"?'?(" + Patterns.VarName + ")?\"?'?\\)?).*";
    }

    public static void main(String[] args) {
        String s = String.format(new RenderSectionParser().patternStr(), "@", Keyword.RENDER_SECTION);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher("@render() Hello world");
        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(4));
        }
    }

}
