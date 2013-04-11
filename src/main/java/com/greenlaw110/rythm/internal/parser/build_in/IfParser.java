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

import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingSpacesIfLineBreakParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IfParser extends KeywordParserFactory {

    public static class IfBlockCodeToken extends BlockCodeToken {
        public IfBlockCodeToken(String s, IContext context, int line) {
            super(s, context);
            this.line = line;
        }
    }
    
    @Override
    public IParser create(final IContext ctx) {
        return new RemoveLeadingSpacesIfLineBreakParser(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @if statement. Correct usage: @if (some-condition) {some-template-code}");
                }
                final String matched = r.stringMatched();
                int line = ctx.currentLine();
                boolean leadingLB = !isLastBuilderLiteral();
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
                    if (matched.startsWith("\n")) {
                        leadingLB = true;
                        line++;
                    }
                    ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                    if (!matched.startsWith("\n")) {
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
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
                String s = r.stringMatched(1);
                ctx().step(s.length());
                String sIf = r.stringMatched(3);
                s = r.stringMatched(4);
                s = ExpressionParser.processPositionPlaceHolder(s);
                if ("if".equalsIgnoreCase(sIf)) {
                    s = "\nif (com.greenlaw110.rythm.utils.Eval.eval(" + s + ")) {";
                } else {
                    s = "\nif (!com.greenlaw110.rythm.utils.Eval.eval(" + s + ")) {";
                }
                //if (!s.endsWith("{")) s = "\n" + s + " {";
                processFollowingOpenBraceAndLineBreak(leadingLB);
                return new IfBlockCodeToken(s, ctx(), line);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IF;
    }

    @Override
    protected String patternStr() {
        //return "(%s(%s\\s+\\(.*\\)(\\s*\\{)?)).*";
        return "(^\\n?[ \\t\\x0B\\f]*%s(%s\\s*((?@()))([ \\t\\x0B\\f]*\\n?))).*";
    }

}
