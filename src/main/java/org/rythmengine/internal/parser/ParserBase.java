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
package org.rythmengine.internal.parser;

import com.stevesoft.pat.Regex;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.Sandbox;
import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.build_in.CaretParserFactoryBase;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;

import java.util.regex.Pattern;

public abstract class ParserBase implements IParser {

    public static final ParserBase NULL_INST = new ParserBase() {
        @Override
        public Token go() {
            return null;
        }
    };

    protected final static ILogger logger = Logger.get(IParser.class);

    public static Pattern pattern(String regex, Object... args) {
        if (0 < args.length) regex = String.format(regex, args);
        return Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    }

    private final IDialect d_;
    private final IContext c_;
    protected final RythmEngine engine_;

    private ParserBase() {
        d_ = null;
        c_ = null;
        engine_ = null;
    }
    
    

    protected ParserBase(IContext context) {
        if (null == context) throw new NullPointerException();
        d_ = context.getDialect();
        c_ = context;
        engine_ = context.getEngine();
    }

    protected final IDialect dialect() {
        return d_;
    }

    protected final String a() {
        return d_.a();
    }

    protected final String remain() {
        return c_.getRemain();
    }

    protected final int currentLine() {
        return c_.currentLine();
    }

    protected final char peek() {
        return c_.peek();
    }

    protected final char pop() {
        return c_.pop();
    }

    public final IContext ctx() {
        return c_;
    }

    protected final CodeBuilder builder() {
        return c_.getCodeBuilder();
    }

    protected final void step(int i) {
        c_.step(i);
    }

    protected final void raiseParseException(String msg, Object... args) {
        CaretParserFactoryBase.raiseParseException(ctx(), msg, args);
    }

    protected final void checkRestrictedClass(String code) {
        if (Rythm.insideSandbox()) {
            String s = Sandbox.hasAccessToRestrictedClasses(ctx().getEngine(), code);
            if (S.notEmpty(s)) {
                raiseParseException("Access to restricted class [%s] is blocked in sandbox mode", s);
            }
        }
    }
    
    protected boolean isLastBuilderLiteral() {
        IContext ctx = ctx();
        if (ctx.cursor() == 0) return false;
        CodeBuilder cb = ctx.getCodeBuilder();
        return cb.isLastBuilderLiteral();
    }

    protected void processFollowingOpenBraceAndLineBreak(boolean leadingLB) {
        IContext ctx = ctx();
        int cnt = 0; int lbCnt = 0;
        StringBuilder sb = new StringBuilder();
        boolean braceOpen = false;
        while(ctx.hasRemain()) {
            char c0 = ctx.pop();cnt--;
            if (c0 == ' ' || c0 == '\t') {
                if (braceOpen) sb.append(c0);
                continue;
            } else if (c0 == '\n') {
                if (braceOpen) {
                    if (!leadingLB) sb.append("\n");
                    ctx.getCodeBuilder().addBuilder(new Token.StringToken(sb.toString(), ctx));
                    break;
                } else {
                    lbCnt++;
                    continue;
                }
            } else if (c0 == '{') {
                if (!braceOpen) {
                    braceOpen = true;
                    cnt = 0;
                    continue;
                } else {
                    ctx.step(cnt);
                    break;
                }
            } else {
                ctx.step(cnt);
                break;
            }
        }
    }

    // -- for testing purpose
    public static void p(int i, Regex r) {
        if (0 == i) {
            System.out.println(i + ": " + r.stringMatched());
        } else {
            System.out.println(i + ": " + r.stringMatched(i));
        }
    }

    public static void p(String s, Regex r) {
        if (r.search(s)) p(r);
    }

    public static void p(String s, Regex r, int max) {
        if (r.search(s)) p(r, max);
    }

    public static void p(Regex r, int max) {
        for (int i = 0; i < max; ++i) {
            p(i, r);
        }
    }

    public static void p(Regex r) {
        p(r, 6);
    }
}
