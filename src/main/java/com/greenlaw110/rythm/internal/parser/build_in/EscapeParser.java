package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
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
import java.util.regex.Pattern;

/**
 * Parse @raw() {...}
 */
public class EscapeParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ESCAPE;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
                int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                if (S.isEmpty(s)) s = "HTML";
                else if ("JavaScript".equalsIgnoreCase(s)) s = "JS";
                else s = s.toUpperCase();
                if (Arrays.binarySearch(ITemplate.Escape.stringValues(), s) < 0) {
                     raiseParseException("Error parsing @escape statement. Escape parameter expected to be one of %s, found: %s", Arrays.asList(ITemplate.Escape.stringValues()), s);
                }
                s = String.format("__ctx.pushEscape(com.greenlaw110.rythm.template.ITemplate.Escape.%s);", s);
                return new BlockCodeToken(s, ctx()) {
                    @Override
                    public void openBlock() {
                    }

                    @Override
                    public String closeBlock() {
                        return "__ctx.popEscape();";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\s]+\\{?\\s*";
    }

    public static void main(String[] args) {
        Regex r = new EscapeParser().reg(new Rythm());
        if (r.search("@escape(JS) \nab")) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }

}
