/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.extension.ICodeType;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.ParserBase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a type block is ended put
 * instruction in template java source to switch
 * back type context
 * <p/>
 * <p>For example when &lt;/script&gt; is reached
 * a instruction <code>popCodeType()</code>
 * should be put in place</p>
 */
public class CodeTypeBlockEndSensor extends ParserBase {

    public CodeTypeBlockEndSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public Token go() {
        IContext ctx = ctx();
        ICodeType curType = ctx.peekCodeType();
        if (curType.allowedExternalTypes().isEmpty()) return null;

        String remain = ctx.getRemain();

        String blockEnd = curType.blockEnd();
        if (null == blockEnd) {
            logger.warn("null block end found for type[%s]", curType);
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
            ctx.popCodeType();
            String s = String.format("p(\"%s\");__ctx.popCodeType();", matched);
            return new CodeToken(s, ctx);
        }

        return null;
    }
}
