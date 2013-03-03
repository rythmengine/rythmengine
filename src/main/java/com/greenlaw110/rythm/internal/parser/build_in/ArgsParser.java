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
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.F;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;

public class ArgsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ARGS;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            /*
             * parse @args {...}
             */
            public TextBuilder go2(String s) {
                Regex r = reg(dialect());
                final List<F.T4<Integer, String, String, String>> ral = new ArrayList();
                s = s.replaceAll("[\\n\\r]+", ",");
                int line = ctx.currentLine();
                while (r.search(s)) {
                    String type = r.stringMatched(2);
                    checkRestrictedClass(type);
                    String name = r.stringMatched(4);
                    String defVal = r.stringMatched(6);
                    name = ExpressionParser.processPositionPlaceHolder(name);
                    ral.add(new F.T4(line, type, name, defVal));
                }
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
                        for (F.T4<Integer, String, String, String> rd : ral) {
                            builder().addRenderArgs(rd._1, rd._2, rd._3, rd._4);
                        }
                    }
                };
            }

            /*
             * parse @args String s...
             */
            public TextBuilder go() {
                String remain = remain();
                Regex r = new Regex(String.format("\\n?[ \\t\\x0B\\f]*%s%s(\\([ \t\f]*\\))?[ \t\f]*((?@{}))\\n?", a(), keyword()));
                if (r.search(remain)) {
                    String matched = r.stringMatched();
                    if (matched.startsWith("\n") || matched.endsWith("\n")) {
                        ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                    }
                    String s = r.stringMatched(2);
                    s = S.strip(s, "{", "}");
                    step(matched.length());
                    return go2(s);
                }
                boolean startWithLineBreak = remain.startsWith("\n");
                if (startWithLineBreak) {
                    remain = remain.substring(1);
                }
                String space = "";
                Regex r0 = new Regex("^(\\s+).*");
                if (r0.search(remain)) {
                    space = r0.stringMatched(1);
                }
                //space = startWithLineBreak ? "\n" + space : space;
                step(space.length());
                remain = remain.replaceFirst("^\\s+", "");
                String key = String.format("%s%s ", a(), keyword());
                if (!remain.startsWith(key)) {
                    raiseParseException("No argument declaration found");
                }
                step(key.length() + (startWithLineBreak ? 1 : 0));
                remain = remain();
                r = reg(dialect());
                int step = 0;
                //final List<CodeBuilder.RenderArgDeclaration> ral = new ArrayList<CodeBuilder.RenderArgDeclaration>();
                while (r.search(remain)) {
                    String matched = r.stringMatched();
                    if (matched.startsWith("\n") || matched.startsWith("\r")) {
                        break;
                    }
                    step += matched.length();
                    String type = r.stringMatched(2);
                    checkRestrictedClass(type);
                    String name = r.stringMatched(4);
                    String defVal = r.stringMatched(6);
                    name = ExpressionParser.processPositionPlaceHolder(name);
                    //ral.add(new CodeBuilder.RenderArgDeclaration(ctx().currentLine(), name, type, defVal));
                    ctx().getCodeBuilder().addRenderArgs(ctx().currentLine(), type, name, defVal);
                }
                step(step);
                // strip off the following ";" symbol and line breaks
                char c;
                while (true) {
                    c = peek();
                    if ((' ' == c || ';' == c || '\n' == c) && ctx.hasRemain()) {
                        step(1);
                        if (space.length() > 0) {
                            ctx.getCodeBuilder().addBuilder(new Token.StringToken(space, ctx));
                        }
                        if ('\n' == c && startWithLineBreak) {
                            ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                        }
                    } else {
                        break;
                    }
                }
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
//                        for (CodeBuilder.RenderArgDeclaration rd: ral) {
//                            builder().addRenderArgs(rd);
//                        }
                    }
                };
            }
        };
    }

    public static List<CodeBuilder.RenderArgDeclaration> parseArgDeclaration(int lineNo, String s) {
        final List<CodeBuilder.RenderArgDeclaration> ral = new ArrayList<CodeBuilder.RenderArgDeclaration>();
        Regex r = new ArgsParser().reg(DialectManager.current());
        while (r.search(s)) {
            String matched = r.stringMatched();
            if (matched.startsWith("\n") || matched.startsWith("\r")) {
                break;
            }
            String name = r.stringMatched(4);
            String type = r.stringMatched(2);
            String defVal = r.stringMatched(5);
            name = ExpressionParser.processPositionPlaceHolder(name);
            ral.add(new CodeBuilder.RenderArgDeclaration(lineNo, type, name, defVal));
        }
        return ral;
    }

    public static final String PATTERN = "\\G[ \\t\\x0B\\f]*,?[ \\t\\x0B\\f]*(([\\sa-zA-Z_][\\w$_\\.]*(?@\\<\\>)?(\\[\\])?)[ \\t\\x0B\\f]+([@a-zA-Z_][\\w$_]*))([ \\t\\x0B\\f]*=[ \\t\\x0B\\f]*((?@{})|[0-9]+[fLld]?|'[.]'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?";

    public static final String PATTERN2 = "";

    @Override
    protected String patternStr() {
        return PATTERN;
    }

    protected String patternStr0() {
        return "(%s%s([\\s,]+[a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*)+(;|\\r?\\n)+).*";
    }

}
