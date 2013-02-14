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
package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.internal.TemplateBuilder;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;

import java.util.*;

/**
 * Define a tag interface.
 */
public interface ITag extends ITemplate {

    /**
     * A datastructure to store tag calling parameter
     */
    public static class Parameter {
        /**
         * Parameter name
         */
        public String name;
        /**
         * Parameter value
         */
        public Object value;

        /**
         * Construct a parameter with name and value
         *
         * @param name
         * @param value
         */
        public Parameter(String name, Object value) {
            this.name = "".equals(name) ? null : name;
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringBuilder("[").append(name).append("]:").append(value).toString();
        }
    }

    /**
     * A list of {@link Parameter}
     */
    public static class ParameterList implements Iterable<Parameter> {
        private List<Parameter> lp = new ArrayList<Parameter>();

        /**
         * Add an new parameter to the list specified by name and value
         *
         * @param name
         * @param value
         */
        public void add(String name, Object value) {
            lp.add(new Parameter(name, value));
        }

        /**
         * Return parameter value by name from the list
         *
         * @param name
         * @return
         */
        public Object getByName(String name) {
            for (Parameter para : lp) {
                if (name.equals(para.name)) return para.value;
            }
            return null;
        }

        /**
         * Return parameter value by name and do type cast to left value type.
         * A default value is specified in case the parameter does not exist in
         * the list
         *
         * @param name
         * @param defVal
         * @param <T>
         * @return
         */
        public <T> T getByName(String name, T defVal) {
            for (Parameter para : lp) {
                if (name.equals(para.name)) return (T) para.value;
            }
            return defVal;
        }

        /**
         * Get default parameter value. Which is the first parameter in the list
         *
         * @return
         */
        public Object getDefault() {
            return getByPosition(0);
        }

        /**
         * Get parameter value by position in the list
         *
         * @param pos
         * @return
         */
        public Object getByPosition(int pos) {
            if (pos >= lp.size()) return null;
            return lp.get(pos).value;
        }

        @Override
        public Iterator<Parameter> iterator() {
            return lp.iterator();
        }

        /**
         * How many parameters are stored in the list
         *
         * @return
         */
        public int size() {
            return lp.size();
        }

        /**
         * Get a {@link Parameter} instance by position in the list
         *
         * @param i
         * @return
         */
        public Parameter get(int i) {
            return lp.get(i);
        }

        /**
         * Convert the parameter list into a map mapped values to names
         *
         * @return
         */
        public Map<String, Object> asMap() {
            Map<String, Object> m = new HashMap<String, Object>();
            for (Parameter p : lp) {
                if (p.name != null) m.put(p.name, p.value);
            }
            return m;
        }

        private String uuid = null;

        /**
         * Used to create unique key for <code>@cacheFor()</code> transformer
         *
         * @return
         */
        public String toUUID() {
            if (null == uuid) {
                StringBuilder sb = new StringBuilder();
                for (Parameter p : lp) {
                    sb.append(";").append(p.name).append("=").append(p.value);
                }
                String s = sb.toString();
                if (S.isEmpty(s)) s = "EMPTY_PARAMETER_LIST";
                uuid = UUID.nameUUIDFromBytes(s.getBytes()).toString();
            }
            return uuid;
        }
    }

    /**
     * Defines a tag body type
     */
    public abstract static class Body extends TemplateBuilder {

        /**
         * the body logger
         */
        protected ILogger __bodyLogger = Logger.get(Body.class);

        /**
         * The context template
         */
        protected TemplateBase _context;

        /**
         * This body itself.
         */
        protected Body self = this;

        /**
         * Construct a body with context template instance
         *
         * @param context
         */
        public Body(TemplateBase context) {
            _context = context;
        }

        public StringBuilder getBuffer() {
            return __buffer;
        }

        public ITemplate getContext() {
            return _context;
        }

        public void setOut(StringBuilder out) {
            //_context.setBuffer(buffer);
        }

        private void call(StringBuilder out) {
            __buffer = out;
            try {
                _call();
            } finally {
                __buffer = null;
            }
        }

        public final Body pe(Object o) {
            if (null == o) return this;
            if (o instanceof ITemplate.RawData) {
                return (Body) p(o);
            }
            ITemplate.Escape escape = _context.defaultEscape();
            return (Body) pe(o, escape);
        }

        protected abstract void setBodyArgByName(String name, Object val);

        protected abstract void setBodyArgByPos(int pos, Object val);

        public String render(Object... vals) {
            StringBuilder sb = new StringBuilder();
            render(sb, vals);
            return sb.toString();
        }

        public void render(StringBuilder out, Object... vals) {
            for (int i = vals.length - 1; i > -1; --i) {
                setBodyArgByPos(i, vals[i]);
            }
            call(out);
        }

        public void render(ParameterList parameterList, StringBuilder out) {
            if (null != parameterList) {
                for (int i = 0; i < parameterList.size(); ++i) {
                    Parameter p = parameterList.get(i);
                    if (!S.isEmpty(p.name)) {
                        setBodyArgByName(p.name, p.value);
                    } else {
                        setBodyArgByPos(i, p.value);
                    }
                }
            }
            call(out);
        }

        protected abstract void _call();

        public abstract void setProperty(String name, Object val);

        public abstract Object getProperty(String name);
//        @Override
//        public String toString() {
////            StringBuilder old = getBuffer();
////            setBuffer(new StringBuilder());
////            call();
////            String s = getBuffer().toString();
////            setBuffer(old);
////            return s;
//            StringBuilder sbNew = new StringBuilder();
//            StringBuilder sbOld = _context.getBuffer();
//            _context.setBuffer(sbNew);
//            this.__buffer = sbNew;
//            _call();
//            String s = sbNew.toString();
//            _context.setBuffer(sbOld);
//            this.__buffer = null;
//            return s;
////            __buffer.setLength(0);
////            call();
////            return __buffer.toString();
//        }
    }

    String getName();

    void call(int line);
}
