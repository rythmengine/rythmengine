package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

public class ArgsParser extends BuildInParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ARGS;
    }

    @Override
    public IParser create(DialectBase dialect, IContext ctx) {
        return new ParserBase(dialect, ctx) {
            @Override
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String declares = s.replaceFirst(String.format("%s%s[\\s]+", a(), keyword()), "");
                return new Directive(declares, ctx()) {
                    Pattern p = Pattern.compile("[\\s,]*([a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*([\\s]*=([^\\n\\$]+))?)");
                    @Override
                    public void call() {
                        Matcher m = p.matcher(s);
                        while (m.find()) {
                            String declare = m.group();
                            declare = declare.replaceFirst("[\\s,]*", "");
                            String[] sa = declare.split("[\\s]+");
                            builder().addRenderArgs(sa[0], sa[1]);
                        }
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s([\\s,]+[a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*)+(;|\\r?\\n)+).*";
    }

}
