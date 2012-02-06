package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

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
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    throw new ParseException(currentLine(), "Error parsing extends statement. The correct format is @extends(\"my.parent.template\")");
                }
                step(r.stringMatched().length());
                // try to match @extends(...)
                String s = r.stringMatched(3);
                if (null == s) {
                    // try to match @extends ...
                    s = r.stringMatched(2);
                }
                s = s.trim();
                Pattern p = Pattern.compile("('([_a-zA-Z][\\w_\\.]*)'|([_a-zA-Z][\\w_\\.]*)|\"([_a-zA-Z][\\w_\\.]*)\")");
                Matcher m = p.matcher(s);
                if (!m.matches()) {
                    throw new ParseException(currentLine(), "Error parsing extends statement. The correct format is @extends(\"my.parent.template\"), found: %s", s);
                }
                if (s.startsWith("\"")) {
                    s = m.group(4);
                } else if (s.startsWith("'")) {
                    s = m.group(2);
                } else {
                    s = m.group(1);
                }
                return new Directive(s, ctx()) {
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
        return "(%s%s(\\s*\\((.*)\\)|\\s+([_a-zA-Z\\\\\\\\/][a-zA-Z0-9_\\.\\\\\\\\/]+))[;]?)";
    }
    
    public static void main(String[] args) {
        Regex r = new ExtendsParser().reg(new Rythm());
        String s = "@extends(\"ab.cd.foo\"); acd";
//        if (r.search(s)) {
//            System.out.println(r.stringMatched());
//            System.out.println(r.stringMatched(1));
//            System.out.println(r.stringMatched(2));
//            System.out.println(r.stringMatched(3));
//        }
        
        s = "main.rythm.html";

        //Pattern p = Pattern.compile("('([_a-zA-Z][\\w_\\.]*)'|([_a-zA-Z][\\w_\\.]*)|\"([_a-zA-Z][\\w_\\.]*)\")");
        Pattern p = Pattern.compile("('([_a-zA-Z][\\w_\\.]*)'|([_a-zA-Z][\\w_\\.]*)|\"([_a-zA-Z][\\w_\\.]*)\")");
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
            System.out.println(m.group(4));
        }
    }

}
