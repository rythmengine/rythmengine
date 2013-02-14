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
package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.parser.build_in.KeywordParserFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class DialectBase implements IDialect {

    protected DialectBase() {
        registerBuildInParsers();
    }

    private List<IParserFactory> freeParsers = new ArrayList<IParserFactory>();

    @Override
    public void registerParserFactory(IParserFactory parser) {
        if (parser instanceof KeywordParserFactory) {
            KeywordParserFactory kp = (KeywordParserFactory) parser;
            IKeyword kw = kp.keyword();
            if (kw.isRegexp()) keywords2.put(kw.toString(), kp);
            else keywords.put(kw.toString(), kp);
        } else {
            if (!freeParsers.contains(parser)) freeParsers.add(parser);
        }
    }

    private final Map<String, KeywordParserFactory> keywords = new HashMap<String, KeywordParserFactory>();
    // - for keyword is regexp
    private final Map<String, KeywordParserFactory> keywords2 = new HashMap<String, KeywordParserFactory>();

    private void registerBuildInParsers() {
        for (Class<?> c : buildInParserClasses()) {
            if (!Modifier.isAbstract(c.getModifiers())) {
                @SuppressWarnings("unchecked")
                Class<? extends IParserFactory> c0 = (Class<? extends IParserFactory>) c;
                try {
                    Constructor<? extends IParserFactory> ct = c0.getConstructor();
                    ct.setAccessible(true);
                    IParserFactory f = ct.newInstance();
                    registerParserFactory(f);
                } catch (Exception e) {
                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                    else throw new RuntimeException(e);
                }
            }
        }
    }

    public IParser createBuildInParser(String keyword, IContext context) {
        KeywordParserFactory f = keywords.get(keyword.toLowerCase());
        if (null == f) {
            for (String r : keywords2.keySet()) {
                if (keyword.matches(r)) {
                    f = keywords2.get(r);
                    break;
                }
            }
        }
        return null == f ? null : f.create(context);
    }

    public Iterable<IParserFactory> freeParsers() {
        return new Iterable<IParserFactory>() {
            final List<IParserFactory> fs = new ArrayList<IParserFactory>(freeParsers);

            @Override
            public Iterator<IParserFactory> iterator() {
                return new Iterator<IParserFactory>() {

                    private int cursor = 0;

                    @Override
                    public boolean hasNext() {
                        return cursor < fs.size();
                    }

                    @Override
                    public IParserFactory next() {
                        return fs.get(cursor++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }
        };
    }

    protected abstract Class<?>[] buildInParserClasses();

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof IDialect) {
            return getClass().equals(o.getClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s Dialect", id());
    }

    @Override
    public boolean isMyTemplate(String template) {
        return false;
    }

    @Override
    public void begin(IContext ctx) {
    }

    @Override
    public void end(IContext ctx) {
    }

    @Override
    public boolean enableScripting() {
        return true;
    }

    @Override
    public boolean enableFreeForLoop() {
        return true;
    }

    @Override
    public CodeBuilder createCodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine) {
        return new CodeBuilder(template, className, tagName, templateClass, engine, this);
    }
}
