package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.internal.IBlockHandler;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * else branch for @for loop, executed in case the loop variable is empty
 *
 * @author luog
 *
 */
public class ElseForParser extends CaretParserFactoryBase {

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                IBlockHandler bh = ctx().currentBlock();
                if (null == bh || ! (bh instanceof ForEachCodeToken)) return null;

                String a = dialect().a();
                Regex r = new Regex(String.format("^((%s\\}?|%s?\\})\\s*(else([\\s\\r\\n\\t]*(\\{|[\\s\\r\\n\\t]+)))).*", a, a));

                String s = ctx.getRemain();
                String s1 = null;
                if (r.search(s)) {
                    s1 = r.stringMatched(1);
                    if (null == s1) return null;
                    step(s1.length());
                    s1 = r.stringMatched(3);
                } else {
                    return null;
                }
                if (!s1.endsWith("{")) s1 = s1 + "{";

                try {
                    s1 = ctx.closeBlock() + s1;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return new BlockCodeToken(s1, ctx);
            }

        };
    }

    public static void main(String[] args) {
        Regex r1 = new Regex(String.format("^((%s\\}?|%s?\\})\\s*(else\\s*if\\s*" + Patterns.Expression + "\\s*\\{?)).*", "@", "@"));
        String a = "@";
        Regex r2 = new Regex(String.format("^((%s\\}?|%s?\\})\\s*(else([\\s\\r\\n\\t]*(\\{|[\\s\\r\\n\\t]+)))).*", a, a));
        String s = "";

    }
}
