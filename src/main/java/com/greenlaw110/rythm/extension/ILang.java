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
package com.greenlaw110.rythm.extension;

import com.greenlaw110.rythm.template.ITemplate;

import java.util.*;

import static com.greenlaw110.rythm.internal.compiler.TemplateClass.CN_SUFFIX;

/**
 * Specify a language (e.g. JavaScript) or a format (e.g. csv). The information could be used by
 * Rythm to support {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#FEATURE_NATURAL_TEMPLATE_ENABLED
 * natural template feature} and
 * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#FEATURE_SMART_ESCAPE_ENABLED smart escape feature}
 */
public interface ILang {

    /**
     * Return comment start. E.g. for HTML, it should be <code>&lt!--</code>
     *
     * @return comment start
     */
    String commentStart();

    /**
     * Return comment end. For HTML it should be <code>--&gt;</code>
     *
     * @return comment end
     */
    String commentEnd();

    /**
     * Return escape scheme
     *
     * @return escape
     */
    ITemplate.Escape escape();

    /**
     * Some lang could be embedded into another. E.g. JS and CSS could be
     * embedded into HTML. This method returns a regex string the direct the start
     * of the embedded lang.
     * <p/>
     * <p>Note the regex string must support group and the {@link java.util.regex.Matcher#group(int) group 1}
     * must be the captured block start. For example, JS block start is &lt;script&gt; or
     * &lt;script type="..."...&gt;, then the <code>blockStart</code> method of JS lang should be
     * <code>(\&lt;\s*script\s*.*?\&lt;).*</code></p>
     *
     * @return block start
     */
    String blockStart();

    /**
     * Return a regex string indicate an end of a lang block
     *
     * @return block end
     * @see #blockStart() for regex requirement
     */
    String blockEnd();

    /**
     * Return true if this lang impl allow another lang be
     * embedded inside. e.g. HTML should return true
     * for this method because it allows JS and CSS
     * be embedded inside
     *
     * @return true if this lang allows embedded lang
     */
    boolean allowInternalLang();

    /**
     * Return a set of other langs that could embed this
     * lang impl. For example, JS should return a Set
     * contains an HTML impl. If no other lang is allowed
     * to embed this lang, then an empty set shall
     * be returned
     *
     * @return true if this lang allows external lang
     */
    Set<ILang> allowedExternalLangs();

    /**
     * Set the parent lang to the embedded lang
     *
     * @param parent
     */
    void setParent(ILang parent);

    /**
     * Return parent lang or null if there is no parent
     * set on it
     *
     * @return parent lang
     */
    ILang getParent();

    /**
     * Return a string that could be write into
     * the target java source code to create an instance
     * of this Lang
     *
     * @return the java code
     */
    String newInstanceStr();


    public static class DefImpl implements ILang, Cloneable {

        public static final DefImpl HTML = new DefImpl("HTML", "<!--", "-->", ITemplate.Escape.XML) {
            @Override
            public boolean allowInternalLang() {
                // HTML allow CSS and JS inside
                return true;
            }
        };
        
        public static final DefImpl XML = new DefImpl("XML", "<!--", "-->", ITemplate.Escape.XML);

        public static final DefImpl JS = new DefImpl("JS", "/*", "*/", ITemplate.Escape.JS, "(<\\s*script[^<>]*?>).*", "(\\<\\/\\s*script\\s*\\>).*") {
            @Override
            public Set<ILang> allowedExternalLangs() {
                Set<ILang> set = new HashSet<ILang>();
                set.add(HTML);
                return set;
            }
        };

        public static final DefImpl CSS = new DefImpl("CSS", "/*", "*/", ITemplate.Escape.JS, "(<\\s*style[^<>]*?>).*", "(\\<\\/\\s*style\\s*\\>).*") {
            @Override
            public Set<ILang> allowedExternalLangs() {
                Set<ILang> set = new HashSet<ILang>();
                set.add(HTML);
                return set;
            }
        };

        public static final DefImpl JSON = new DefImpl("JSON", null, null, ITemplate.Escape.JSON);
        public static final DefImpl CSV = new DefImpl("CSV", null, null, ITemplate.Escape.CSV);

        private final String id;
        private final String commentStart;
        private final String commentEnd;
        private final ITemplate.Escape escape;

        private final String blockStart;
        private final String blockEnd;

        private ILang parent;

        protected DefImpl(String id, String commentStart, String commentEnd, ITemplate.Escape escape) {
            this(id, commentStart, commentEnd, escape, null, null);
        }

        protected DefImpl(String id, String commentStart, String commentEnd, ITemplate.Escape escape, String blockStart, String blockEnd) {
            this.id = id;
            this.commentEnd = commentEnd;
            this.commentStart = commentStart;
            this.escape = escape;
            this.blockEnd = blockEnd;
            this.blockStart = blockStart;
        }

        @Override
        public String newInstanceStr() {
            StringBuilder sb = new StringBuilder();
            String clsName = ILang.class.getName();
            sb.append("(").append(clsName).append(")").append(clsName).append(".DefImpl.").append(this.id).append(".clone()");
            return sb.toString();
        }

        @Override
        public String commentStart() {
            return commentStart;
        }

        @Override
        public String commentEnd() {
            return commentEnd;
        }

        @Override
        public ITemplate.Escape escape() {
            return escape;
        }

        @Override
        public String blockStart() {
            return blockStart;
        }

        @Override
        public String blockEnd() {
            return blockEnd;
        }

        @Override
        public boolean allowInternalLang() {
            return false;
        }

        @Override
        public void setParent(ILang parent) {
            this.parent = parent;
        }

        @Override
        public ILang getParent() {
            return parent;
        }

        @Override
        public Set<ILang> allowedExternalLangs() {
            return Collections.EMPTY_SET;
        }

        @Override
        public String toString() {
            return newInstanceStr();
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public static ILang probeFileName(String fileName, ILang def) {
            Map<String, ILang> m = new HashMap<String, ILang>();

            String[] sa = {"html" + CN_SUFFIX, "html_rythm" + CN_SUFFIX, "xml" + CN_SUFFIX, "xml_rythm" + CN_SUFFIX};
            for (String s : sa) m.put(s, DefImpl.XML);

            sa = new String[]{"js" + CN_SUFFIX, "js_rythm" + CN_SUFFIX, "css" + CN_SUFFIX, "css_rythm" + CN_SUFFIX};
            for (String s : sa) m.put(s, DefImpl.JS);

            sa = new String[]{"json" + CN_SUFFIX,};
            for (String s : sa) m.put(s, DefImpl.JSON);

            sa = new String[]{"csv" + CN_SUFFIX, "csv_rythm" + CN_SUFFIX};
            for (String s : sa) m.put(s, DefImpl.CSV);

            for (String k : m.keySet()) {
                if (fileName.endsWith(k)) {
                    return m.get(k);
                }
            }

            return def;
        }
    }
}
