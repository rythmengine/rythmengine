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

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.dialect.SimpleRythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Single line expression parser
 *
 * @author luog
 */
public class ExpressionParser extends CaretParserFactoryBase {

    /**
     * Return symbol with transformer extension stripped off 
     * 
     * @param symbol
     * @param context
     * @return
     */
    public static String assertBasic(String symbol, IContext context) {
        if (symbol.contains("_utils.sep(\"")) return symbol;// Rythm builtin expression TODO: generalize
        //String s = Token.stripJavaExtension(symbol, context);
        //s = S.stripBrace(s);
        String s = symbol;
        boolean isSimple = Patterns.VarName.matches(s);
        IContext ctx = context;
        if (!isSimple) {
            throw new TemplateParser.ComplexExpressionException(ctx);
        }
        return s;
    }

    static class ExpressionToken extends CodeToken {

        public ExpressionToken(String s, IContext context) {
            super(s, context);
            checkRestrictedClass(ctx, s);
            if (context.getDialect() instanceof BasicRythm) {
                if (s.startsWith("(")) {
                    s = S.stripBrace(s);
                }
                // basic rythm dialect support only simple expression
                s = assertBasic(s, context);
                // find out array and it's dimension
                int d = 0;
                for (int i = 0; i < s.length(); ++i) {
                    if (s.charAt(i) == '[') d++;
                }
                int pos = s.indexOf("[");
                if (pos != -1) {
                    s = s.substring(0, pos);
                }
                String type = "Object";
                for (int i = 0; i < d; ++i) {
                    type = type + "[]";
                }
                context.getCodeBuilder().addRenderArgsIfNotDeclared(context.currentLine(), type, s);
            }
        }

        @Override
        public void output() {
            boolean needsPrint = true;
            int pos = s.indexOf("(");
            if (pos != -1) {
                String tagName = s.substring(0, pos).trim();
                if (!S.isEmpty(tagName)) {
                    needsPrint = ctx.getCodeBuilder().needsPrint(tagName);
                }
            }
            outputExpression(needsPrint);
        }
    }

    public static String processPositionPlaceHolder(String s) {
        Regex r = new Regex("@([0-9]+)", "__v_${1}");
        return r.replaceAll(s);
    }

    public static String reversePositionPlaceHolder(String s) {
        Regex r = new Regex("__v_([0-9]+)", "@${1}");
        return r.replaceAll(s);
    }

    @Override
    public IParser create(IContext ctx) {

        Regex r1_ = null, r2_ = null;
        String caret_ = null;
        final IDialect dialect = ctx.getDialect();
        if (dialect instanceof Rythm || dialect instanceof SimpleRythm) {
            caret_ = dialect.a();
            r1_ = new Regex(String.format(patternStr(), caret_));
            r2_ = new Regex(String.format("^(%s(?@())*).*", caret_));
        }
        final Regex r1 = r1_, r2 = r2_;
        final String caret = caret_;
        if (null == r1 || null == r2) {
            throw new DialectNotSupportException(dialect.id());
        }

        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                String s = remain();
                if (r1.search(s)) {
                    s = r1.stringMatched(1);
                    if (null != s && !caret.equals(s.trim())) {
                        step(s.length());
                        s = processPositionPlaceHolder(s);
                        s = s.replaceFirst(caret, "");
                        return new ExpressionToken(s, ctx());
                    }
                }
                s = remain();
                if (r2.search(s)) {
                    s = r2.stringMatched(1);
                    if (null != s && !"@".equals(s.trim())) {
                        step(s.length());
                        s = processPositionPlaceHolder(s);
                        return new ExpressionToken(s.replaceFirst(caret, ""), ctx());
                    }
                }
                return null;
            }
        };
    }

    protected String patternStr() {
        return "^(%s[0-9a-zA-Z_][a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)*)*";
    }

}
