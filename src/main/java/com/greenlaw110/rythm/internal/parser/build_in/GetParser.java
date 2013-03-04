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

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @get("name")
 */
public class GetParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.GET;
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s((?@())))";
    }

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @get call. Correct usage: @get(\"myVal\")");
                }
                final String matched = r.stringMatched();
                int line = ctx.currentLine();
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
                    if (matched.startsWith("\n")) line = line + 1;
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
                step(r.stringMatched().length()); // remain: @get("name")...
                String s = r.stringMatched(2); // s: ("name")
                s = s.substring(1); // s: "name")
                s = s.substring(0, s.length() - 1); // s: "name"
                r = new Regex("(((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)(\\s*[:=,]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?)");

                if (!r.search(s)) {
                    raiseParseException("Error parsing @get tag. Correct usage: @get(\"name\"[:default-value])");
                }
                s = r.stringMatched(2); // propName: "name"
                if (s.startsWith("\"") || s.startsWith("'")) {
                    s = s.substring(1);
                    s = s.substring(0, s.length() - 1);
                    // propName: name
                }
                final String propName = s;
                final String propDefVal = r.stringMatched(4);
                return new Token("", ctx()) {
                    @Override
                    protected void output() {
                        p("\np(__getRenderProperty(\"").p(propName).p("\", ").p(propDefVal == null ? "null" : propDefVal).p("));");
                    }
                };
            }
        };
    }

}
