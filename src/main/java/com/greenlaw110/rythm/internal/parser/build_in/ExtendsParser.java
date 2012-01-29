package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Parse @extends path/to/mylayout.html or @extends path.to.mylayout.html
 */
public class ExtendsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.EXTENDS;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String extend = m.group(2);
                return new Directive(extend, ctx()) {
                    @Override
                    public void call() {
                        builder().setExtended(s);
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s[\\s]+([a-zA-Z\\\\\\\\/][a-zA-Z0-9_\\.\\\\\\\\/]+)\\s*(;|\\r?\\n)+).*";
    }
    
    public static void main(String[] args) {
        Pattern p = Pattern.compile("(@extends([\\s]+[a-zA-Z\\\\\\\\/][a-zA-Z0-9_\\.\\\\\\\\/]+)\\s*(;|\\r?\\n)+).*");
        Matcher m = p.matcher("@extends ab.cd.foo;abc");
        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
    }

}
