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

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.BlockCodeToken;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.internal.parser.Patterns;
import org.rythmengine.utils.S;
import org.rythmengine.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * assign enclosed part into a variable
 *
 * @assign("xx") {...} create a variable
 * @assign("xxx", true) {...} // create a final variable
 */
public class AssignParser extends KeywordParserFactory {

    public class AssignToken extends BlockCodeToken {
        private String assignTo;
        private boolean isFinal;

        public AssignToken(String assignTo, IContext context) {
            super(null, context);
            String[] sa = assignTo.split(",");
            this.assignTo = S.stripQuotation(sa[0]);
            if (Patterns.RESERVED.matches(this.assignTo)) {
                raiseParseException(ctx, "assign variable name is reserved: %s", this.assignTo);
            }
            if (sa.length > 1) {
                isFinal = Boolean.parseBoolean(sa[1].trim());
            }
        }

        @Override
        public void output() {
            String assignTo = this.assignTo;
            if (isFinal) assignTo = this.assignTo + "___";
            p2t("Object ").p(assignTo).p(" = null;");
            p2tline("{");
            p3tline("StringBuilder sbOld = getSelfOut();");
            p3tline("StringBuilder sbNew = new StringBuilder();");
            p3tline("setSelfOut(sbNew);");
        }

        @Override
        public String closeBlock() {
            String assignTo = this.assignTo;
            if (isFinal) assignTo = this.assignTo + "___";
            StringBuilder sbNew = new StringBuilder();
            StringBuilder sbOld = __getBuffer();
            __setBuffer(sbNew);
            String varName = ctx.getCodeBuilder().newVarName();
            p3tline(String.format("String %s = sbNew.toString();", varName));
            p3tline("setSelfOut(sbOld);");
            p3t(assignTo).p(String.format(" = %s;", varName));
            pline();
            p2tline("}");
            if (isFinal) {
                p2t("final Object ").p(this.assignTo).p(" = ").p(assignTo).p(";");
                pline();
            }
            String s = sbNew.toString();
            __setBuffer(sbOld);
            return s;
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.ASSIGN;
    }

//    public IParser create(IContext ctx) {
//        return new ParserBase(ctx) {
//            public TextBuilder go() {
//                Matcher m = ptn(dialect()).matcher(remain());;
//                if (!m.matches()) return null;
//                String s = m.group(1);
//                step(s.length());
//                String assignTo = m.group(2);
//                return new SectionToken(assignTo, ctx());
//            }
//        };
//    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain()))
                    raiseParseException("bad @assign statement. Correct usage: @assign(myVariable){...}");
                String matched = r.stringMatched();
                step(matched.length());
                String s = r.stringMatched(1);
                s = S.stripBrace(s);
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
                return new AssignToken(s, ctx());
            }
        };
    }


    @Override
    protected String patternStr() {
        //return "(%s%s[\\s]+([a-zA-Z][a-zA-Z0-9_]+)[\\s\\r\\n\\{]*).*";
        return "\\n?[ \\t\\x0B\\f]*%s%s[ \\t\\x0B\\f]*((?@()))[ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?";
    }

}
