/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.IBlockHandler;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
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
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {

            @Override
            public TextBuilder go() {
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
                final String matched;
                if (r1.search(s)) {
                    s1 = r1.stringMatched(1);
                    matched = s1;
                    if (null == s1) return null;
                    step(s1.length());
                    s1 = r1.stringMatched(4);
                    expression = true;
                } else if (r2.search(s)) {
                    s1 = r2.stringMatched(1);
                    if (null == s1) return null;
                    matched = s1;
                    step(s1.length());
                    s1 = r2.stringMatched(4);
                } else {
                    return null;
                }
                //boolean needsToAddLF = rLF.search(s);
                Regex r = new Regex("}?\\s*else\\s+if\\s*((?@()))(\\s*\\{)?");
                if (expression && r.search(s1)) {
                    s1 = r.stringMatched(1);
                    s1 = ExpressionParser.processPositionPlaceHolder(s1);
                    s1 = "\n} else if (com.greenlaw110.rythm.utils.Eval.eval(" + s1 + ")) {";
                } else {
                    Pattern p = Pattern.compile(".*\\{\\s?", Pattern.DOTALL);
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
                processFollowingOpenBraceAndLineBreak(false);
                return new IfParser.IfBlockCodeToken(s1, ctx, line);
            }

        };
    }
}
