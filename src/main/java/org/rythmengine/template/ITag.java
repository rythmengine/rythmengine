/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

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

import org.rythmengine.internal.TemplateBuilder;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.Escape;
import org.rythmengine.utils.RawData;
import org.rythmengine.utils.S;

import java.util.*;

/**
 * Define a tag interface.
 */
public interface ITag {

    /**
     * A datastructure to store tag calling parameter
     */
    public static class __Parameter {
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
        public __Parameter(String name, Object value) {
            this.name = "".equals(name) ? null : name;
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringBuilder("[").append(name).append("]:").append(value).toString();
        }
    }

    /**
     * A list of {@link org.rythmengine.template.ITag.__Parameter}
     */
    public static class __ParameterList implements Iterable<__Parameter> {
        private List<__Parameter> lp = new ArrayList<__Parameter>();

        /**
         * Add an new parameter to the list specified by name and value
         *
         * @param name
         * @param value
         */
        public void add(String name, Object value) {
            lp.add(new __Parameter(name, value));
        }

        /**
         * Return parameter value by name from the list
         *
         * @param name
         * @return parameter by name
         */
        public Object getByName(String name) {
            for (__Parameter para : lp) {
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
         * @return parameter by name
         */
        public <T> T getByName(String name, T defVal) {
            for (__Parameter para : lp) {
                if (name.equals(para.name)) return (T) para.value;
            }
            return defVal;
        }

        /**
         * Get default parameter value. Which is the first parameter in the list
         *
         * @return default parameter
         */
        public Object getDefault() {
            return getByPosition(0);
        }

        /**
         * Get parameter value by position in the list
         *
         * @param pos
         * @return parameter by position
         */
        public Object getByPosition(int pos) {
            if (pos >= lp.size()) return null;
            return lp.get(pos).value;
        }

        @Override
        public Iterator<__Parameter> iterator() {
            return lp.iterator();
        }

        /**
         * How many parameters are stored in the list
         *
         * @return size of the param list
         */
        public int size() {
            return lp.size();
        }

        /**
         * Get a {@link org.rythmengine.template.ITag.__Parameter} instance by position in the list
         *
         * @param i
         * @return parameter by position
         */
        public __Parameter get(int i) {
            return lp.get(i);
        }

        /**
         * Convert the parameter list into a map mapped values to names
         *
         * @return parameter as map mapped by name
         */
        public Map<String, Object> asMap() {
            Map<String, Object> m = new HashMap<String, Object>();
            for (__Parameter p : lp) {
                if (p.name != null) m.put(p.name, p.value);
            }
            return m;
        }

        private String uuid = null;

        /**
         * Used to create unique key for <code>@cacheFor()</code> transformer
         *
         * @return the UUID string
         */
        public String toUUID() {
            if (null == uuid) {
                StringBuilder sb = new StringBuilder();
                for (__Parameter p : lp) {
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
    public abstract static class __Body extends TemplateBuilder {

        /**
         * the body logger
         */
        protected final static ILogger __bodyLogger = Logger.get(__Body.class);

        /**
         * The context template
         */
        protected TemplateBase __context;

        /**
         * This body itself.
         */
        protected __Body __self = this;

        /**
         * Construct a body with context template instance
         *
         * @param context
         */
        public __Body(TemplateBase context) {
            __context = context;
        }

        private void call(StringBuilder out) {
            __buffer = out;
            try {
                __call();
            } finally {
                __buffer = null;
            }
        }

        public final __Body pe(Object o) {
            if (null == o) return this;
            if (o instanceof RawData) {
                return (__Body) p(o);
            }
            Escape escape = __context.__defaultEscape();
            return (__Body) pe(o, escape);
        }

        protected abstract void __setBodyArgByName(String name, Object val);

        protected abstract void __setBodyArgByPos(int pos, Object val);

        public String render(Object... vals) {
            StringBuilder sb = new StringBuilder();
            render(sb, vals);
            return sb.toString();
        }

        public void render(StringBuilder out, Object... vals) {
            for (int i = vals.length - 1; i > -1; --i) {
                __setBodyArgByPos(i, vals[i]);
            }
            call(out);
        }

        public void render(__ParameterList parameterList, StringBuilder out) {
            if (null != parameterList) {
                for (int i = 0; i < parameterList.size(); ++i) {
                    __Parameter p = parameterList.get(i);
                    if (!S.isEmpty(p.name)) {
                        __setBodyArgByName(p.name, p.value);
                    } else {
                        __setBodyArgByPos(i, p.value);
                    }
                }
            }
            call(out);
        }

        protected ITemplate __template() {
            return __context != null ? __context : caller();
        }

        protected abstract void __call();

        public abstract void __setProperty(String name, Object val);

        public abstract Object __getProperty(String name);
    }

    /**
     * Get the tag name
     * 
     * @return tag name
     */
    String __getName();

    /**
     * Set body context and return this tag
     * @param body
     * @return this tag
     */
    public ITag __setBodyContext(__Body body);

    /**
     * Call this tag
     * 
     * @param line the number of the caller template line which invoke this tag
     */
    void __call(int line);

}
