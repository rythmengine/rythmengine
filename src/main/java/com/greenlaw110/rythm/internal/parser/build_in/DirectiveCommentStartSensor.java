package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a directive comment is reached and strip
 * it out from the parsing process
 * <p/>
 * <p>For example when &lt;!-- @&gt; is found the
 * &lt;!-- should be stripped out</p>
 */
public class DirectiveCommentStartSensor extends ParserBase {
    ILogger logger = Logger.get(DirectiveCommentStartSensor.class);

    public DirectiveCommentStartSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        if (ctx.insideDirectiveComment()) {
            return null;
            //raiseParseException("directive comment not closed");
        }
        ILang lang = ctx.peekLang();
        while (null != lang) {
            String sCommentStart = lang.commentStart();
            sCommentStart = S.escapeRegex(sCommentStart).toString();
            // try <!-- @ first
            String s = "(" + sCommentStart + "\\s*" + ")" + ctx.getDialect().a() + ".*";
            Pattern p = patterns.get(s);
            if (null == p) {
                p = Pattern.compile(s, Pattern.DOTALL);
                patterns.put(s, p);
            }
            Matcher m = p.matcher(remain());
            if (m.matches()) {
                s = m.group(1);
                ctx.step(s.length());
                ctx.enterDirectiveComment();
                return new TextBuilder();
            }
            // try <!-- }
            s = "(" + sCommentStart + "\\s*)\\}.*";
            p = patterns.get(s);
            if (null == p) {
                p = Pattern.compile(s, Pattern.DOTALL);
                patterns.put(s, p);
            }
            m = p.matcher(remain());
            if (m.matches()) {
                s = m.group(1);
                ctx.step(s.length());
                ctx.enterDirectiveComment();
                return new TextBuilder();
            }
            lang = lang.getParent();
        }

        return null;
    }

    public static void main(String[] args) {
    }
}
