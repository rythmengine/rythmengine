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
import org.rythmengine.internal.TemplateParser;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;
import com.stevesoft.pat.Regex;

/**
 * Free Java code parser.
 * <p/>
 * All code between @{ and }@ will be copied literally into target java source
 *
 * @author luog
 */
public class ScriptParser extends RemoveLeadingLineBreakAndSpacesParser {


    private static final String PTN = "^(%s((?@{}))%s?)";

    public ScriptParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        IContext ctx = ctx();
        //if (ctx.currentBlock() == null) return null;
        Regex r = new Regex(String.format(PTN, a(), a()));
        if (!r.search(ctx.getRemain())) return null;
        if (!ctx.getDialect().enableScripting()) {
            throw new TemplateParser.ScriptingDisabledException(ctx);
        }
        String s = r.stringMatched(1);
        int curLine = ctx.currentLine();
        ctx.step(s.length());
        s = r.stringMatched(2);
        s = s.substring(1); // strip left "{"
        s = s.substring(0, s.length() - 1); // strip right "}"
        r = new Regex(".*[ \\t\\n\\r\\}]+if[ \\t]*\\(.*");
        boolean hasIfStatement = r.search(" " + s);
        String[] lines = s.split("[\\n\\r]+");
        int len = lines.length;
        StringBuilder sb = new StringBuilder(s.length() * 2);
        String lastLine = "";
        for (int i = 0; i < len; ++i) {
            String line = lines[i];
            if (!S.isEmpty(line)) lastLine = line;
            curLine = curLine + 1;
            sb.append(line).append(" //line: ").append(curLine).append("\n");
        }
        if (!hasIfStatement && !lastLine.trim().endsWith(";")) sb.append(";");
        String code = sb.toString();
        checkRestrictedClass(code);
        return new CodeToken(code, ctx);
    }

    public static void main(String[] args) {
        String s = "adfs\n\tif (sdfs) {\n...}\n";
        Regex r = new Regex(".*[ \\t\\n\\r\\}]+if[ \\t]*\\(.*");
        p(s, r);  
    }
}
