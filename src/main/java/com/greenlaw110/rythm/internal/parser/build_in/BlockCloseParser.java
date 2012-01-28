package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;


public class BlockCloseParser extends ParserBase {

    private static final String PTN = "([\\}]?%s[\\}\\s\\n]).*";
    
    public BlockCloseParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        IContext ctx = ctx();
        if (ctx.currentBlock() == null) return null;
        String remain = remain();
        String s;
        if ("@".equals(remain)) {
            s = remain;
        } else {
            Pattern p = Pattern.compile(String.format(PTN, a()), Pattern.DOTALL);
            Matcher m = p.matcher(ctx.getRemain());
            if (!m.matches()) return null;
            s = m.group(1);
        }
        ctx.step(s.length());
        try {
            s = ctx.closeBlock();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new CodeToken(s, ctx);
    }
    
    public static void main(String[] args) {
        String s = "@";
        Pattern p = Pattern.compile(String.format(PTN, "@"), Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
