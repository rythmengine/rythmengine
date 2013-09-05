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
import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

public class ExitIfNoClassParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.EXIT_IF_NO_CLASS;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @debug, correct usage: @__exitIfNoClass__(My.Class.Name)");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                try {
                    ctx().getEngine().classLoader().loadClass(s);
                    return new Token("", ctx());
                } catch (Exception e) {
                    throw new TemplateParser.ExitInstruction();
                }
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\s]+";
    }
}
