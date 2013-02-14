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

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;

public class ImportParser extends KeywordParserFactory {

    private static final String R = "(%s%s[\\s]+([a-zA-Z0-9_\\.*,\\s]+);?).*";

    public ImportParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(final IContext c) {
        return new RemoveLeadingLineBreakAndSpacesParser(c) {
            public TextBuilder go() {
                String remain = remain();
                String line = null;
                Regex r = new Regex(String.format("%s%s(\\([ \t\f]*\\))?[ \t\f]*((?@{}))", a(), keyword()));
                if (r.search(remain)) {
                    String s = r.stringMatched(2);
                    s = S.strip(s, "{", "}");
                    step(r.stringMatched().length());
                    line = s.replaceAll("[\\n\\r]+", ",");
                } else {
                    Matcher m = ptn(dialect()).matcher(remain);
                    if (!m.matches()) return null;
                    String s = m.group(1);
                    step(s.length());
                    //String imports = s.replaceFirst(String.format("%s%s[\\s]+", a(), keyword()), "").replaceFirst("(;|\\r?\\n)+$", "");
                    line = m.group(2);
                }
                checkRestrictedClass(line);
                /**
                 * We need to make sure import path added to template class
                 * to support call tag using import paths. That why we move
                 * the addImport statement here from Directive.call()
                 */
                String[] sa = line.split("[;,\\s]+");
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
                return new Directive("", ctx());
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IMPORT;
    }

}
