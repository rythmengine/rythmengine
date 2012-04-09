package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.template.ITemplate;
import org.apache.commons.lang3.StringEscapeUtils;

public class S {
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

    public static ITemplate.RawData raw(Object s) {
        return new ITemplate.RawData(s);
    }

    public static ITemplate.RawData escape(Object s) {
        return escapeHtml(s);
    }

    public static ITemplate.RawData escapeHtml(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeHtml4(s.toString()));
    }

    public static ITemplate.RawData escapeCsv(Object s) {
        if (null == s) return ITemplate.RawData.NULL;
        return new ITemplate.RawData(StringEscapeUtils.escapeCsv(s.toString()));
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

    public static String shrinkSpace(Object s) {
        if (null == s) return  "";
        return s.toString().replaceAll("[\r\n]+", "\n").replaceAll("\\s+", "\\s");
    }

    public static String pad(String str, Integer size) {
        int t = size - str.length();
        for (int i = 0; i < t; i++) {
            str += "&nbsp;";
        }
        return str;
    }

    public static String capitalizeWords(String source) {
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

    public static void main(String[] args) {
        System.out.println(S.escape("<h1>Hello</h1>"));
    }

}
