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

import com.greenlaw110.rythm.internal.ExtensionManager;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse groovy nullable expression, e.g. @foo?.bar()?.zee
 * <p/>
 * Must be invoked behind invoking of the normal ExpressionParser
 *
 * @author luog
 */
public class NullableExpressionParser extends CaretParserFactoryBase {

    @Override
    public IParser create(IContext ctx) {

        final IDialect dialect = ctx.getDialect();
        //if (!(dialect instanceof Rythm)) throw new DialectNotSupportException(dialect.id());
        final String caret_ = dialect.a();

        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                final String caret_ = dialect.a();
                final Regex r1 = new Regex(String.format(patternStr1(), caret_));
                final Regex r2 = new Regex(String.format(patternStr2(), caret_));
                final Regex r3 = new Regex(patternStr3());
                final Regex r4 = new Regex(patternStr4());

                String s = remain();
                String exp;
                int step = 0;
                if (r1.search(s)) {
                    exp = r1.stringMatched(2);
                    step = r1.stringMatched(1).length();
                } else if (r2.search(s)) {
                    exp = r2.stringMatched(2);
                    exp = S.stripBrace(exp);
                    step = r2.stringMatched().length();
                } else {
                    return null;
                }
                exp = exp.trim();
                if (!exp.contains("?")) return null; // leave it to normal expression handler
                if (!r4.search(exp)) {
                    //raiseParseException("nullable expression can contain only expression, \"[]\", \"()\", \".\", \"?\", found: %s", exp);
                    return null; // the foo == null ? "bar" : foo style expression?
                }
                step(step);
                StringBuilder curExp = new StringBuilder();
                final List<String> statements = new ArrayList<String>();
                ExtensionManager jem = ctx().getEngine().extensionManager();
                while (r3.search(exp)) {
                    String s0 = r3.stringMatched().trim();
                    if (jem.isJavaExtension(s0)) break;
                    if (s0.endsWith("?.")) {
                        s0 = s0.replace("?.", "");
                        curExp.append(s0);
                        String e = curExp.toString();
                        curExp.append(".");
                        statements.add(e);
                    } else if (s0.endsWith(".")) {
                        curExp.append(s0);
                    }
                }
                exp = exp.replaceAll("\\?", "");
                return new CodeToken(exp, ctx()) {
                    @Override
                    public void output() {
                        outputExpression(statements);
                    }
                };
            }
        };
    }

    protected String patternStr1() {
        return "^(%s(([a-zA-Z_][\\w]*((?@())(?@[])?|(?@[])(?@())?)?(\\??\\.)?)+)).*";
    }

    protected String patternStr2() {
        //return "^(%s(@?()))";
        return "^(%s((?@())))";
    }

    protected String patternStr3() {
        return "\\G([a-zA-Z_][\\w]*((?@())(?@[])?|(?@[])(?@())?)?(\\??\\.)?)";
    }

    protected String patternStr4() {
        return "^([a-zA-Z_][\\w]*((?@())(?@[])?|(?@[])(?@())?)?(\\??\\.)?)+$";
    }

    public static void main(String[] args) {
        main1(args);
    }

    public static void main2(String[] args) {
        String s = "app?.name\"";
        Regex r = new Regex(new NullableExpressionParser().patternStr4());
        if (r.search(s)) {
            p(r, 5);
        }
    }

    public static void main1(String[] args) {
        String ps = String.format(new NullableExpressionParser().patternStr1(), "@");
        System.out.println(ps);
        Regex r = new Regex(ps);
        String s = "@foo?.bar\"dsfsa";
        if (r.search(s)) {
            p(r, 5);
        }
    }

}
