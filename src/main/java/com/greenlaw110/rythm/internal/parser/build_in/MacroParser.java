package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Define and invoke Macro
 */
public class MacroParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.MACRO;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) raiseParseException("bad @macro statement. Correct usage: @macro(\"macro-name\"){...}");
                int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                final String macro = S.stripBraceAndQuotation(s);
                return new BlockCodeToken("", ctx()) {
                    @Override
                    public void openBlock() {
                        ctx().getCodeBuilder().pushMacro(macro);
                    }

                    @Override
                    public String closeBlock() {
                        ctx().getCodeBuilder().popMacro();
                        return "";
                    }
                };
            }
        };
    }


    @Override
    protected String patternStr() {
        //return "(%s%s[\\s]+([a-zA-Z][a-zA-Z0-9_]+)[\\s\\r\\n\\{]*).*";
        return "%s%s\\s*((?@()))[\\s]*\\{?\\s*";
    }

    public static void main(String[] args) {
        Regex r = new MacroParser().reg(new Rythm());
        if (r.search("@macro(\"JS\") abc")) {
            p(r);
        }
    }

}
