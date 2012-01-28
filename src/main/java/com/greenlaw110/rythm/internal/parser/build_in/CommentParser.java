package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

/**
 * CommentParser deals with the following type comments:
 * 1. inline comment. e.g. @//this is my comment \n
 * 2. block comment. e.g. @* this is my multi \nline comments *@
 * User: luog
 * Date: 2/12/11
 * Time: 3:04 PM
 */
public class CommentParser extends CaretParserFactoryBase {
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Pattern p = inlineComment();
                Matcher m = p.matcher(remain());
                if (!m.matches()) {
                    p = blockComment();
                    m = p.matcher(remain());
                    if (!m.matches()) return null;
                }
                String s = m.group(1);
                ctx().step(s.length());
                return new Directive(s, ctx());
            }

            private Pattern inlineComment() {
                return Pattern.compile(String.format("^(%s//.*?\n).*", a()), Pattern.DOTALL);
            }
            
            private Pattern blockComment() {
                return Pattern.compile(String.format("^(%s\\*.*?\\*%s).*", a(), a()), Pattern.DOTALL);
            }
        };
    }

    public static void main(String[] args) {
        Pattern p = Pattern.compile(String.format("^(%s//.*?\n).*", "@"), Pattern.DOTALL);
        Matcher m = p.matcher("@// abc.foo() @xyz, @if \n abcd adf @each ");
        if (m.matches()) {
            System.out.println(m.group(1));
        }

        p = Pattern.compile(String.format("^(%s\\*.*?\\*%s).*", "@", "@"), Pattern.DOTALL);
        m = p.matcher("@* @args include @each @a.b() #\n@//abc\nadfd *@ Hello world @abcd");
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }
}
