package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * Parse @section mysection
 */
public class SectionParser extends KeywordParserFactory {

    public class SectionToken extends BlockCodeToken {
        private String section;
        public SectionToken(String section, IContext context) {
            super(null, context);
            this.section = section;
        }

        @Override
        public void output() {
            p("\n_startSection(\"").p(section).p("\");\n");
        }

        @Override
        public String closeBlock() {
            return "\n_endSection();";
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.SECTION;
    }

//    public IParser create(IContext ctx) {
//        return new ParserBase(ctx) {
//            public TextBuilder go() {
//                Matcher m = ptn(dialect()).matcher(remain());;
//                if (!m.matches()) return null;
//                String s = m.group(1);
//                step(s.length());
//                String section = m.group(2);
//                return new SectionToken(section, ctx());
//            }
//        };
//    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) raiseParseException("bad @section statement. Correct usage: @section(\"mySection\"){...}");
                int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                return new SectionToken(s, ctx());
            }
        };
    }


    @Override
    protected String patternStr() {
        //return "(%s%s[\\s]+([a-zA-Z][a-zA-Z0-9_]+)[\\s\\r\\n\\{]*).*";
        return "%s%s\\s*((?@()))[\\s]*\\{?\\s*";
    }

    public static void main(String[] args) {
        Regex r = new SectionParser().reg(new Rythm());
        if (r.search("@section(\"JS\") \nab")) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }

}
