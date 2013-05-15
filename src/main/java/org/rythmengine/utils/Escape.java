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
package org.rythmengine.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.rythmengine.RythmEngine;
import org.rythmengine.template.ITemplate;

import java.util.Arrays;

/**
 * Escape
 */
public enum Escape {
    /**
     * Indicate raw escape scheme, i.e. no other escape scheme should apply
     */
    RAW,
    /**
     * CSV escape scheme
     */
    CSV {
        @Override
        protected RawData apply_(String s) {
            // fix https://github.com/greenlaw110/Rythm/issues/155
            //return org.rythmengine.utils.S.escapeCsv(s);
            return new RawData(CSVEscape.escape(s));
        }
    },
    /**
     * HTML escape scheme
     */
    HTML

    {
        @Override
        protected RawData apply_(String s) {
            return org.rythmengine.utils.S.escapeHtml(s);
        }
    },
    /**
     * javascript escape scheme
     */
    JS {
        @Override
        protected RawData apply_(String s) {
            return org.rythmengine.utils.S.escapeJavaScript(s);
        }
    },
    /**
     * JSON escape scheme
     */
    JSON

    {
        @Override
        protected RawData apply_(String s) {
            return org.rythmengine.utils.S.escapeJson(s);
        }
    },
    /**
     * XML escape scheme
     */
    XML {
        @Override
        protected RawData apply_(String s) {
            return org.rythmengine.utils.S.escapeXml(s);
        }
    };

    /**
     * Apply this escape scheme to the object's string representation
     * @param o
     * @return the raw data been processed out from the object
     */
    public RawData apply(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData) return (RawData)o;
        String s = o.toString();
        return apply_(s);
    }

    protected RawData apply_(String s) {
        return new RawData(s);
    }

    private static String[] sa_ = null;

    public static String[] stringValues() {
        if (null == sa_) {
            Escape[] ea = values();
            String[] sa = new String[ea.length];
            for (int i = 0; i < ea.length; ++i) {
                sa[i] = ea[i].toString();
            }
            Arrays.sort(sa);
            sa_ = sa;
        }
        return sa_.clone();
    }
    
    public static Escape valueOfIngoreCase(Object o) {
        String escape = S.str(o);
        if (S.empty(escape)) {
            return RythmEngine.get().conf().defaultCodeType().escape();
        }
        escape = escape.toUpperCase();
        if (escape.equals("JAVASCRIPT")) escape = "JS";
        return valueOf(escape);
    }

    public static Escape valueOfIngoreCase(ITemplate template, Object o) {
        String escape = S.str(o);
        if (S.empty(escape)) {
            if (null != template) {
                return template.__curEscape();
            } else {
                RythmEngine engine = RythmEngine.get();
                if (null == engine) {
                    return Escape.RAW;
                } else {
                    return RythmEngine.get().conf().defaultCodeType().escape();
                }
            }
        }
        escape = escape.toUpperCase();
        if (escape.equals("JAVASCRIPT")) escape = "JS";
        return valueOf(escape);
    }

    private static class CSVEscape {
        private static final char CSV_DELIMITER = ',';
        private static final char CSV_QUOTE = '"';
        private static final String CSV_QUOTE_STR = String.valueOf(CSV_QUOTE);
        private static final char[] CSV_SEARCH_CHARS = 
            new char[] {CSV_DELIMITER, CSV_QUOTE, CharUtils.CR, CharUtils.LF};

        private static String escape(String s) {
            if (StringUtils.containsNone(s, CSV_SEARCH_CHARS)) {
                return s;
            }
            StringBuilder sb = new StringBuilder(CSV_QUOTE_STR);
            sb.append(StringUtils.replace(s, CSV_QUOTE_STR, CSV_QUOTE_STR + CSV_QUOTE_STR));
            return sb.append(CSV_QUOTE_STR).toString();
            //return StringEscapeUtils.escapeCsv(s);
        }
    }
}
