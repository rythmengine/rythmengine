/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.exception.ParseException;
import org.rythmengine.internal.IBlockHandler;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.BlockCodeToken;
import org.rythmengine.internal.parser.RemoveLeadingSpacesIfLineBreakParser;

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
            public Token go() {
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
                processFollowingOpenBraceAndLineBreak(false);
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
