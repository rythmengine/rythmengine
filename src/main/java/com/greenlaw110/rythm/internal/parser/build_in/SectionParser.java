package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String section = m.group(3);
                return new SectionToken(section, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s[\\s]+([a-zA-Z][a-zA-Z0-9_]+)[\\s\\r\\n\\{]*).*";
    }
    
    public static void main(String[] args) {
        String s = String.format(new SectionParser().patternStr(), "@", Keyword.SECTION);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher("@section abc_xyz {Hello world}@");
        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(3));
        }
    }

}
