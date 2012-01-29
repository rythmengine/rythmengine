package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Parse @tag tagname
 */
public class DefTagParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.DEFTAG;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String tagName = m.group(2);
                return new Directive(tagName, ctx()) {
                    @Override
                    public void call() {
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s\\s+\"?("+ PatternStr.VarName +")\"?[;\\s\\r\\n]*).*";
    }
    
    public static void main(String[] args) {
        DefTagParser tp = new DefTagParser();
        Pattern p = Pattern.compile(String.format(tp.patternStr(), "@", Keyword.DEFTAG));
        Matcher m = p.matcher("@deftag \"abc\";");
        if (m.matches()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
    }

}
