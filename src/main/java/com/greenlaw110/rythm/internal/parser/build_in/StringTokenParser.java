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
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The StringToken probe grab plain texts (no special token at all)
 */
public class StringTokenParser extends ParserBase {

    public StringTokenParser(IContext context) {
        super(context);
    }

    /*
     * ([^@]+((@[^@]+?)
     */
    private static final String PTN = "(%s%s.*?|.*?)([\\n\\r@\\<\\#\\$\\&\\{\\}\\-\\*\\/].*|$)";

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        String s = ctx.getRemain();
        if (s.length() == 0) {
            return Token.EMPTY_TOKEN;
        }
        String a = a();
        Pattern p = Pattern.compile(String.format(PTN, a, a, a),
                Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (!m.matches()) {
            return null;
        }
        s = m.group(1);
        if (s.length() == 0) {
            return null;
        }
        ctx.step(s.length());
        s = s.replace(String.format("%s%s", a, a), a).replace("\\", "\\\\");
        if ("".equals(s)) {
            return Token.EMPTY_TOKEN;
        } else {
            return new Token.StringToken(s, ctx);
        }
    }

    public static void main(String[] args) {
        String s = "@@163.com#{verbatim} @{for xb}@";
        String sp = String.format(PTN, "@", "@", "@");
        System.out.println(sp);
        Pattern p = Pattern.compile(sp, Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
