package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.IBlockHandler;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingSpacesIfLineBreakParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * else branch for @for loop, executed in case the loop variable is empty
 *
 * @author luog
 */
public class ElseForParser extends CaretParserFactoryBase {

    @Override
    public IParser create(final IContext ctx) {
        return new RemoveLeadingSpacesIfLineBreakParser(ctx) {

            @Override
            public TextBuilder go() {
                IBlockHandler bh = ctx().currentBlock();
                if (null == bh || !(bh instanceof ForEachCodeToken)) return null;

                String a = dialect().a();
                Regex r = new Regex(String.format("^((\\n\\r|\\r\\n|[\\n\\r])?(%s\\}?|%s?\\})\\s*(else([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?))).*", a, a));

                String s = ctx.getRemain();
                String s1;
                if (r.search(s)) {
                    s1 = r.stringMatched(1);
                    if (null == s1) return null;
                    step(s1.length());
                    //s1 = r.stringMatched(3);
                } else {
                    return null;
                }
                //if (!s1.endsWith("{")) s1 = s1 + "{";

                try {
                    ctx.closeBlock();
                    s1 = "\n\t}\n} else {\n";
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                BlockCodeToken tk = new BlockCodeToken(s1, ctx) {
                    @Override
                    public String closeBlock() {
                        return "}}";
                    }
                };
                return tk;
            }

        };
    }
}
