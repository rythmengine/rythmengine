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
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

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

        RenderBodyToken(InvokeTemplateParser.ParameterDeclarationList params, IContext ctx) {
            super("", ctx);
            this.params = params;
        }

        @Override
        public void output() {
            pline("{");
            ptline("com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null; ");
            if (params.pl.size() > 0) {
                ptline("_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList();");
                for (int i = 0; i < params.pl.size(); ++i) {
                    InvokeTemplateParser.ParameterDeclaration pd = params.pl.get(i);
                    //if (i == 0 && pd.nameDef == null) pd.nameDef = "arg";
                    pt("_pl.add(\"").p(pd.nameDef == null ? "" : pd.nameDef).p("\",").p(pd.valDef).p(");");
                    pline();
                }
            }
            ptline("_pTagBody(_pl, __buffer);");
            pline("}");
        }
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @renderBody statement. Correct usage: @renderBody(params...)");
                }
                step(r.stringMatched().length());
                String paramS = r.stringMatched(3);
                InvokeTemplateParser.ParameterDeclarationList params = new InvokeTemplateParser.ParameterDeclarationList();
                InvokeTemplateParser.InvokeTagToken.parseParams(paramS, params, ctx());
                return new RenderBodyToken(params, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s\\s*((?@()))\\s*)";
    }

    public static void main(String[] args) {
        Regex r = new RenderBodyParser().reg(Rythm.INSTANCE);
        if (r.search("@renderBody(ab: 1, foo.bar())")) ;
        p(r, 6);
    }

}
