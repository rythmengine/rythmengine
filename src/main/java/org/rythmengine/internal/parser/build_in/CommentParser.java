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

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommentParser deals with the following type comments:
 * 1. inline comment. e.g. @/ this is my comment \n
 * 2. block comment. e.g. @* this is my multi \nline comments *@
 * User: luog
 * Date: 2/12/11
 * Time: 3:04 PM
 */
public class CommentParser extends CaretParserFactoryBase {
	public static final String COMMENT_FORMAT="^(%s/.*?)(\n.*|$)";
    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Pattern p = inlineComment();
                Matcher m = p.matcher(remain());
                if (!m.matches()) {
                    p = blockComment();
                    m = p.matcher(remain());
                    if (!m.matches()) return null;
                } else {
                    // special process to directive comments
                    if (ctx.insideDirectiveComment()) {
                        ctx.leaveDirectiveComment();
                    }
                }
                String s = m.group(1);
                ctx.step(s.length());
                return Token.EMPTY_TOKEN;
            }

            private Pattern inlineComment() {
            	String a=a();
            	Pattern result = Pattern.compile(String.format(COMMENT_FORMAT, a), Pattern.DOTALL);
                return result;
//                IContext ctx = ctx();
//                if (ctx.insideDirectiveComment()) {
//                    return Pattern.compile(String.format("^(%s//.*?)(%s|\n).*", a(), S.escapeRegex(ctx.peekCodeType().commentEnd())), Pattern.DOTALL);
//                } else {
//                    return Pattern.compile(String.format("^(%s//.*?)\n.*", a()), Pattern.DOTALL);
//                }
            }

            private Pattern blockComment() {
                return Pattern.compile(String.format("^(%s\\*.*?\\*%s).*", a(), a()), Pattern.DOTALL);
            }
        };
    }
}
