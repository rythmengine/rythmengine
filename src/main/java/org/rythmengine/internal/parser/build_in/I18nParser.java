/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;

/**
 * Parsing @i18n() directive
 */
public class I18nParser extends KeywordParserFactory {

    @Override
    public IKeyword keyword() {
        return Keyword.I18N;
    }

    @Override
    protected String patternStr() {
        return "^((\\n?[ \\t\\x0B\\f]*)%s%s((?@())))";
    }

    protected static String innerPattern() {
        return "^((?@\"\")|(?@''))(\\s*,\\s*(.*))?";
    }
    
    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                String remain = remain();
                Regex r = reg(dialect());
                if (!r.search(remain)) {
                    raiseParseException("Error parsing @i18n statement. Correct usage: @i18n(\"key\", ...)");
                }
                final String matched = r.stringMatched();
                step(matched.length());
                String space = r.stringMatched(2);
                ctx.getCodeBuilder().addBuilder(new Token.StringToken(space, ctx));
                String s = S.stripBrace(r.stringMatched(1).replace("@i18n", ""));
                r = new Regex(innerPattern());
                if (r.search(s)) {
                    // "" or '' present so prefetch String or MessageFormat
                    String args = r.stringMatched(3);
                    if (S.empty(args)) {
                        String k = r.stringMatched(1);
                        s = "\"" + S.stripQuotation(k) + "\"";
                    } else {
                        String k = r.stringMatched(1);
                        s = "\"" + S.stripQuotation(k) + "\", " + args;
                    }
                }
                // cannot pre-resolve, output S.i18n directly
                s = String.format("__i18n(%s)", s);
                s = ExpressionParser.processPositionPlaceHolder(s);
                return new CodeToken(s, ctx()) {
                    @Override
                    public void output() {
                        p("p(").p(s).p(");");
                        pline();
                    }
                };
            }
        };
    }

}
