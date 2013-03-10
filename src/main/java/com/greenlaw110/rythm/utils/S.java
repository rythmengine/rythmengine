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
package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.internal.CacheKey;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.*;
import java.util.*;

/**
 * A utility class providing String manipulation methods. Commonly used in template engine process.
 * <p/>
 * <p>Note all methods defined in this class is null safe.
 * if any input is <code>null</code> the return value is <code>""</code></p>
 * <p/>
 * <p>An instance of this utility is exposed to any Rythm template
 * via {@link com.greenlaw110.rythm.template.TemplateBase#s()} method and can be used
 * in template source code freely. E.g.</p>
 * <p/>
 * <pre><code>
 * {@literal @}if(s().empty(name)) {
 * <div class="alert alert-error">user name is empty!</div>
 * }
 * </code></pre>
 */
public class S {
    public static final S INSTANCE = new S();
    public static final String EMPTY_STR = "";
    private static final ILogger logger = Logger.get(S.class);

    /**
     * Determine if a given String is null or empty. By empty it
     * means equals to <code>""</code> after do a {@link String#trim()}
     * operation on it
     *
     * @param s
     * @return true if the String specified is empty or null
     */
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }

    /**
     * Alias of {@link #isEmpty(String)}
     *
     * @param s
     * @return true if the given string is empty
     */
    public static boolean empty(String s) {
        return null == s || "".equals(s.trim());
    }

    /**
     * Determine if a given String is NOT null or empty.
     *
     * @param s
     * @return false if the String specified is empty or null
     * @see #isEmpty(String)
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * Alias of {@link #isNotEmpty(String)}
     *
     * @param s
     * @return true if the give string is not empty
     */
    public static boolean notEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * Determine if a given Object instance is null or empty after it
     * converted to a String.
     *
     * @param o
     * @return true if the object string representation is empty
     * @see #isEmpty(String)
     */
    public static boolean isEmpty(Object o) {
        return null == o || "".equals(o.toString().trim());
    }

    /**
     * Alias of {@link #isEmpty(Object)}
     *
     * @param o
     * @return true if the object string representation is empty
     */
    public static boolean empty(Object o) {
        return null == o || "".equals(str(o).trim());
    }

    /**
     * Determine if a given Object instance is NOT null or empty.
     *
     * @param o
     * @return false if the String specified is empty or null
     * @see #isEmpty(Object)
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * Alias of {@link #isNotEmpty(Object)}
     *
     * @param o
     * @return true if object str representation is not empty
     */
    public static boolean notEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * The modifier used to indicate the comparison should
     * ignore case
     *
     * @see #isEqual(String, String, int)
     */
    public static final int IGNORECASE = 0x00001000;

    /**
     * The modifier used to indicate the comparison should
     * ignore space
     *
     * @see #isEqual(String, String, int)
     */
    public static final int IGNORESPACE = 0x00002000;

    /**
     * Check if two String is equal. This comparison is {@link #IGNORECASE case sensitive}
     * and {@link #IGNORESPACE space sensitive}
     *
     * @param s1
     * @param s2
     * @return true if the two specified Strings are equal to each other
     */
    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Alias of {@link #isEqual(String, String)}
     *
     * @param s1
     * @param s2
     * @return true if s1 equals s2
     */
    public static boolean eq(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Check if two Object is equal after converted into String
     *
     * @param o1
     * @param o2
     * @return true if the specified two object instance are equal after converting to String
     */
    public static boolean isEqual(Object o1, Object o2) {
        return isEqual(str(o1), str(o2));
    }

    /**
     * Alias of {@link #isEqual(Object, Object)}
     *
     * @param o1
     * @param o2
     * @return true if o1's str equals o2's str
     */
    public static boolean eq(Object o1, Object o2) {
        return isEqual(str(o1), str(o2), 0);
    }

    /**
     * Alias of {@link #isEqual(String, String, int)}
     *
     * @param s1
     * @param s2
     * @param modifier
     * @return true if o1's str equals o2's str
     */
    public static boolean eq(String s1, String s2, int modifier) {
        return isEqual(s1, s2, modifier);
    }

    /**
     * Determine whether two string instance is equal based on
     * the modifier passed in.
     * <p/>
     * <p>
     * is 2 strings equal case insensitive?
     * <code>S.isEqual(s1, s2, S.IGNORECASE)</code>
     * </p>
     * <p/>
     * <p>
     * is 2 strings equals case and space insensitive?
     * <code>S.isEqual(s1, s2, S.IGNORECASE & S.IGNORESPACE)</code>
     * </p>
     *
     * @param s1
     * @param s2
     * @param modifier
     * @return true if s1 equals s2
     */
    public static boolean isEqual(String s1, String s2, int modifier) {
        if (null == s1) {
            return s2 == null;
        }
        if (null == s2)
            return false;
        if ((modifier & IGNORESPACE) != 0) {
            s1 = s1.trim();
            s2 = s2.trim();
        }
        if ((modifier & IGNORECASE) != 0) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return s1.equals(s2);
        }
    }

    /**
     * Alias of {@link #toString(Object)}
     *
     * @param o
     * @return the string representation of object
     */
    public static String str(Object o) {
        return null == o ? "" : o.toString();
    }

    /**
     * Safe convert an Object to String. if the Object
     * is <code>null</code> than <code>""</code> is
     * returned
     *
     * @param o
     * @return String representation of the object
     */
    public static String toString(Object o) {
        return null == o ? "" : o.toString();
    }

    /**
     * Remove all line breaks from string representation of specified object O
     *
     * @param o
     * @return String
     */
    public static String removeAllLineBreaks(Object o) {
        String s = str(o);
        return s.replaceAll("[\n\r]+", " ");
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object without any escaping.
     *
     * @param o
     * @return raw data
     */
    @Transformer
    public static RawData raw(Object o) {
        return new RawData(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with default {@link #escapeXml(Object)} escaping or if the current
     * render engine exists return the escape specified by the default lang of the 
     * current render engine
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     *
     * @param o
     * @return escaped data
     */
    @Transformer
    public static RawData escape(Object o) {
        if (empty(o)) return RawData.NULL;
        Escape escape = Rythm.RenderTime.getEscape();
        return escape.apply(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with specified escaping scheme.
     * <p/>
     * <p>
     * You can pass any type of object to specify the escaping scheme. However
     * they will in the end converted to {@link #toString(Object) converted to String}
     * and then determine which escaping to use:
     * <p/>
     * <ul>
     * <li>json: {@link #escapeJSON(Object)} </li>
     * <li>xml: {@link #escapeXML(Object)} </li>
     * <li>javascript|js: {@link #escapeJavaScript(Object)} </li>
     * <li>csv: {@link #escapeCSV(Object)} </li>
     * <li>html: {@link #escapeCSV(Object)} </li>
     * </ul>
     * </p>
     * <p/>
     * <p>Note Object instance is converted to String before escaping</p>
     *
     * @param o
     * @param escape
     * @return escaped data
     */
    @Transformer
    public static RawData escape(Object o, Object escape) {
        if (isEmpty(o)) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        if (escape instanceof Escape) return ((Escape) escape).apply(o);
        if (isEmpty(escape)) return escape(o);
        String se = escape.toString();
        if ("json".equalsIgnoreCase(se)) return escapeJson(o);
        if ("xml".equalsIgnoreCase(se)) return escapeXml(o);
        if ("javascript".equalsIgnoreCase(se) || "js".equalsIgnoreCase(se)) return escapeJavaScript(o);
        if ("csv".equalsIgnoreCase(se)) return escapeCsv(o);
        if ("html".equalsIgnoreCase(se)) return escapeHtml(o);
        if ("raw".equalsIgnoreCase(se)) return raw(o);
        throw new IllegalArgumentException("Unknown escape scheme: " + se);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with HTML escaping
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     *
     * @param o
     * @return html escaped data
     */
    @Transformer
    public static RawData escapeHTML(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        return new RawData(StringEscapeUtils.escapeHtml4(o.toString()));
    }

    /**
     * Alias of {@link #escapeHTML(Object)}
     *
     * @param o
     * @return html escaped data
     */
    public static RawData escapeHtml(Object o) {
        return escapeHTML(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with CSV escaping
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     *
     * @param o
     * @return csv escaped data
     */
    @Transformer
    public static RawData escapeCSV(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        return new RawData(StringEscapeUtils.escapeCsv(o.toString()));
    }

    /**
     * Alias of {@link #escapeCSV(Object)}
     *
     * @param o
     * @return CSV escaped data
     */
    public static RawData escapeCsv(Object o) {
        return escapeCSV(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with JSON escaping
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     * <p/>
     * <p>After the object get escaped, the output string is safe to put into a
     * JSON block</p>
     *
     * @param o
     * @return JSON escaped data
     */
    @Transformer
    public static RawData escapeJSON(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        String s0 = o.toString();
        s0 = s0.replaceAll("[\n\r]+", "\\\\\\n").replaceAll("[ \t]+", " ").replaceAll("\"", "\\\\\"");
        return new RawData(s0);
    }

    /**
     * Alias of {@link #escapeCSV(Object)}
     *
     * @param o
     * @return JSON escaped data
     */
    public static RawData escapeJson(Object o) {
        return escapeJSON(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with JavaScript escaping
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     * <p/>
     * <p>After the object get escaped, the output string is safe to put inside a pair of
     * JavaScript quotation marks</p>
     *
     * @param o
     * @return JavaScript escaped data
     */
    @Transformer
    public static RawData escapeJavaScript(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        return new RawData(StringEscapeUtils.escapeEcmaScript(o.toString()));
    }

    /**
     * Alias of {@link #escapeJavaScript(Object)}
     *
     * @param o
     * @return JavaScript escaped data
     */
    public static RawData escapeJavascript(Object o) {
        return escapeJavaScript(o);
    }

    /**
     * Alias of {@link #escapeJavaScript(Object)}
     *
     * @param o
     * @return JavaScript escaped data
     */
    @Transformer
    public static RawData escapeJS(Object o) {
        return escapeJavaScript(o);
    }

    /**
     * Return a {@link com.greenlaw110.rythm.utils.RawData} type wrapper of
     * an object with XML escaping
     * <p/>
     * <p>Object is {@link #toString(Object) converted to String} before escaping</p>
     * <p/>
     * <p>After the object get escaped, the output string is safe to put inside a XML
     * attribute
     *
     * @param o
     * @return XML escaped data
     */
    @Transformer
    public static RawData escapeXML(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        return new RawData(StringEscapeUtils.escapeXml(o.toString()));
    }

    /**
     * Alias of {@link #escapeXML(Object)}
     *
     * @param o
     * @return XML escaped data
     */
    public static RawData escapeXml(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        return new RawData(StringEscapeUtils.escapeXml(o.toString()));
    }

    /**
     * Escape for regular expression
     *
     * @param o
     * @return Regex escaped data
     */
    public static RawData escapeRegex(Object o) {
        if (null == o) return RawData.NULL;
        if (o instanceof RawData)
            return (RawData) o;
        String s = o.toString();
        return new RawData(s.replaceAll("([\\/\\*\\{\\}\\<\\>\\-\\\\\\!])", "\\\\$1"));
    }

    /**
     * Strip the prefix and suffix from an object's String representation and
     * return the result
     * <p/>
     * <p>For example: </p>
     * <p/>
     * <pre><code>Object o = "xxBByy";
     * String s = S.strip(o, "xx", "yy")</code></pre>
     * <p/>
     * <p>At the end above code, <code>s</code> should be "BB"</p>
     *
     * @param o
     * @param prefix
     * @param suffix
     * @return the String result
     */
    public static final String strip(Object o, String prefix, String suffix) {
        if (null == o) return "";
        String s = o.toString();
        s = s.trim();
        if (s.startsWith(prefix)) s = s.substring(prefix.length());
        if (s.endsWith(suffix)) s = s.substring(0, s.length() - suffix.length());
        return s;
    }

    /**
     * Strip the brace from an object's string representation and return the result
     *
     * @param o
     * @return the string result
     */
    public static final String stripBrace(Object o) {
        return strip(o, "(", ")");
    }

    /**
     * Strip the quotation mark from an object's string representation and return the result
     *
     * @param o
     * @return the String result
     */
    public static final String stripQuotation(Object o) {
        return strip(strip(o, "\"", "\""), "'", "'");
    }
    
    /**
     * Strip off both brace and quotation
     *
     * @param o
     * @return the string result
     */
    public static final String stripBraceAndQuotation(Object o) {
        if (null == o) return "";
        String s = stripBrace(o);
        s = stripQuotation(s);
        return s;
    }

    /**
     * Shrink spaces in an object's string representation by merge multiple
     * spaces, tabs into one space, and multiple line breaks into one line break
     *
     * @param o
     * @return the string result
     */
    public static String shrinkSpace(Object o) {
        if (null == o) return "";
        return o.toString().replaceAll("[\r\n]+", "\n").replaceAll("[ \\t\\x0B\\f]+", " ");
    }


    /**
     * Capitalize the first character of every word of the specified object's
     * string representation. Words are separated by space
     *
     * @param o 
     * @return the string result
     */
    @Transformer
    public static String capitalizeWords(Object o) {
        if (null == o) return "";
        String source = o.toString();
        char prevc = ' '; // first char of source is capitalized
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c != ' ' && prevc == ' ') {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
            prevc = c;
        }
        return sb.toString();
    }

    /**
     * Replace accent character (usually found in European languages) of the String representation of a
     * give object to non-accent char.
     *
     * @param o
     * @return the string result
     */
    @Transformer
    public static String noAccents(Object o) {
        if (null == o) return "";
        String string = o.toString();
        return Normalizer.normalize(string, Normalizer.Form.NFKC).replaceAll("[àáâãäåāąă]", "a").replaceAll("[çćčĉċ]", "c").replaceAll("[ďđð]", "d").replaceAll("[èéêëēęěĕė]", "e").replaceAll("[ƒſ]", "f").replaceAll("[ĝğġģ]", "g").replaceAll("[ĥħ]", "h").replaceAll("[ìíîïīĩĭįı]", "i").replaceAll("[ĳĵ]", "j").replaceAll("[ķĸ]", "k").replaceAll("[łľĺļŀ]", "l").replaceAll("[ñńňņŉŋ]", "n").replaceAll("[òóôõöøōőŏœ]", "o").replaceAll("[Þþ]", "p").replaceAll("[ŕřŗ]", "r").replaceAll("[śšşŝș]", "s").replaceAll("[ťţŧț]", "t").replaceAll("[ùúûüūůűŭũų]", "u").replaceAll("[ŵ]", "w").replaceAll("[ýÿŷ]", "y").replaceAll("[žżź]", "z").replaceAll("[æ]", "ae").replaceAll("[ÀÁÂÃÄÅĀĄĂ]", "A").replaceAll("[ÇĆČĈĊ]", "C").replaceAll("[ĎĐÐ]", "D").replaceAll("[ÈÉÊËĒĘĚĔĖ]", "E").replaceAll("[ĜĞĠĢ]", "G").replaceAll("[ĤĦ]", "H").replaceAll("[ÌÍÎÏĪĨĬĮİ]", "I").replaceAll("[Ĵ]", "J").replaceAll("[Ķ]", "K").replaceAll("[ŁĽĹĻĿ]", "L").replaceAll("[ÑŃŇŅŊ]", "N").replaceAll("[ÒÓÔÕÖØŌŐŎ]", "O").replaceAll("[ŔŘŖ]", "R").replaceAll("[ŚŠŞŜȘ]", "S").replaceAll("[ÙÚÛÜŪŮŰŬŨŲ]", "U").replaceAll("[Ŵ]", "W").replaceAll("[ÝŶŸ]", "Y").replaceAll("[ŹŽŻ]", "Z").replaceAll("[ß]", "ss");
    }

    /**
     * Make the first character be lowercase of the given object's string representation
     *
     * @param o
     * @return the string result
     */
    @Transformer
    public static String lowerFirst(Object o) {
        if (null == o) return "";
        String string = o.toString();
        if (string.length() == 0) {
            return string;
        }
        return ("" + string.charAt(0)).toLowerCase() + string.substring(1);
    }

    /**
     * Make the first character be uppercase of the given object's string representation
     *
     * @param o
     * @return the string result
     */
    @Transformer
    public static String capFirst(Object o) {
        if (null == o) return "";
        String string = o.toString();
        if (string.length() == 0) {
            return string;
        }
        return ("" + string.charAt(0)).toUpperCase() + string.substring(1);
    }

    /**
     * Turn an object's String representation into Camel Case
     *
     * @param obj
     * @return the string result
     */
    @Transformer
    public static String camelCase(Object obj) {
        if (null == obj) return "";
        String string = obj.toString();
        //string = noAccents(string);
        //string = string.replaceAll("[^\\w ]", "");
        StringBuilder result = new StringBuilder(string.length());
        String[] sa = string.split(" ");
        int l = sa.length;
        for (int i = 0; i < l; ++i) {
            if (i > 0) result.append(" ");
            for (String s : sa[i].split("_")) {
                result.append(capFirst(s));
            }
        }
        return result.toString();
    }
    

    /**
     * Change line break in the data string into <tt><br/></tt>
     * @param data
     * @return raw data of transformed result
     */
    @Transformer
    public static RawData nl2br(RawData data) {
        return new RawData(data.toString().replace("\n", "<br/>"));
    }

    /**
     * Change line break in the data string into <tt><br/></tt>
     * @param data
     * @return raw data of transformed result
     */
    @Transformer
    public static RawData nl2br(Object data) {
        return new RawData(StringEscapeUtils.escapeHtml4(str(data)).replace("\n", "<br/>"));
    }

    /**
     * encode using utf-8
     * 
     * @param data
     * @return encoded 
     */
    @Transformer
    public static String urlEncode(Object data) {
        if (null == data) return "";
        String entity = data.toString();
        try {
            String encoding = "utf-8";
            return URLEncoder.encode(entity, encoding);
        } catch (UnsupportedEncodingException e) {
            Logger.error(e, entity);
        }
        return entity;
    }

    /**
     * Format the number with specified pattern, language and locale
     * @param number
     * @param pattern
     * @param locale
     * @return the formatted String
     * @see DecimalFormatSymbols
     */
    @Transformer(requireEngine = true)
    public static String format(Number number, String pattern, Locale locale) {
        return format(null, number, pattern, locale);
    }
    
    /**
     * Format the number with specified engine, pattern, language and locale
     * @param number
     * @param pattern
     * @param locale
     * @return the formatted String
     * @see DecimalFormatSymbols
     */
    public static String format(RythmEngine engine, Number number, String pattern, Locale locale) {
        if (null == locale) {
            locale = I18N.locale(engine);
        }
        
        NumberFormat nf;
        if (null == pattern) nf = NumberFormat.getNumberInstance(locale);
        else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            nf = new DecimalFormat(pattern, symbols);
        }
        
        return nf.format(number);
    }

    /**
     * Format a number with specified pattern
     * 
     * @param number
     * @param pattern
     * @return formatted String
     */
    @Transformer(requireEngine = true)
    public static String format(Number number, String pattern) {
        return format(null, number, pattern, null);
    }
    
    /**
     * Format a number with specified engine, pattern
     * 
     * @param number
     * @param pattern
     * @return formatted String
     */
    public static String format(RythmEngine engine, Number number, String pattern) {
        return format(engine, number, pattern, null);
    }    

    /**
     * Format a date with engine's default format corresponding
     * to the engine's locale configured
     * 
     * @param date
     * @return the formatted String
     */
    @Transformer(requireEngine = true)
    public static String format(Date date) {
        return format(date, null, null, null);
    }

    /**
     * Format a date with specified engine's default format corresponding
     * to the engine's locale configured
     * 
     * @param date
     * @return the formatted String
     */
    public static String format(RythmEngine engine, Date date) {
        return format(engine, date, null, null, null);
    }

    /**
     * Format a date with specified pattern
     * 
     * @param date
     * @param pattern
     * @return formated string
     */
    @Transformer(requireEngine = true)
    public static String format(Date date, String pattern) {
        return format(date, pattern, null, null);
    }
    
    /**
     * Format a date with specified pattern
     * 
     * @param engine
     * @param date
     * @param pattern
     * @return formated string
     */
    public static String format(RythmEngine engine, Date date, String pattern) {
        return format(engine, date, pattern, null, null);
    }

    /**
     * Transformer. Format a date with specified pattern, language and locale
     * @param date
     * @param pattern
     * @param locale
     * @return the formatted String
     */
    @Transformer(requireEngine = true)
    public static String format(Date date, String pattern, Locale locale) {
        return format(date, pattern, locale, null);
    }

    /**
     * See {@link #format(com.greenlaw110.rythm.RythmEngine, java.util.Date, String, java.util.Locale, String)}
     * @param engine
     * @param date
     * @param pattern
     * @param locale
     * @return formatted date string
     */
    public static String format(RythmEngine engine, Date date, String pattern, Locale locale) {
        return format(engine, date, pattern, locale, null);
    }

    /**
     * Transformer. Format a date with specified pattern, lang, locale and timezone.
     * 
     * @param date
     * @param pattern
     * @param locale
     * @param timezone
     * @return the formatted String
     * @see SimpleDateFormat
     */
    @Transformer(requireEngine = true)
    public static String format(Date date, String pattern, Locale locale, String timezone) {
        return format(null, date, pattern, locale, timezone);
    }

    /**
     * Format a date with specified pattern, lang, locale and timezone. The locale
     * comes from the engine instance specified
     * 
     * @param engine
     * @param date
     * @param pattern
     * @param locale
     * @param timezone
     * @return
     */
    public static String format(RythmEngine engine, Date date, String pattern, Locale locale, String timezone) {
        if (null == locale) {
            locale = I18N.locale(engine);
        }

        DateFormat df;
        if (null != pattern) df = new SimpleDateFormat(pattern, locale);
        else df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        
        if (null != timezone) df.setTimeZone(TimeZone.getTimeZone(timezone));
        
        return df.format(date);
    }
    
    /**
     * Format size (e.g. disk space in bytes) into human readable style
     * <ul>
     * <li>When size is smaller than <code>1024L</code>, return size + <code>B</code></li>
     * <li>When size is smaller than <code>1024L ^ 2</code>, return size/1024L + <code>KB</code></li>
     * <li>When size is smaller than <code>1024L ^ 3</code>, return size/1048576L + <code>MB</code></li>
     * <li>When size is smaller than <code>1024L ^ 4</code>, return size/1073741824L + <code>GB</code></li>
     * </ul>
     * 
     * <p>The method accept any data type. When <code>null</code> is found then 
     * <code>NullPointerException</code> will be thrown out; if an <code>Number</code>
     * is passed in, it will be type cast to <code>Long</code>; otherwise 
     * a <code>Long.valueOf(data.toString())</code> is used to find out
     * the number</p>
     * 
     * @param data
     * @return formatted string result
     */
    @Transformer
    public static String formatSize(Object data) {
        if (null == data) throw new NullPointerException();
        Long bytes;
        if (data instanceof Number) {
            bytes = (Long)data;
        } else {
            bytes = Long.valueOf(data.toString());
        }
        if (bytes < 1024L) {
            return bytes + " B";
        }
        if (bytes < 1048576L) {
            return bytes / 1024L + "KB";
        }
        if (bytes < 1073741824L) {
            return bytes / 1048576L + "MB";
        }
        return bytes / 1073741824L + "GB";
    }
    
    /**
     * Transformer method. Format give data into currency
     * 
     * @param data
     * @return the currency
     * @see {@link #formatCurrency(RythmEngine, Object, String, java.util.Locale)}
     */
    @Transformer(requireEngine = true)
    public static String formatCurrency(Object data) {
        return formatCurrency(null, data, null, null);
    }

    /**
     * Transformer method. Format currency using specified currency code
     * 
     * @param data
     * @param currencyCode
     * @return the currency
     * @see {@link #formatCurrency(com.greenlaw110.rythm.RythmEngine, Object, String, java.util.Locale)}
     */
    @Transformer(requireEngine = true)
    public static String formatCurrency(Object data, String currencyCode) {
        return formatCurrency(null, data, currencyCode, null);
    }

    /**
     * See {@link #formatCurrency(com.greenlaw110.rythm.RythmEngine, Object, String, java.util.Locale)}
     * 
     * @param engine
     * @param data
     * @param currencyCode
     * @return the currency string
     */
    public static String formatCurrency(RythmEngine engine, Object data, String currencyCode) {
        return formatCurrency(engine, data, currencyCode, null);
    }

    /**
     * See {@link #formatCurrency(com.greenlaw110.rythm.RythmEngine, Object, String, java.util.Locale)}
     * 
     * @param data
     * @param currencyCode
     * @param locale
     * @return the currency string
     */
    public static String formatCurrency(Object data, String currencyCode, Locale locale) {
        return formatCurrency(null, data, currencyCode, locale);
    }

    /**
     * Format give data into currency using locale info from the engine specified
     * 
     * <p>The method accept any data type. When <code>null</code> is found then 
     * <code>NullPointerException</code> will be thrown out; if an <code>Number</code>
     * is passed in, it will be type cast to <code>Number</code>; otherwise 
     * a <code>Double.valueOf(data.toString())</code> is used to find out
     * the number</p>
     *
     * @param engine
     * @param data
     * @param currencyCode
     * @param locale
     * @return the currency
     */
    public static String formatCurrency(RythmEngine engine, Object data, String currencyCode, Locale locale) {
        if (null == data) throw new NullPointerException();
        Number number;
        if (data instanceof Number) {
            number = (Number)data;
        } else {
            number = Double.parseDouble(data.toString());
        }
        if (null == locale) locale = I18N.locale(engine);
        Currency currency = null == currencyCode ? Currency.getInstance(locale) : Currency.getInstance(currencyCode);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        numberFormat.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        String s = numberFormat.format(number);
        s = s.replace(currency.getCurrencyCode(), currency.getSymbol(locale));
        return s;
    }
    
    private static String getMessage(RythmEngine engine, ResourceBundle bundle, String key, Locale locale, Object ... args) {
        if (null == locale) locale = I18N.locale(engine);
        String s = key;
        try {
            s = bundle.getString(key);
        } catch (RuntimeException e) {
            //ignore it
        }
        int argLen = args.length;
        if (argLen > 0) {
            MessageFormat fmt = new MessageFormat(s, locale);
            Object[] argsResolved = new Object[argLen];
            for (int i = 0; i < argLen; ++i) {
                Object arg = args[i];
                if (arg instanceof String) {
                    arg = S.i18n(engine, (String)arg);
                }
                argsResolved[i] = arg;
            }
            return fmt.format(argsResolved);
        } else {
            return s;
        }
    }

    /**
     * Not an API. Don't not use this method
     * @param locale
     * @param engine
     * @param key
     * @return String
     */
    public static String i18n(Locale locale, RythmEngine engine, String key) {
        String cacheKey = CacheKey.i18nMsg(engine, key, false, locale);
        Object cached = engine.cached(cacheKey);
        if (S.notEmpty(cached)) return S.str(cached);
        ResourceBundle bundle;
        for (String msgSrc: RythmConfiguration.get().messageSources()) {
            bundle = I18N.bundle(engine, msgSrc, locale);
            if (null != bundle) {
                String data = getMessage(engine, bundle, key, locale, new Object[0]);
                if (null != data) {
                    engine.cache(cacheKey, data, -1);
                    return data;
                }
            }
        }
        return key;
    }
    
    /**
     * <p>Return i18n message of a given key and args, use the locale info from the engine specified. 
     * if <tt>null</tt> engine instance passed in then it will try to guess the current engine via
     * {@link com.greenlaw110.rythm.RythmEngine#get()}</p>
     * 
     * @param engine
     * @param key
     * @param args the format arguments. If the first argument is of type Locale then it will be used to specify
     * the locale of the processing, and the rest elements are used as format arguments
     * @return the i18n message
     */
    public static String i18n(RythmEngine engine, String key, Object... args) {
        boolean useFormat = args.length > 0;
        Locale locale = null;
        if (useFormat) {
            // check if the first arg is locale
            Object arg0 = args[0];
            if (arg0 instanceof Locale) {
                locale = (Locale)arg0;
                Object[] args0 = new Object[args.length - 1];
                System.arraycopy(args, 1, args0, 0, args.length - 1);
                args = args0;
                useFormat = args.length > 0;
            }
        }
        String cacheKey = null;
        if (null == engine) {
            engine = RythmEngine.get();
        }
        if (null != engine) {
            cacheKey = CacheKey.i18nMsg(engine, key, useFormat, locale);
            Object cached = engine.cached(cacheKey);
            if (S.notEmpty(cached)) return S.str(cached);
        }
        ResourceBundle bundle;
        for (String msgSrc: RythmConfiguration.get().messageSources()) {
            bundle = I18N.bundle(engine, msgSrc, locale);
            if (null != bundle) {
                String data = getMessage(engine, bundle, key, locale, args);
                if (null != data) {
                    if (null != engine) {
                        engine.cache(cacheKey, data, -1);
                    }
                    return data;
                }
            }
        }
        return key;
    }

    /**
     * Transformer method. Return i18n message of a given key and args.
     * 
     * @param key
     * @param args
     * @return the i18n message
     */
    @Transformer(requireEngine = true)
    public static String i18n(String key, Object... args) {
        return i18n(null, key, args);
    }
    
    @Transformer(requireEngine = true)
    public static String i18n(String key) {
        return i18n(null, key, new Object[0]);
    }


    /**
     * Generate random string.
     *
     * The generated string is safe to be used as filename
     * @param len
     * @return
     */
    public static String random(int len) {
        final char[] chars = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9', '$', '#', '^', '&', '_',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z',
                '~', '!', '@'};

        final int max = chars.length;
        Random r = new Random();
        StringBuffer sb = new StringBuffer(len);
        while(len-- > 0) {
            int i = r.nextInt(max);
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Return a random string with 8 chars
     * @return the string generated
     */
    public static String random() {
        return random(8);
    }
}
