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
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;

import java.util.regex.Matcher;

public class ImportParser extends KeywordParserFactory {

    private static final String R = "(\\n?[ \\t\\x0B\\f]*%s%s[\\s]+([a-zA-Z0-9_\\.*,[ \\t\\x0B\\f]*)]+);?\\n?).*";

    public ImportParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                String remain = remain();
                String line;
                Regex r = new Regex(String.format("\\n?[ \\t\\x0B\\f]*%s%s(\\([ \t\f]*\\))?[ \t\f]*((?@{}))[ \\t\\x0B\\f]*\\n?", a(), keyword()));
                String matched;
                if (r.search(remain)) {
                    matched = r.stringMatched();
                    String s = r.stringMatched(2);
                    s = S.strip(s, "{", "}");
                    step(matched.length());
                    line = s.replaceAll("[\\n\\r]+", ",");
                } else {
                    Matcher m = ptn(dialect()).matcher(remain);
                    if (!m.matches()) return null;
                    String s = m.group(1);
                    step(s.length());
                    matched = s;
                    //String imports = s.replaceFirst(String.format("%s%s[\\s]+", a(), keyword()), "").replaceFirst("(;|\\r?\\n)+$", "");
                    line = m.group(2);
                }
                checkRestrictedClass(line);
                /**
                 * We need to make sure import path added to template class
                 * to support call tag using import paths. That why we move
                 * the addImport statement here from Directive.call()
                 */
                String[] sa = line.split("[;, \\t\\x0B\\f]+");
                CodeBuilder cb = builder();
                boolean statik = false;
                for (String imp : sa) {
                    if (S.isEmpty(imp)) continue;
                    if ("static".equals(imp)) statik = true;
                    else {
                        cb.addImport(statik ? "static " + imp : imp, ctx().currentLine() - 1);
                        statik = false;
                    }
                }
                boolean leadingLB = matched.startsWith("\n");
                boolean afterLB = matched.endsWith("\n"); 
                if (leadingLB) {
                    ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                    if (leadingLB ^ afterLB) {
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    }
                } else {
                    Regex r0 = new Regex("([ \\t\\x0B\\f]*).*");
                    if (r0.search(matched)) {
                        String blank = r0.stringMatched(1);
                        if (blank.length() > 0) {
                            ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                        }
                    }
                }
                return Token.EMPTY_TOKEN;
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IMPORT;
    }

}
