package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.IBlockHandler;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Pattern;

/**
 * <ul>Recognised the following patterns:
 * <li><code>@}? else if (...) {?...@}? </code></li>
 * <li><code>@ else ...@</code><li>
 *
 * @author luog
 */
public class ElseIfParser extends CaretParserFactoryBase {

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                IBlockHandler bh = ctx().currentBlock();
                if (null == bh || !(bh instanceof IfParser.IfBlockCodeToken)) return null;

                String a = dialect().a();
                Regex r1 = new Regex(String.format("^((\\n\\r|\\r\\n|[\\n\\r])?(%s\\}?|%s?\\})\\s*(else\\s*if\\s*" + Patterns.Expression + "[ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?)).*", a, a));
                Regex r2 = new Regex(String.format("^((\\n\\r|\\r\\n|[\\n\\r])?(%s\\}?|%s?\\})\\s*(else([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?))).*", a, a));

                String s = ctx.getRemain();
                String s1;
                boolean expression = false;
                if (r1.search(s)) {
                    s1 = r1.stringMatched(1);
                    if (null == s1) return null;
                    step(s1.length());
                    s1 = r1.stringMatched(4);
                    expression = true;
                } else if (r2.search(s)) {
                    s1 = r2.stringMatched(1);
                    if (null == s1) return null;
                    step(s1.length());
                    s1 = r2.stringMatched(4);
                } else {
                    return null;
                }
                Regex r = new Regex("}?\\s*else\\s+if\\s*((?@()))(\\s*\\{)?");
                if (expression && r.search(s1)) {
                    s1 = r.stringMatched(1);
                    s1 = ExpressionParser.processPositionPlaceHolder(s1);
                    s1 = "\n} else if (com.greenlaw110.rythm.utils.Eval.eval(" + s1 + ")) {";
                } else {
                    Pattern p = Pattern.compile(".*\\{(\\n\\r|\\r\\n|[\\n\\r])?", Pattern.DOTALL);
                    if (!p.matcher(s1).matches()) s1 = s1 + "{";
                    if (!s1.startsWith("}")) s1 = "}" + s1;
                }
                try {
                    ctx.closeBlock();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return new IfParser.IfBlockCodeToken(s1, ctx);
            }

        };
    }
}
