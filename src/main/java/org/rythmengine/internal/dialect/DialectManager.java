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
package org.rythmengine.internal.dialect;

import org.rythmengine.internal.CodeBuilder;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.IParserFactory;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;

import java.util.Stack;

public class DialectManager {
    protected final static ILogger logger = Logger.get(DialectManager.class);
    final static IDialect[] defDialects = {
            BasicRythm.INSTANCE,
            SimpleRythm.INSTANCE,
            Rythm.INSTANCE
    };

    public static IDialect getById(String id) {
        for (IDialect d : defDialects) {
            if (S.isEqual(id, d.id())) return d;
        }
        return null;
    }

    private static int indexOf(IDialect dialect) {
        if (null == dialect) throw new NullPointerException();
        for (int i = 0; i < defDialects.length; ++i) {
            if (S.isEqual(dialect.id(), defDialects[i].id())) return i;
        }
        return -1;
    }

    private static IDialect nextAvailable(IDialect d) {
        if (null == d) return defDialects[0];
        int i = indexOf(d) + 1;
        if (i >= defDialects.length) return null;
        return defDialects[i];
    }

    private static final InheritableThreadLocal<Stack<IDialect>> cur = new InheritableThreadLocal<Stack<IDialect>>() {
        @Override
        protected Stack<IDialect> initialValue() {
            return new Stack<IDialect>();
        }
    };

    private static void push(IDialect dialect) {
        if (null == dialect) throw new NullPointerException();
        cur.get().push(dialect);
    }

    public static IDialect current() {
        return cur.get().peek();
    }

    private static IDialect pop() {
        Stack<IDialect> sd = cur.get();
        IDialect d = sd.pop();
        if (sd.isEmpty()) {
            cur.remove();
        }
        return d;
    }

    public void beginParse(IContext ctx) {
        CodeBuilder cb = ctx.getCodeBuilder();
        IDialect d = cb.requiredDialect;
        if (null == d) {
            d = ctx.getDialect();
            if (null != d) {
                // try the next available
                d = nextAvailable(d);
                if (null == d) throw new NullPointerException("No dialect can process the template");
            } else {
                // guess the most capable
                String template = ctx.getRemain();
                for (IDialect d0 : defDialects) {
                    if (d0.isMyTemplate(template)) {
                        d = d0;
                        break;
                    }
                }
            }
        }
        ctx.setDialect(d);
        push(d);
        d.begin(ctx);
    }

    public void endParse(IContext ctx) {
        IDialect d = ctx.getDialect();
        d.end(ctx);
        pop();
    }

    private void registerParserFactories(IDialect dialect, IParserFactory... factories) {
        for (IParserFactory pf : factories) {
            dialect.registerParserFactory(pf);
        }
    }

    public void registerExternalParsers(String dialectId, IParserFactory... factories) {
        if (null == dialectId) {
            for (IDialect d : defDialects) {
                registerParserFactories(d, factories);
            }
        } else {
            IDialect d = getById(dialectId);
            if (null != d) {
                registerParserFactories(d, factories);
            } else {
                throw new IllegalArgumentException("Cannot find dialect by Id: " + dialectId);
            }
        }
    }
}
