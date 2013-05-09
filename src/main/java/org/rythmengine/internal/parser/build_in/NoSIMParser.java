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
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Mark a template as not a SIM template
 */
public class NoSIMParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.NOSIM;
    }

    public IParser create(IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    String s = r.stringMatched();
                    step(s.length());
                }
                return new CodeToken("", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s(?@())?)\\s+";
    }

}
