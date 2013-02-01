package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.ILang;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a lang block is reached and put
 * instruction in template java source to switch
 * lang context
 * 
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
        Iterable<ILang> langs = ctx.getEngine().getExtensionManager().templateLangs();
        
        for (ILang lang: langs) {
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
                    String s = String.format("p(\"%s\");__ctx.pushLang(%s);", matched, lang.newInstanceStr());
                    return new CodeToken(s, ctx);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String template = "<p>@1</p>\n\t<script>\n\t\talert('@2');\n\t</script>\n<p>@3</p>";
        String p1 = "'<h2>abc<h2>'";
        String p2 = "'<h1>xyz<h1>'";
        String p3 = "'<h3>123<h3>'";
    
        System.out.println("--- smart escape enabled ---");
        System.setProperty("rythm.enableSmartEscape", "true");
        RythmEngine engine1 = new RythmEngine();
        String s1 = engine1.render(template, p1, p2, p3);
        System.out.println(s1);
        
        System.out.println("\n\n--- smart escape disabled ---");
        System.setProperty("rythm.enableSmartEscape", "false");
        RythmEngine engine2 = new RythmEngine();
        String s2 = engine2.render(template, p1, p2, p3);
        System.out.println(s2);
    }
}
