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
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @get("name")
 */
public class GetParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.GET;
    }

    @Override
    protected String patternStr() {
        return "^(%s%s((?@())))";
    }

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @get call. Correct usage: @get(\"myVal\")");
                }
                step(r.stringMatched().length()); // remain: @get("name")...
                String s = r.stringMatched(2); // s: ("name")
                s = s.substring(1); // s: "name")
                s = s.substring(0, s.length() - 1); // s: "name"
                r = new Regex("(((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)(\\s*[:=,]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?)");

                if (!r.search(s)) {
                    raiseParseException("Error parsing @get tag. Correct usage: @get(\"name\"[:default-value])");
                }
                s = r.stringMatched(2); // propName: "name"
                if (s.startsWith("\"") || s.startsWith("'")) {
                    s = s.substring(1);
                    s = s.substring(0, s.length() - 1);
                    // propName: name
                }
                final String propName = s;
                final String propDefVal = r.stringMatched(4);
                return new Token("", ctx()) {
                    @Override
                    protected void output() {
                        p("\np(__getRenderProperty(\"").p(propName).p("\", ").p(propDefVal == null ? "null" : propDefVal).p("));");
                    }
                };
            }
        };
    }

    public static void main(String[] args) {
//        String s = "@get(\"var\":A.b())";
//        GetParser ap = new GetParser();
//        Regex r = ap.reg(new Rythm());
//        System.out.println(r);
//        if (r.search(s)) {
//            System.out.println("m: " + r.stringMatched());
//            System.out.println("1: " + r.stringMatched(1));
//            System.out.println("2: " + r.stringMatched(2));
//            System.out.println("3: " + r.stringMatched(3));
//            System.out.println("4: " + r.stringMatched(4));
//            System.out.println("5: " + r.stringMatched(5));
//        }
        Regex r = new Regex("(((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)(\\s*[:=,]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?)");
        String s = "(\"sammyRoute\", \"#/\")";
        s = s.substring(1); // s: "name")
        System.out.println(s);
        s = s.substring(0, s.length() - 1); // s: "name"
        System.out.println(s);
        if (r.search(s)) {
            System.out.println("1 " + r.stringMatched(1));
            System.out.println("2 " + r.stringMatched(2));
            System.out.println("4 " + r.stringMatched(4));
        }
    }

}
