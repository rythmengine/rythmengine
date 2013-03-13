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

import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.Token;
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
 * Detect if a directive comment is closed and strip
 * it out from the parsing process
 */
public class DirectiveCommentEndSensor extends RemoveLeadingLineBreakAndSpacesParser {
    ILogger logger = Logger.get(DirectiveCommentEndSensor.class);

    public DirectiveCommentEndSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        if (!ctx.insideDirectiveComment()) {
            return null;
        }
        ICodeType type = ctx.peekCodeType();
        while (null != type) {
            String s = type.commentEnd();
            if (!S.empty(s)) {
                s = S.escapeRegex(s).toString();
                s = "(\\s*" + s + ")" + ".*";
                Pattern p = patterns.get(s);
                if (null == p) {
                    p = Pattern.compile(s, Pattern.DOTALL);
                    patterns.put(s, p);
                }
                Matcher m = p.matcher(remain());
                if (m.matches()) {
                    s = m.group(1);
                    ctx.step(s.length());
                    ctx.leaveDirectiveComment();
                    return Token.EMPTY_TOKEN;
                }
            }
            type = type.getParent();
        }

        return null;
    }

}
