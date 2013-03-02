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
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Parse @cacheFor("1m")
 */
public class CacheParser extends KeywordParserFactory {

    private static final Pattern P_INT = Pattern.compile("\\-?[0-9\\*\\/\\+\\-]+");

    public static void validateDurationStr(String d, IContext ctx) {
        if ("null".equals(d)) return;
        if ((d.startsWith("\"") && d.endsWith("\""))) {
            String s = S.stripQuotation(d);
            try {
                ctx.getEngine().conf().durationParser().parseDuration(s);
            } catch (Exception e) {
                raiseParseException(ctx, "Invalid time duration: %s", d);
            }
        } else {
            if (!P_INT.matcher(d).matches()) {
                raiseParseException(ctx, "Invalid time duration: %s. int(second) or string expected. String must be double quoted", d);
            }
        }
    }

    /*
    {
      String s = __engine().cached("key", 1, foo.bar());
      if (null != s) {
        p(s);
      } else {
        StringBuilder sbOld = __getBuffer();
        StringBuilder sbNew = new StringBuilder()
        __setBuffer(sbNew);
        ...
        s = sbNew.toString();
        __setBuffer(sbOld);
        __engine().cache("key", s, duration, 1, foo.bar());
        p(s)
      }
    }
     */
    private static class CacheToken extends BlockCodeToken {
        private String args;
        private String duration;
        private int startIndex;
        private int endIndex;
        private String key;

        CacheToken(String duration, String args, IContext ctx) {
            super("", ctx);
            this.duration = S.isEmpty(duration) ? "null" : duration;
            // check if duration is valid
            validateDurationStr(this.duration, ctx);
            this.args = args;
            this.startIndex = ctx.cursor();
        }

        @Override
        public void output() {
            p("{");
            pline();
            pt("java.io.Serializable s = __engine().cached(\"").p(key).p("\"").p(args).p(");");
            pline();
            pt("if (null != s) {");
            pline();
            p2t("p(s);");
            pline();
            pt("} else {");
            pline();
            p2t("StringBuilder sbOld = __getBuffer();");
            pline();
            p2t("StringBuilder sbNew = new StringBuilder();");
            pline();
            p2t("__setBuffer(sbNew);");
            pline();
        }

        @Override
        public String closeBlock() {
            endIndex = ctx.cursor();
            String body = ctx.getTemplateSource(startIndex, endIndex);
            String tmplName = ctx.getTemplateClass().name();
            String keySeed = body + tmplName;
            key = UUID.nameUUIDFromBytes(keySeed.getBytes()).toString();
            StringBuilder sbOld = __getBuffer();
            StringBuilder sbNew = new StringBuilder();
            __setBuffer(sbNew);
            p2t("s = sbNew.toString();");
            pline();
            p2t("__setBuffer(sbOld);");
            pline();
            p2t("__engine().cache(\"").p(key).p("\",s,").p(duration).p(args).p(");");
            pline();
            p2t("p(s);");
            pline();
            pt("}");
            pline();
            p("}");
            pline();
            String s = sbNew.toString();
            __setBuffer(sbOld);
            return s;
        }
    }

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @cache statement. Correct usage: @cache (\"duration_string\") {cache block}");
                }
                String matched = r.stringMatched();
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
                ctx.step(r.stringMatched().length());
                String s = r.stringMatched(2); // ("1m", 1, bar.foo())
                s = S.stripBrace(s); // "1m", 1, bar.foo()
                String[] sa = s.split(",");
                String duration = null;
                if (sa.length > 0) duration = sa[0]; // "1m"
                String args = "";
                if (sa.length > 1) {
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 1; i < sa.length; ++i) {
                        sb.append(",").append(sa[i]);
                    }
                    args = sb.toString();
                }
                return new CacheToken(duration, args, ctx());
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.CACHE;
    }

    @Override
    protected String patternStr() {
        //return "(%s(%s\\s+\\(.*\\)(\\s*\\{)?)).*";
        return "^(\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))(\\s*\\{)?\\n?)";
    }

}
