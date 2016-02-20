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

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.dialect.Rythm;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.ParserBase;

/**
 * Parse @renderBody - callback to tag body
 */
public class RenderBodyParser extends KeywordParserFactory {
    @Override
    public Keyword keyword() {
        return Keyword.RENDER_BODY;
    }

    private static class RenderBodyToken extends CodeToken {
        protected InvokeTemplateParser.ParameterDeclarationList params;
        private boolean lineBreak = false;

        RenderBodyToken(InvokeTemplateParser.ParameterDeclarationList params, IContext ctx, boolean lineBreak) {
            super("", ctx);
            this.params = params;
            this.lineBreak = lineBreak;
        }

        @Override
        public void output() {
            pline("{");
            ptline("org.rythmengine.template.ITag.__ParameterList _pl = null; ");
            if (!params.pl.isEmpty()) {
                ptline("_pl = new org.rythmengine.template.ITag.__ParameterList();");
                for (int i = 0; i < params.pl.size(); ++i) {
                    InvokeTemplateParser.ParameterDeclaration pd = params.pl.get(i);
                    //if (i == 0 && pd.nameDef == null) pd.nameDef = "arg";
                    pt("_pl.add(\"").p(pd.nameDef == null ? "" : pd.nameDef).p("\",").p(pd.valDef).p(");");
                    pline();
                }
            }
            ptline("_pTagBody(_pl, __buffer);");
            pline("}");
            if (lineBreak) {
                pline(";\npn();\n");
            }
        }
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @renderBody statement. Correct usage: @renderBody(params...)");
                }
                final String matched = r.stringMatched();
                boolean lineBreak = false;
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
                    lineBreak = matched.endsWith("\n");
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
                String paramS = r.stringMatched(3);
                InvokeTemplateParser.ParameterDeclarationList params = new InvokeTemplateParser.ParameterDeclarationList();
                InvokeTemplateParser.InvokeTagToken.parseParams(paramS, params, ctx());
                return new RenderBodyToken(params, ctx(), lineBreak);
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))\\s*)";
    }

    public static void main(String[] args) {
        Regex r = new RenderBodyParser().reg(Rythm.INSTANCE);
        if (r.search("@renderBody(ab: 1, foo.bar())")) ;
        p(r, 6);
    }

}
