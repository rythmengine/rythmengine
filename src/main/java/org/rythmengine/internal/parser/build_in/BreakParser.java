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
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

public class BreakParser extends KeywordParserFactory {

    private static final String R = "^(\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))?[\\s;]*)";

    public BreakParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new RemoveLeadingLineBreakAndSpacesParser(c) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Bad @break statement. Correct usage: @break()");
                }
                String matched = r.stringMatched();
                String condition = r.stringMatched(2);
                if (null != condition) {
                    condition = S.stripBrace(condition);
                }
                step(matched.length());
                IContext.Break b = ctx().peekBreak();
                if (null == b) raiseParseException("Bad @break statement: No loop context");
                if (S.notEmpty(condition)) {
                    return new IfThenToken(condition, "break", ctx());
                } else {
                    return new CodeToken(b.getStatement(), ctx());
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.BREAK;
    }

}
