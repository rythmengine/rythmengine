/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.stevesoft.pat.Regex;
import org.rythmengine.exception.ParseException;
import org.rythmengine.internal.IBlockHandler;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.Patterns;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

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
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {

            @Override
            public Token go() {
                IBlockHandler bh = ctx().currentBlock();
                if (null == bh || !(bh instanceof IfParser.IfBlockCodeToken)) return null;

                String a = dialect().a();
                //Regex rLF = new Regex("^(\\n\\r|\\r\\n|[\\n\\r]).*");
                Regex r1 = new Regex(String.format("^((\\n\\r|\\r\\n|[\\n\\r])?[ \\t\\x0B\\f]*(%s\\}?|%s?\\})\\s*(else\\s*if\\s*" + Patterns.Expression + "[ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?)).*", a, a));
                Regex r2 = new Regex(String.format("^((\\n\\r|\\r\\n|[\\n\\r])?[ \\t\\x0B\\f]*(%s\\}?|%s?\\})\\s*(else([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?))).*", a, a));

                final String s = ctx.getRemain();
                int line = ctx.currentLine();
                String s1;
                boolean expression = false;
                boolean needsToProcessFollowingOpenBrace;
                final String matched;
                if (r1.search(s)) {
                    s1 = r1.stringMatched(1);
                    matched = s1;
                    if (null == s1) return null;
                    step(s1.length());
                    needsToProcessFollowingOpenBrace = !s1.trim().endsWith("{");
                    s1 = r1.stringMatched(4);
                    expression = true;
                } else if (r2.search(s)) {
                    s1 = r2.stringMatched(1);
                    if (null == s1) return null;
                    matched = s1;
                    step(s1.length());
                    needsToProcessFollowingOpenBrace = !s1.trim().endsWith("{");
                    s1 = r2.stringMatched(4);
                } else {
                    return null;
                }
                //boolean needsToAddLF = rLF.search(s);
                Regex r = new Regex("}?\\s*else\\s+if\\s*((?@()))(\\s*\\{)?");
                if (expression && r.search(s1)) {
                    s1 = r.stringMatched(1);
                    s1 = ExpressionParser.processPositionPlaceHolder(s1);
                    s1 = "\n} else if (org.rythmengine.utils.Eval.eval(" + s1 + ")) {";
                } else {
                    Pattern p = Pattern.compile(".*\\{\\s?\\n?", Pattern.DOTALL);
                    if (!p.matcher(s1).matches()) s1 = s1 + "{";
                    if (!s1.startsWith("}")) s1 = "}" + s1;
                }
                try {
                    if (matched.startsWith("\n") || matched.endsWith("\n")) {
                        if (matched.startsWith("\n")) line++;
                        ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    } else {
                        Regex r0 = new Regex("([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    }
                    ctx.closeBlock();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
//                if (needsToAddLF) {
//                    ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
//                }
                if (needsToProcessFollowingOpenBrace) {
                    processFollowingOpenBraceAndLineBreak(false);
                }
                return new IfParser.IfBlockCodeToken(s1, ctx, line);
            }

        };
    }
}
