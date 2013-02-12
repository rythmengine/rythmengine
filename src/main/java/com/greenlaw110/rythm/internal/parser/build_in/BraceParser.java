package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 8/02/12
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class BraceParser implements IParserFactory {

    private final Pattern P = Pattern.compile("^((\\n\\s*}\\s*)\\n).*", Pattern.DOTALL);

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
                } else {
                    IBlockHandler bh = ctx().currentBlock();
                    boolean isLiteral = null == bh ? false : bh instanceof BlockToken.LiteralBlock;
                    if ('}' == c) {
                        step(1);
                        if (null == bh) raiseParseException("no open block found");
                        String s = ctx().closeBlock();
                        if (isLiteral) {
                            if ("".equals(s)) {
                                return Token.EMPTY_TOKEN;
                            } else {
                                return new Token.StringToken(s, ctx());
                            }
                        } else {
                            CodeToken ct = new CodeToken(s, ctx());
                            String bhCls = bh.getClass().getName();
                            if (bhCls.contains("For")) {
                                ctx.getCodeBuilder().removeSpaceTillLastLineBreak(ctx);
                                ct.removeNextLineBreak = true;
                            } else {
                                ctx.getCodeBuilder().removeSpaceToLastLineBreak(ctx);
                            }
                            if (S.isEmpty(s)) {
                                return Token.EMPTY_TOKEN;
                            } else {
                                return ct;
                            }
                        }
                    } else if (null != bh && !isLiteral) {
                        Matcher m = P.matcher(remain);
                        if (m.matches()) {
                            CodeBuilder cb = ctx.getCodeBuilder();
                            String bhCls = bh.getClass().getName();
                            String s = m.group(2);
                            if (bhCls.contains("For")) {
                                cb.addBuilder(new Token.StringToken("\n", ctx));
                                cb.removeNextLF = true;
                            }
                            ctx.step(s.length());
                            CodeToken ct = new CodeToken(ctx.closeBlock(), ctx);
                            return ct;
                        }
                    }
                }
                return null;
            }
        };
    }
}
