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
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.Arrays;

/**
 * Parse @escape("JS") {...}
 */
public class EscapeParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ESCAPE;
    }

    public IParser create(IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
                int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                if (S.isEmpty(s)) s = "HTML";
                else if ("JavaScript".equalsIgnoreCase(s)) s = "JS";
                else s = s.toUpperCase();
                if (Arrays.binarySearch(ITemplate.Escape.stringValues(), s) < 0) {
                    raiseParseException("Error parsing @escape statement. Escape parameter expected to be one of %s, found: %s", Arrays.asList(ITemplate.Escape.stringValues()), s);
                }
                s = String.format("__ctx.pushEscape(com.greenlaw110.rythm.template.ITemplate.Escape.%s);", s);
                return new BlockCodeToken(s, ctx()) {
                    @Override
                    public void openBlock() {
                    }

                    @Override
                    public String closeBlock() {
                        return "__ctx.popEscape();";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\s]+\\{?\\s*";
    }

    public static void main(String[] args) {
        Regex r = new EscapeParser().reg(Rythm.INSTANCE);
        if (r.search("@escape(JS) \nab")) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }

}
