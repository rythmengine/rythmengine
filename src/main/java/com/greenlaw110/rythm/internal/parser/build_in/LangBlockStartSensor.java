package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.RythmEngine;
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
 * Detect if a lang block is reached and put
 * instruction in template java source to switch
 * lang context
 * <p/>
 * <p>For example when &lt;script &gt; is reached
 * a instruction <code>pushLang(ILang lang)</code>
 * should be put in place</p>
 */
public class LangBlockStartSensor extends ParserBase {
    ILogger logger = Logger.get(LangBlockStartSensor.class);

    public LangBlockStartSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        ILang curLang = ctx.peekLang();
        if (!curLang.allowInternalLang()) return null;

        String remain = ctx.getRemain();
        Iterable<ILang> langs = ctx.getEngine().extensionManager().templateLangs();

        for (ILang lang : langs) {
            if (lang.allowedExternalLangs().contains(curLang)) {
                String blockStart = lang.blockStart();
                if (null == blockStart) {
                    logger.warn("null block start found for lang[%s] inside lang[%s]", lang, curLang);
                    continue;
                }

                Pattern pStart = patterns.get(blockStart);
                if (null == pStart) {
                    pStart = Pattern.compile(blockStart, Pattern.DOTALL);
                    patterns.put(blockStart, pStart);
                }
                Matcher m = pStart.matcher(remain);
                if (m.matches()) {
                    String matched = m.group(1);
                    ctx.step(matched.length());
                    ctx.pushLang(lang);
                    String s = matched;
                    s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
                    
                    s = String.format("p(\"%s\");__ctx.pushLang(%s);", s, lang.newInstanceStr());
                    return new CodeToken(s, ctx);
                } else {
                }
            }
        }
        return null;
    }
}
