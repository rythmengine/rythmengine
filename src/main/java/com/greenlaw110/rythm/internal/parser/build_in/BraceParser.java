package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 8/02/12
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class BraceParser implements IParserFactory {

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                String remain = remain();
                char c = remain.charAt(0);
                if ('{' == c) {
                    step(1);
                    return new BlockToken.LiteralBlock(ctx());
                } else if ('}' == c) {
                    step(1);
                    IBlockHandler bh = ctx().currentBlock();
                    if (null == bh) raiseParseException("no open block found");
                    boolean isLiteral = bh instanceof BlockToken.LiteralBlock;
                    String s = ctx().closeBlock();
                    if (!isLiteral) {
                        ctx.removeImmediateLastLineBreak();
                    }
                    return isLiteral ? new Token.StringToken(s, ctx()) : new CodeToken(s, ctx());
                }
                return null;
            }
        };
    }
}
