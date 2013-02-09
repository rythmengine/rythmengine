package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a lang block is ended put
 * instruction in template java source to switch
 * back lang context
 * <p/>
 * <p>For example when &lt;/script&gt; is reached
 * a instruction <code>popLang()</code>
 * should be put in place</p>
 */
public class LangBlockEndSensor extends ParserBase {
    ILogger logger = Logger.get(LangBlockEndSensor.class);

    public LangBlockEndSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        ILang curLang = ctx.peekLang();
        if (curLang.allowedExternalLangs().isEmpty()) return null;

        String remain = ctx.getRemain();

        String blockEnd = curLang.blockEnd();
        if (null == blockEnd) {
            logger.warn("null block end found for lang[%s]", curLang);
            return null;
        }

        Pattern p = patterns.get(blockEnd);
        if (null == p) {
            p = Pattern.compile(blockEnd, Pattern.DOTALL);
            patterns.put(blockEnd, p);
        }
        Matcher m = p.matcher(remain);
        if (m.matches()) {
            String matched = m.group(1);
            ctx.step(matched.length());
            ctx.popLang();
            String s = String.format("p(\"%s\");__ctx.popLang();", matched, curLang);
            return new CodeToken(s, ctx);
        }

        return null;
    }
}
