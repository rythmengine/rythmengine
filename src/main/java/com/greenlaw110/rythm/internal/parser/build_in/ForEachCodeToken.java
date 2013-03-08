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

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.utils.S;
import com.stevesoft.pat.Regex;

public class ForEachCodeToken extends BlockCodeToken {

    private String type;
    private String varname;
    private String iterable;
//    private int openPos;
//    private int closePos;

    /**
     * @param type
     * @param varname
     * @param iterable
     * @param context
     * @each String [str]: myStrList @
     * ^     ^       ^        ^
     * |     |       |        |
     * type varname  iterable endloop
     */
    public ForEachCodeToken(String type, String varname, String iterable, IContext context, int lineNo) {
        super(null, context);
        line = lineNo;
        if (null == iterable) throw new NullPointerException();
        iterable = iterable.trim();
        iterable = ExpressionParser.processPositionPlaceHolder(iterable);
        if (null != type) type = type.trim();
        this.type = ObjectType(type);
        this.varname = null == varname ? "_" : varname;
        if (iterable.contains("..") || iterable.contains(" to ") || iterable.contains(" till ")) {
            iterable = "com.greenlaw110.rythm.utils.Range.valueOf(\"" + iterable + "\")";
        }
        this.iterable = iterable;
        //openPos = context.cursor();
        IContext ctx = context;
        ctx.pushBreak(IContext.Break.BREAK);
        ctx.pushContinue(IContext.Continue.CONTINUE);
        CodeBuilder cb = context.getCodeBuilder();
        boolean isBasic = ctx.getDialect() instanceof BasicRythm;
        if (S.isEmpty(type) || "Object".equals(type)) {
            String itrType = cb.getRenderArgType(iterable);
            if (null != itrType) {
                Regex r = new Regex(".*((?@<>))");
                if (r.search(itrType)) {
                    type = r.stringMatched(1);
                    this.type = S.strip(type, "<", ">");
                    boolean key = iterable.endsWith("keySet()");
                    boolean val = iterable.endsWith("values()");
                    if (key || val) {
                        r = new Regex("([a-zA-Z0-9\\[\\]_]+(?@<>)?)\\s*\\,\\s*([a-zA-Z0-9\\[\\]_]+(?@<>)?)");
                        if (r.search(this.type)) {
                            if (key) this.type = r.stringMatched(1);
                            else this.type = r.stringMatched(2);
                        } else {
                            throw new ParseException(ctx.getEngine(), ctx.getTemplateClass(), line, "Invalid for loop iterator type declaration: %s", itrType);
                        }
                    }
                } else {
                    if (itrType.endsWith("]")) {
                        int pos = itrType.lastIndexOf("[");
                        this.type = itrType.substring(0, pos);
                    } else {
                        this.type = "java.lang.Object";
                    }
                }
                if (S.isEqual(this.type, this.varname)) this.varname = "_";
            } else {
                this.type = "java.lang.Object";
            }
        } else if (isBasic) {
            throw new TemplateParser.TypeDeclarationException(ctx);
        }
        if (isBasic) {
            ExpressionParser.assertBasic(iterable, context);
            context.getCodeBuilder().addRenderArgsIfNotDeclared(line, "Iterable<?>", iterable);
        }
    }

    private String ObjectType(String type) {
        if (null == type) return "";
        if ("int".equals(type)) return "Integer";
        if ("float".equals(type)) return "Float";
        if ("double".equals(type)) return "Double";
        if ("boolean".equals(type)) return "Boolean";
        if ("char".equals(type)) return "Character";
        if ("long".equals(type)) return "Long";
        if ("byte".equals(type)) return "Byte";
        if ("short".equals(type)) return "Integer";
        return type;
    }

    @Override
    public void output() {
        String prefix = "_".equals(varname) ? "" : varname + "";
        CodeBuilder cb = ctx.getCodeBuilder();
        String varId = prefix + "_index";
        String varIsOdd = prefix + "_isOdd";
        String varSize = prefix + "_size";
        String varParity = prefix + "_parity";
        String varIsFirst = prefix + "_isFirst";
        String varIsLast = prefix + "_isLast";
        String varSep = prefix + "_sep";
        String varWithSep = prefix + "__sep";
        String varUtils = prefix + "_utils";
        String varWithUtils = prefix + "__utils";

        String varItr = cb.newVarName();
        p("{\n__Itr<").p(type).p("> ").p(varItr).p(" = new __Itr(").p(iterable).p(");");
        pline();
        p("int ").p(varSize).p(" = ").p(varItr).p(".size();");
        pline();
        p("if (").p(varSize).p(" > 0) {");
        pline();
        p("int ").p(varId).p(" = 0;");
        pline();
        p("for(").p("?".equals(type) ? "java.lang.Object" : type).p(" ").p(varname).p(" : ").p(varItr).p(") {");
        pline();
        p(varId).p("++;");
        pline();
        p("boolean ").p(varIsOdd).p(" = ").p(varId).p(" % 2 == 1;");
        pline();
        p("java.lang.String ").p(varParity).p(" = ").p(varIsOdd).p(" ? \"odd\" : \"even\";");
        pline();
        p("boolean ").p(varIsFirst).p(" = ").p(varId).p(" == 1;");
        pline();
        p("boolean ").p(varIsLast).p(" = ").p(varId).p(" >= ").p(varSize).p(";");
        pline();
        p("com.greenlaw110.rythm.utils.RawData ").p(varSep).p(" = new com.greenlaw110.rythm.utils.RawData(").p(varIsLast).p(" ? \"\" : \",\");");
        pline();
        p("com.greenlaw110.rythm.utils.RawData ").p(varWithSep).p(" = new com.greenlaw110.rythm.utils.RawData(com.greenlaw110.rythm.utils.S.escape(").p(varname).p(")+(").p(varIsLast).p(" ? \"\" : \",\"));");
        pline();
        p("com.greenlaw110.rythm.internal.LoopUtil ").p(varUtils).p(" = new com.greenlaw110.rythm.internal.LoopUtil(").p(varIsFirst).p(", ").p(varIsLast).p(");");
        pline();
        p("com.greenlaw110.rythm.internal.LoopUtil ").p(varWithUtils).p(" = new com.greenlaw110.rythm.internal.LoopUtil(").p(varIsFirst).p(", ").p(varIsLast).p(", ").p(varname).p(");");
        pline();
    }
    @Override
    public String closeBlock() {
        return "\n\t}\n}\n}\n";
    }
}
