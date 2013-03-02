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
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;

/**
 * Parse @compact() {...}
 */
public class CompactParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.COMPACT;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());
                if (!m.matches()) return null;
                final String matched = m.group(1);
                step(matched.length());
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
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
                return new BlockCodeToken("", ctx()) {
                    @Override
                    public void openBlock() {
                        ctx().getCodeBuilder().addBuilder(new Token("", ctx()) {
                            @Override
                            protected void output() {
                                ctx().pushCompact(true);
                                super.output();
                            }
                        });
                    }

                    @Override
                    public String closeBlock() {
                        ctx().getCodeBuilder().addBuilder(new Token("", ctx()) {
                            @Override
                            protected void output() {
                                ctx().popCompact();
                                super.output();
                            }
                        });
                        return "";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(\\n?[ \\t\\x0B\\f]*%s%s\\s*\\(\\s*\\)[\\s]*\\{?[ \\t\\x0B\\f]*\\n?).*";
    }

}
