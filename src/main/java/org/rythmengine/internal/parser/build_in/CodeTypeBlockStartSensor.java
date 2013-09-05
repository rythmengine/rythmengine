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

import org.rythmengine.extension.ICodeType;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.ParserBase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a code type block is reached and put
 * instruction in template java source to switch
 * code type context
 * <p/>
 * <p>For example when &lt;script &gt; is reached
 * a instruction <code>pushCodeType(ICodeType type)</code>
 * should be put in place</p>
 */
public class CodeTypeBlockStartSensor extends ParserBase {
    
    public CodeTypeBlockStartSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public Token go() {
        IContext ctx = ctx();
        ICodeType curType = ctx.peekCodeType();
        if (!curType.allowInternalTypeBlock()) return null;

        String remain = ctx.getRemain();
        Iterable<ICodeType> types = ctx.getEngine().extensionManager().templateLangs();

        for (ICodeType type : types) {
            if (type.allowedExternalTypes().contains(curType)) {
                String blockStart = type.blockStart();
                if (null == blockStart) {
                    logger.warn("null block start found for lang[%s] inside lang[%s]", type, curType);
                    continue;
                }

                Pattern pStart = patterns.get(blockStart);
                if (null == pStart) {
                    pStart = Pattern.compile(blockStart, Pattern.DOTALL);
                    patterns.put(blockStart, pStart);
                }
                Matcher m = pStart.matcher(remain);
                if (m.matches()) {
                    ctx.pushCodeType(type);
                    String matched = m.group(1);
                    ctx.step(matched.length());
                    String s = matched;
                    if (matched.indexOf('@') > -1) {
                        // process internal template
                        char ch = s.charAt(0);
                        s = s.substring(1); //prevent CodeTypeBlockStartSensor to sense it again 
                        String code = ctx.getCodeBuilder().addInlineInclude(s, ctx.currentLine());
                        if (matched.endsWith("\n")) {
                            code = code + ";p(\"\\n\");";
                        }
                        s = String.format("p('%s');", String.valueOf(ch)) + code + String.format(";__ctx.pushCodeType(%s);", type.newInstanceStr());
                    } else {
                        s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
                        
                        s = String.format("p(\"%s\");__ctx.pushCodeType(%s);", s, type.newInstanceStr());
                    }
                    return new CodeToken(s, ctx);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String s = "(<\\s*script[^<>]*?>).*";
        Pattern p = Pattern.compile(s);
        s = "<script src='@src'></script><script src='@src'></script>";
        Matcher m = p.matcher(s);
        System.out.println(m.matches());
        System.out.println(m.group());
        System.out.println(m.group(1));
    }
}
