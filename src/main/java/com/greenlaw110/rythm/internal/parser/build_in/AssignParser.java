package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * assign enclosed part into a variable
 * @assign("xx") {...} create a variable
 * @assign("xxx", true) {...} // create a final variable
 */
public class AssignParser extends KeywordParserFactory {

    public class AssignToken extends BlockCodeToken {
        private String assignTo;
        private boolean isFinal;
        public AssignToken(String assignTo, IContext context) {
            super(null, context);
            String[] sa = assignTo.split(",");
            this.assignTo = S.stripQuotation(sa[0]);
            if (Patterns.RESERVED.matches(this.assignTo)) {
                raiseParseException(ctx, "assign variable name is reserved: %s", this.assignTo);
            }
            if (sa.length > 1) {
                isFinal = Boolean.parseBoolean(sa[1].trim());
            }
        }

        @Override
        public void output() {
            String assignTo = this.assignTo;
            if (isFinal) assignTo = this.assignTo + "___";
            p2t("Object ").p(assignTo).p(" = null;");
            p2tline("{");
            p3tline("StringBuilder sbOld = getSelfOut();");
            p3tline("StringBuilder sbNew = new StringBuilder();");
            p3tline("setSelfOut(sbNew);");
        }

        @Override
        public String closeBlock() {
            String assignTo = this.assignTo;
            if (isFinal) assignTo = this.assignTo + "___";
            StringBuilder sbNew = new StringBuilder();
            StringBuilder sbOld = getOut();
            setOut(sbNew);
            p3tline("String s = sbNew.toString();");
            p3tline("setSelfOut(sbOld);");
            p3t(assignTo).p(" = s;");
            pline();
            p2tline("}");
            if (isFinal) {
                p2t("final Object ").p(this.assignTo).p(" = ").p(assignTo).p(";");
                pline();
            }
            String s = sbNew.toString();
            setOut(sbOld);
            return s;
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.ASSIGN;
    }

//    public IParser create(IContext ctx) {
//        return new ParserBase(ctx) {
//            public TextBuilder go() {
//                Matcher m = ptn(dialect()).matcher(remain());;
//                if (!m.matches()) return null;
//                String s = m.group(1);
//                step(s.length());
//                String assignTo = m.group(2);
//                return new SectionToken(assignTo, ctx());
//            }
//        };
//    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) raiseParseException("bad @assign statement. Correct usage: @assign(\"myVariable\"){...}");
                int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBrace(s);
                return new AssignToken(s, ctx());
            }
        };
    }


    @Override
    protected String patternStr() {
        //return "(%s%s[\\s]+([a-zA-Z][a-zA-Z0-9_]+)[\\s\\r\\n\\{]*).*";
        return "%s%s\\s*((?@()))[\\s]*\\{?\\s*";
    }

    public static void main(String[] args) {
        String s = "@assign(\"x\") {abc} @x.getClass()";
        System.out.println(new RythmEngine().render(s));
    }

}
