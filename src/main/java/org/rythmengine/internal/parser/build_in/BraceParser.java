/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;

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

    private final Pattern P = Pattern.compile("^((\\n[ \\t\\x0B\\f]*\\}[ \\t\\x0B\\f]*)\\n).*", Pattern.DOTALL);

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public Token go() {
                String remain = remain();
                char c = remain.charAt(0);
                if ('{' == c) {
                    step(1);
//                    if (ctx().getCodeBuilder().lastIsBlockToken()) {
//                        // remove space followed by line break;
//                        int cnt = 0;
//                        while (ctx().hasRemain()) {
//                            char c0 = ctx().pop();
//                            cnt--;
//                            if (c0 == ' ' || c0 == '\t') {
//                                continue;
//                            } else if (c0 == '\n') {
//                                return new Token.StringToken("\n", ctx());
//                            } else {
//                                ctx().step(cnt);
//                                break;
//                            }
//                        }
//                        //ctx().getCodeBuilder().removeNextLF = true;
//                        return Token.EMPTY_TOKEN2;
//                    } else {
//                        return new BlockToken.LiteralBlock(ctx());
//                    }
                    return new BlockToken.LiteralBlock(ctx());
                } else {
                    IBlockHandler bh = ctx().currentBlock();
                    boolean isLiteral = null == bh ? false : bh instanceof BlockToken.LiteralBlock;
                    if ('}' == c) {
                        step(1);
                        if (null == bh) {
                            raiseParseException("no open block found");
                        }
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
                            } else if (bhCls.contains("Assign")) {
                                remain = ctx.getRemain();
                                Matcher m = Pattern.compile("(^[ \\t\\x0B\\f]*\\n).*", Pattern.DOTALL).matcher(remain);
                                if (m.matches()) {
                                    String space = m.group(1);
                                    step(space.length());
                                }
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
                            } else if (bhCls.contains("Assign") || bhCls.contains("DefTag")) {
                                if (m.group(1).endsWith("\n")) {
                                    cb.removeNextLF = true;
                                }
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
