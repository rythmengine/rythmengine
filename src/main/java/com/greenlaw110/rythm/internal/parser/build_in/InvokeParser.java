package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IKeyword;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;

/**
 * Parse @invoke("tagname", ...) {body}
 */
public class InvokeParser extends KeywordParserFactory {
    @Override
    public IKeyword keyword() {
        return Keyword.INVOKE;
    }

    @Override
    protected String patternStr() {
        return "(^%s(%s\\s*((?@()))\\s*)(\\.cache((?@())))?(\\.ignoreNonExistsTag(?@()))?)";
    }

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @invoke statement. Correct usage: @invoke(\"tagname\", ...)");
                }
                String matched = r.stringMatched();
                step(matched.length());
                boolean ignoreNonExistsTag = matched.indexOf("ignoreNonExistsTag") != -1;
                String invocation = r.stringMatched(3);
                invocation = S.stripBrace(invocation);
                // get tag name
                int pos = invocation.indexOf(",");
                String tagName, params;
                if (-1 == pos) {
                    tagName = invocation;
                    params = "";
                } else {
                    tagName = invocation.substring(0, pos);
                    params = invocation.substring(pos + 1);
                }
                String cacheFor = r.stringMatched(5);
                boolean enableCacheFor = false;
                String cacheForDuration = null;
                String cacheForArgs = null;
                if (null != cacheFor) {
                    enableCacheFor = true;
                    cacheFor = S.stripBrace(cacheFor); // "1h",1,foo.bar()
                    String[] cacheForArray = cacheFor.split(",");
                    if (cacheForArray.length > 0) {
                        cacheForDuration = cacheForArray[0]; // "1h"
                    }
                    if (cacheForArray.length > 1) {
                        cacheForArgs = cacheFor.replaceFirst(cacheForDuration, "");
                    }
                }
                String s = remain();
                Matcher m0 = InvokeTagParser.P_HEREDOC_SIMBOL.matcher(s);
                Matcher m1 = InvokeTagParser.P_STANDARD_BLOCK.matcher(s);
                if (m0.matches()) {
                    ctx().step(m0.group(1).length());
                    return new InvokeTagParser.InvokeTagWithBodyToken(tagName, params, enableCacheFor, cacheForDuration, cacheForArgs, ctx(), ignoreNonExistsTag);
                } else if (m1.matches()) {
                    ctx().step(m1.group(1).length());
                    return new InvokeTagParser.InvokeTagWithBodyToken(tagName, params, enableCacheFor, cacheForDuration, cacheForArgs, ctx(), ignoreNonExistsTag);
                } else {
                    return new InvokeTagParser.InvokeTagToken(tagName, params, enableCacheFor, cacheForDuration, cacheForArgs, ctx(), ignoreNonExistsTag);
                }
            }
        };
    }

    public static void main(String[] args) {
        Regex r = new InvokeParser().reg(new Rythm());
        String s = "@invoke(\"hello.world\" + foo.bar(), 1, 2, 3).cache(\"1h\", foo).ignoreNonExistsTag() \nxyz";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
            System.out.println(r.stringMatched(4));
            System.out.println(r.stringMatched(5));
        }

        s = r.stringMatched(3);
        s = S.stripBrace(s);
        int pos = s.indexOf(",");
        System.out.println(s.substring(0, pos));
        System.out.println(s.substring(pos + 1));
    }
}
