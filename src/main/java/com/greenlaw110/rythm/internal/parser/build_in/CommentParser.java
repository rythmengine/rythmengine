package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommentParser deals with the following type comments:
 * 1. inline comment. e.g. @//this is my comment \n
 * 2. block comment. e.g. @* this is my multi \nline comments *@
 * User: luog
 * Date: 2/12/11
 * Time: 3:04 PM
 */
public class CommentParser extends CaretParserFactoryBase {
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Pattern p = inlineComment();
                Matcher m = p.matcher(remain());
                if (!m.matches()) {
                    p = blockComment();
                    m = p.matcher(remain());
                    if (!m.matches()) return null;
                    ctx.removeImmediateLastLineBreak();
                } else {
                    // special process to directive comments
                    if (ctx.insideDirectiveComment()) {
                        ctx.leaveDirectiveComment();
                    }
                }
                String s = m.group(1);
                ctx.step(s.length());
                return Token.EMPTY_TOKEN;
            }

            private Pattern inlineComment() {
                return Pattern.compile(String.format("^(%s//.*?)(\n.*|$)", a()), Pattern.DOTALL);
//                IContext ctx = ctx();
//                if (ctx.insideDirectiveComment()) {
//                    return Pattern.compile(String.format("^(%s//.*?)(%s|\n).*", a(), S.escapeRegex(ctx.peekLang().commentEnd())), Pattern.DOTALL);
//                } else {
//                    return Pattern.compile(String.format("^(%s//.*?)\n.*", a()), Pattern.DOTALL);
//                }
            }

            private Pattern blockComment() {
                return Pattern.compile(String.format("^(%s\\*.*?\\*%s).*", a(), a()), Pattern.DOTALL);
            }
        };
    }
}
