package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.Sandbox;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.S;
import com.stevesoft.pat.Regex;

/**
 * Free Java code parser.
 *
 * All code between @{ and }@ will be copied literally into target java source
 *
 * @author luog
 */
public class ScriptParser extends ParserBase {


    private static final String PTN = "^(%s((?@{}))%s?[\\r\\n]*)";

    public ScriptParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        IContext ctx = ctx();
        //if (ctx.currentBlock() == null) return null;
        Regex r = new Regex(String.format(PTN, a(), a()));
        if (!r.search(ctx.getRemain())) return null;
        if (!ctx.getDialect().enableScripting()) {
            throw new TemplateParser.ScriptingDisabledException(ctx);
        }
        String s = r.stringMatched(1);
        int curLine = ctx.currentLine();
        ctx.step(s.length());
        s = r.stringMatched(2);
        s = s.substring(1); // strip left "{"
        s = s.substring(0, s.length() - 1); // strip right "}"
        String[] lines = s.split("[\\n\\r]+");
        int len = lines.length;
        StringBuilder sb = new StringBuilder(s.length() * 2);
        String lastLine = "";
        for (int i = 0; i < len; ++i) {
            String line = lines[i];
            if (!S.isEmpty(line)) lastLine = line;
            sb.append(line).append(" //line: ").append(curLine++).append("\n");
        }
        if (!lastLine.trim().endsWith(";")) sb.append(";");
        String code = sb.toString();
        checkRestrictedClass(code);
        return new CodeToken(code, ctx);
    }

    public static void main(String[] args) {
        String s = "xd@{for() { xb\n\r;}}@\nabc";
        Regex r = new Regex(String.format(PTN, "@", "@"));
        if (r.search(s)) {
            //System.out.println(r.stringMatched());
            System.out.println(1 + r.stringMatched(1));
            System.out.println(2 + r.stringMatched(2));
            s = r.stringMatched(2);
            s = s.substring(1); // strip left "{"
            s = s.substring(0, s.length() - 1); // strip right "}"
            System.out.println(s);
        }
    }
}
