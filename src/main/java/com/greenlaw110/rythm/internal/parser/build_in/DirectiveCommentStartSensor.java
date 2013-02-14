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

import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a directive comment is reached and strip
 * it out from the parsing process
 * <p/>
 * <p>For example when &lt;!-- @&gt; is found the
 * &lt;!-- should be stripped out</p>
 */
public class DirectiveCommentStartSensor extends RemoveLeadingLineBreakAndSpacesParser {
    ILogger logger = Logger.get(DirectiveCommentStartSensor.class);

    public DirectiveCommentStartSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        if (ctx.insideDirectiveComment()) {
            return null;
            //raiseParseException("directive comment not closed");
        }
        ILang lang = ctx.peekLang();
        while (null != lang) {
            String sCommentStart = lang.commentStart();
            sCommentStart = S.escapeRegex(sCommentStart).toString();
            // try <!-- @ first
            String s = "(" + sCommentStart + "\\s*" + ")" + ctx.getDialect().a() + ".*";
            Pattern p = patterns.get(s);
            if (null == p) {
                p = Pattern.compile(s, Pattern.DOTALL);
                patterns.put(s, p);
            }
            Matcher m = p.matcher(remain());
            if (m.matches()) {
                s = m.group(1);
                ctx.step(s.length());
                ctx.enterDirectiveComment();
                return Token.EMPTY_TOKEN;
            }
            // try <!-- }
            s = "(" + sCommentStart + "\\s*)\\}.*";
            p = patterns.get(s);
            if (null == p) {
                p = Pattern.compile(s, Pattern.DOTALL);
                patterns.put(s, p);
            }
            m = p.matcher(remain());
            if (m.matches()) {
                s = m.group(1);
                ctx.step(s.length());
                ctx.enterDirectiveComment();
                return Token.EMPTY_TOKEN;
            }
            lang = lang.getParent();
        }

        return null;
    }

    public static void main(String[] args) {
    }
}
