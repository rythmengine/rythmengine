package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.util.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Free Java code parser.
 *
 * All code between @{ and }@ will be copied literally into target java source
 * 
 * @author luog
 */
public class ScriptParser extends ParserBase {


    private static final String PTN = "(%s\\{(.*)\\}%s).*";

    public ScriptParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        IContext ctx = ctx();
        if (ctx.currentBlock() == null) return null;
        Pattern p = Pattern.compile(String.format(PTN, a(), a()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.step(s.length());
        s = m.group(2);
        return new CodeToken(s, ctx);
    }

    public static void main(String[] args) {
        String s = "@{\n\tString s = \"Hello world\";for(String s: ls){sys;}\n}@";
        Pattern p = Pattern.compile(String.format(PTN, "@", "@"), Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
    }
}
