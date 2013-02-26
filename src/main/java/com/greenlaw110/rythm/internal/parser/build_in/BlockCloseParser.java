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
import com.greenlaw110.rythm.internal.IBlockHandler;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BlockCloseParser extends ParserBase {

    private static final String PTN = "([\\}]?%s[\\}\\s\\n\\>\\]]).*";
    private static final String PTN2 = "((\\}%s|%s\\}|\\})([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?)).*";

    public BlockCloseParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        IContext ctx = ctx();
        IBlockHandler bh = ctx.currentBlock();
        if (null == bh) return null;
        String remain = remain();
        String s;
        if ("@".equals(remain)) {
            s = remain;
        } else {
            Pattern p = Pattern.compile(String.format(PTN2, a(), a(), a()), Pattern.DOTALL);
            Matcher m = p.matcher(remain);
            if (!m.matches()) {
                p = Pattern.compile(String.format(PTN, a()), Pattern.DOTALL);
                m = p.matcher(remain);
                if (!m.matches()) {
                    return null;
                }
            };
            s = m.group(1);
        }
        // keep ">" or "]" for case like <a id=".." @if (...) class="error" @>
        if (s.endsWith(">") || s.endsWith("]") || s.endsWith("\n")) s = s.substring(0, s.length() - 1);
        ctx.step(s.length());
        boolean hasLineBreak = s.contains("\\n") || s.contains("\\r");
        try {
            s = ctx.closeBlock();
            if (hasLineBreak) s = s + "\n"; // fix #53
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        CodeToken ct = new CodeToken(s, ctx);
        if (!(bh instanceof BlockToken.LiteralBlock)) {
            String bhCls = bh.getClass().getName();
            if (bhCls.contains("ForEach") || bhCls.contains("ElseFor") || bhCls.contains("Assign")) {
                ctx.getCodeBuilder().removeSpaceTillLastLineBreak(ctx);
                ct.removeNextLineBreak = true;
            } else {
                ctx.getCodeBuilder().removeSpaceToLastLineBreak(ctx);
            }
        }
        return ct;
    }
}
