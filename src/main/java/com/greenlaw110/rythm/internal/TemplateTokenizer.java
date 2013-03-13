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
package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.internal.parser.IRemoveLeadingLineBreakAndSpaces;
import com.greenlaw110.rythm.internal.parser.IRemoveLeadingSpacesIfLineBreak;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.ParserDispatcher;
import com.greenlaw110.rythm.internal.parser.build_in.*;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.F;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateTokenizer implements Iterable<TextBuilder> {
    ILogger logger = Logger.get(TemplateTokenizer.class);
    private IContext ctx;
    private List<IParser> parsers = new ArrayList<IParser>();
    private int lastCursor = 0;

    public TemplateTokenizer(IContext context) {
        ctx = context;
        RythmEngine engine = ctx.getEngine();
        RythmConfiguration conf = engine.conf();
        if ((conf.smartEscapeEnabled() || conf.naturalTemplateEnabled()) && engine.extensionManager().hasTemplateLangs()) {
            parsers.add(new CodeTypeBlockStartSensor(ctx));
            parsers.add(new CodeTypeBlockEndSensor(ctx));
        }
        if (conf.naturalTemplateEnabled() && engine.extensionManager().hasTemplateLangs()) {
            parsers.add(new DirectiveCommentStartSensor(ctx));
            parsers.add(new DirectiveCommentEndSensor(ctx));
        }
        parsers.add(new ParserDispatcher(ctx));
        parsers.add(new BlockCloseParser(ctx));
        parsers.add(new ScriptParser(ctx));
        parsers.add(new StringTokenParser(ctx));
        // add a fail through parser to prevent unlimited loop
        parsers.add(new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                TemplateParser p = (TemplateParser) ctx();
                if (lastCursor < p.cursor) return null;
                //logger.warn("fail-through parser reached. is there anything wrong in your template? line: %s", ctx.currentLine());
                String oneStep = p.getRemain().substring(0, 1);
                p.step(1);
                return new Token.StringToken(oneStep, p);
            }
        });
    }

    @Override
    public Iterator<TextBuilder> iterator() {
        return new Iterator<TextBuilder>() {

            @Override
            public boolean hasNext() {
                return ctx.hasRemain();
            }

            @Override
            public TextBuilder next() {
                for (IParser p : parsers) {
                    TextBuilder t;
                    F.T2<IParser, TextBuilder> t2 = null;
                    if (p instanceof ParserDispatcher) {
                        t2 = ((ParserDispatcher) p).go2();
                        t = null == t2 ? null : t2._2;
                    } else {
                        t = p.go();
                    }
                    
                    if (null != t) {
                        if (null != t2) {
                            p = t2._1;
                        }
                        IContext ctx = p.ctx();
                        CodeBuilder cb = ctx.getCodeBuilder();
                        if (p instanceof IRemoveLeadingLineBreakAndSpaces) {
                            cb.removeSpaceToLastLineBreak(ctx);
                        } else if (p instanceof IRemoveLeadingSpacesIfLineBreak) {
                            cb.removeSpaceTillLastLineBreak(ctx);
                        }
                        lastCursor = ((TemplateParser) ctx).cursor;
                        return t;
                    }
                }
                throw new RuntimeException("Internal error");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
}
