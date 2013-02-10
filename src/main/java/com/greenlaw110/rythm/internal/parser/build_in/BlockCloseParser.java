package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BlockCloseParser extends ParserBase {

    private static final String PTN = "([\\}]?%s[\\}\\s\\n\\>\\]]).*";
    private static final String PTN2 = "((\\}%s|%s\\}|\\})([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?)).*";

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
            Pattern p = Pattern.compile(String.format(PTN2, a(), a(), a()), Pattern.DOTALL);
            Matcher m = p.matcher(remain);
            if (!m.matches()) {
                p = Pattern.compile(String.format(PTN, a()), Pattern.DOTALL);
                m = p.matcher(remain);
                if (!m.matches()) {
                    return null;
                }
            };
            s = m.group(1);
        }
        // keep ">" or "]" for case like <a id=".." @if (...) class="error" @>
        if (s.endsWith(">") || s.endsWith("]")) s = s.substring(0, s.length() - 1);
        ctx.step(s.length());
        boolean hasLineBreak = s.contains("\\n") || s.contains("\\r");
        try {
            s = ctx.closeBlock();
            if (hasLineBreak) s = s + "\n"; // fix #53
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ctx.removeImmediateLastLineBreak();
        return new CodeToken(s, ctx);
    }
}
