package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.template.ITemplate;
import org.apache.commons.lang3.StringEscapeUtils;

import java.text.Normalizer;

public class S {
    public static boolean isEmpty(Object s) {
        return null == s || "".equals(s.toString().trim());
    }
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static final int IGNORECASE = 0x00001000;
    public static final int IGNORESPACE = 0x00002000;

    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

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

    public static String toString(Object o) {
        return null == o ? "" : o.toString();
    }

    public static ITemplate.RawData raw(Object s) {
        return new ITemplate.RawData(s);
    }

    public static ITemplate.RawData escape(Object s) {
        return escapeHtml(s);
    }

    public static ITemplate.RawData escape(Object s, Object escape) {
        if (isEmpty(s)) return ITemplate.RawData.NULL;
        if (isEmpty(escape)) return escape(s);
        String se = escape.toString();
        if ("json".equalsIgnoreCase(se)) return escapeJson(s);
        if ("xml".equalsIgnoreCase(se)) return escapeXml(s);
        if ("javascript".equalsIgnoreCase(se) || "js".equalsIgnoreCase(se)) return escapeJavaScript(s);
        if ("csv".equalsIgnoreCase(se)) return escapeCsv(s);
        if ("java".equalsIgnoreCase(se)) return escapeJava(s);
        if ("html".equalsIgnoreCase(se)) return escapeHtml(s);
        if ("raw".equalsIgnoreCase(se)) return raw(s);
        throw new IllegalArgumentException("Unknown escape scheme: " + se);
    }

    public static ITemplate.RawData escapeHtml(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeHtml4(s.toString()));
    }

    public static ITemplate.RawData escapeCsv(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeCsv(s.toString()));
    }

    public static ITemplate.RawData escapeJson(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        String s0 = s.toString();
        s0 = s0.replaceAll("[\n\r]+", "\\\\\\n").replaceAll("[ \t]+", " ").replaceAll("\"", "\\\\\"");
        return new ITemplate.RawData(s0);
    }



    public static ITemplate.RawData escapeJava(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeJava(s.toString()));
    }

    public static ITemplate.RawData escapeJavaScript(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeEcmaScript(s.toString()));
    }

    public static ITemplate.RawData escapeXml(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeXml(s.toString()));
    }

    public static final String stripBrace(String s) {
        if (null == s) return "";
        s = s.trim();
        if (s.startsWith("(")) s = s.substring(1);
        if (s.endsWith(")")) s = s.substring(0, s.length() - 1);
        return s;
    }

    public static final String stripQuotation(String s) {
        if (null == s) return "";
        s = s.trim();
        if (s.startsWith("\"") || s.startsWith("'")) s = s.substring(1);
        if (s.endsWith("\"") || s.endsWith("'")) s = s.substring(0, s.length() - 1);
        return s;
    }

    public static final String stripBraceAndQuotation(String s) {
        s = stripBrace(s);
        s = stripQuotation(s);
        return s;
    }

    public static String shrinkSpace(Object s) {
        if (null == s) return  "";
        return s.toString().replaceAll("[\r\n]+", "\n").replaceAll("\\s+", "\\s");
    }

    public static String pad(Object obj, Integer size) {
        String str = null == obj ? "" : obj.toString();
        int t = size - str.length();
        for (int i = 0; i < t; i++) {
            str += "&nbsp;";
        }
        return str;
    }

    public static String capitalizeWords(Object obj) {
        if (null == obj) return "";
        String source = obj.toString();
        char prevc = ' '; // first char of source is capitalized
        StringBuffer sb = new StringBuffer();
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

    public static String noAccents(Object obj) {
        if (null == obj) return "";
        String string = obj.toString();
        return Normalizer.normalize(string, Normalizer.Form.NFKC).replaceAll("[àáâãäåāąă]", "a").replaceAll("[çćčĉċ]", "c").replaceAll("[ďđð]", "d").replaceAll("[èéêëēęěĕė]", "e").replaceAll("[ƒſ]", "f").replaceAll("[ĝğġģ]", "g").replaceAll("[ĥħ]", "h").replaceAll("[ìíîïīĩĭįı]", "i").replaceAll("[ĳĵ]", "j").replaceAll("[ķĸ]", "k").replaceAll("[łľĺļŀ]", "l").replaceAll("[ñńňņŉŋ]", "n").replaceAll("[òóôõöøōőŏœ]", "o").replaceAll("[Þþ]", "p").replaceAll("[ŕřŗ]", "r").replaceAll("[śšşŝș]", "s").replaceAll("[ťţŧț]", "t").replaceAll("[ùúûüūůűŭũų]", "u").replaceAll("[ŵ]", "w").replaceAll("[ýÿŷ]", "y").replaceAll("[žżź]", "z").replaceAll("[æ]", "ae").replaceAll("[ÀÁÂÃÄÅĀĄĂ]", "A").replaceAll("[ÇĆČĈĊ]", "C").replaceAll("[ĎĐÐ]", "D").replaceAll("[ÈÉÊËĒĘĚĔĖ]", "E").replaceAll("[ĜĞĠĢ]", "G").replaceAll("[ĤĦ]", "H").replaceAll("[ÌÍÎÏĪĨĬĮİ]", "I").replaceAll("[Ĵ]", "J").replaceAll("[Ķ]", "K").replaceAll("[ŁĽĹĻĿ]", "L").replaceAll("[ÑŃŇŅŊ]", "N").replaceAll("[ÒÓÔÕÖØŌŐŎ]", "O").replaceAll("[ŔŘŖ]", "R").replaceAll("[ŚŠŞŜȘ]", "S").replaceAll("[ÙÚÛÜŪŮŰŬŨŲ]", "U").replaceAll("[Ŵ]", "W").replaceAll("[ÝŶŸ]", "Y").replaceAll("[ŹŽŻ]", "Z").replaceAll("[ß]", "ss");
    }

    public static String slugify(Object obj) {
        return slugify(obj, Boolean.TRUE);
    }

    public static String slugify(Object obj, Boolean lowercase) {
        if (null == obj) return "";
        String string = obj.toString();
        string = noAccents(string);
        // Apostrophes.
        string = string.replaceAll("([a-z])'s([^a-z])", "$1s$2");
        string = string.replaceAll("[^\\w]", "-").replaceAll("-{2,}", "-");
        // Get rid of any - at the start and end.
        string.replaceAll("-+$", "").replaceAll("^-+", "");

        return (lowercase ? string.toLowerCase() : string);
    }

    public static String capFirst(Object o) {
        if (null == o) return "";
        String string = o.toString().toLowerCase();
        if (string.length() == 0) {
            return string;
        }
        return ("" + string.charAt(0)).toUpperCase() + string.substring(1);
    }

    public static String capAll(Object o) {
        if (null == o) return "";
        String string = o.toString();
        return capitalizeWords(string);
    }

    public static String camelCase(Object obj) {
        if (null == obj) return "";
        String string = obj.toString();
        string = noAccents(string);
        string = string.replaceAll("[^\\w ]", "");
        StringBuilder result = new StringBuilder(string.length());
        for (String part : string.split(" ")) {
            result.append(capFirst(part));
        }
        return result.toString();
    }


    public static void main(String[] args) {
        String s = "var x = 0; ybc\n\nvar y = 1";
        System.out.println(S.escapeJson(s));
    }

}
