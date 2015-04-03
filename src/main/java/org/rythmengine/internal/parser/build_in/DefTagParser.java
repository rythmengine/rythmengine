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
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;
import org.rythmengine.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @tag [return-type] tagname(Type var,...) {template...}
 */
public class DefTagParser extends KeywordParserFactory {

    private static class DefTagToken extends BlockToken {
        String tagName;
        String signature;
        String retType;
        CodeBuilder.InlineTag tag;

        public DefTagToken(String tagName, String retType, String signature, String body, IContext context) {
            super("", context);
            this.retType = retType;
            this.tagName = tagName;
            this.signature = signature;
            tag = ctx.getCodeBuilder().defTag(tagName, retType, signature, body);
        }

        @Override
        public void openBlock() {
        }

        @Override
        public String closeBlock() {
            ctx.getCodeBuilder().endTag(tag);
            return "";
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.TAG;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @def, correct usage: @def [type] tagName([arguments...])");
                }
                final String matched = r.stringMatched();
                if (matched.startsWith("\n")) {
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
                step(matched.length());
                String retType = r.stringMatched(3);
                String tagName = r.stringMatched(6);
                String signature = r.stringMatched(7);
                if (null != retType && !"void".equals(retType)) {
                    r = new Regex("^(\\s*((?@{})))");
                    String remain = ctx.getRemain();
                    if (!r.search("{" + remain)) {
                        // short notation?
                        r = new Regex("^(\\s*((?@@@)))");
                        if (!r.search("@" + remain)) {
                            this.raiseParseException("code blocked expected after @def tag");
                        }
                    }
                    String s = r.stringMatched(1);
                    int curLine = ctx().currentLine();
                    ctx().step(s.length() - 1);
                    if (s.startsWith("{")) {
                        while (ctx().peek() != '}') ctx().step(-1);
                    } else {
                        while (ctx().peek() != '@') ctx().step(-1);
                    }
                    s = r.stringMatched(2);
                    s = s.substring(1); // strip left "{"
                    s = s.substring(0, s.length() - 1); // strip right "}"
                    r = new Regex(".*[ \\t\\n\\r\\}]+if[ \\t]*\\(.*");
                    boolean hasIfStatement = r.search(" " + s);
                    String[] lines = s.split("[\\n\\r]+");
                    int len = lines.length;
                    StringBuilder sb = new StringBuilder(s.length() * 2);
                    String lastLine = "";
                    for (int i = 0; i < len; ++i) {
                        String line = lines[i];
                        if (!S.isEmpty(line)) lastLine = line;
                        sb.append(line).append(" //line: ").append(curLine++).append("\n");
                    }
                    if (!hasIfStatement && !lastLine.trim().endsWith(";")) sb.append(";");
                    return new DefTagToken(tagName, retType, signature, sb.toString(), ctx());
                }
                return new DefTagToken(tagName, retType, signature, null, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^\\n?[ \\t\\x0B\\f]*%s%s\\s+(([_a-zA-Z][\\w_\\.$]*(\\s*((?@<>)|(?@[])))?)\\s+)?([_a-zA-Z][\\w_$]*)\\s*((?@()))\\s*{?[ \\t\\x0B\\f]*\\n?";
    }

}
