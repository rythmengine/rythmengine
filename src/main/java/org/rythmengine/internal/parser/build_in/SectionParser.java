/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.BlockCodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

/**
 * Parse @section mysection
 */
public class SectionParser extends KeywordParserFactory {

    public class SectionToken extends BlockCodeToken {
        private String section;

        public SectionToken(String section, IContext context) {
            super(null, context);
            if (null == section) throw new NullPointerException();
            this.section = section;
        }

        public String section() {
            return section;
        }

        @Override
        public void output() {
            p("\n__startSection(\"").p(section).p("\");\n");
        }

        @Override
        public String closeBlock() {
            return "\n__endSection();";
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
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain()))
                    raiseParseException("bad @section statement. Correct usage: @section(\"mySection\"){...}");
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
        return "\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))[\\s]*\\{?";
    }

}
