package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.*;
import com.greenlaw110.rythm.internal.AutoToStringCodeBuilder;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.template.ToStringTemplateBase;
import com.greenlaw110.rythm.toString.ToStringOption;
import com.greenlaw110.rythm.toString.ToStringStyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ToString dialect is a kind of Rythm dialect, the difference is that
 * it preset the type of the only one render arg
 */
public class AutoToString extends ToString {

    public static final String ID = "rythm-autoToString";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new AutoToString();
    protected AutoToString() {}

    public AutoToStringData meta = null;

    public AutoToString(Class type, AutoToStringData data) {
        super(type);
        meta = data;
    }

    @Override
    public CodeBuilder createCodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine) {
        return new AutoToStringCodeBuilder(template, className, tagName, templateClass, engine, this);
    }

    public static String templateStr(Class<?> c, ToStringOption o, ToStringStyle s) {
        return com.greenlaw110.rythm.Rythm.render("{class: @c; toStringOption: @o; toStringStyle: @s}", null == c ? "" : c.getName(), o.toString(), s.toString());
    }

    public static class AutoToStringData {

        public AutoToStringData(Class<?> clazz, ToStringOption option, ToStringStyle style) {
            this.clazz = clazz;
            if (null != option) this.option = option;
            if (null != style) this.style = style;
        }

        public Class<?> clazz;
        public ToStringOption option = ToStringOption.defaultOption;
        public ToStringStyle style = ToStringStyle.DEFAULT_STYLE;

        private int hash = 0;

        @Override
        public String toString() {
            return templateStr(clazz, option, style);
        }

        @Override
        public int hashCode() {
            if (0 == hash) hash = ((31 + clazz.hashCode()) * 17 + option.hashCode()) * 17 + style.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof AutoToStringData) {
                AutoToStringData that = (AutoToStringData)obj;
                return that.clazz.equals(this.clazz) && that.option.equals(this.option) && that.style.equals(this.style);
            }
            return false;
        }

        public static AutoToStringData valueOf(String s) {
            return parseStr(s);
        }
    }

    private static final Pattern P = Pattern.compile("\\{class *: *([a-zA-Z_0-9\\.\\$]+) *; *toStringOption *: *(\\{.*?\\}) *; *toStringStyle *: *([a-zA-Z_0-9\\.\\$]+) *\\}");
    public static AutoToStringData parseStr(String s) {
        Matcher m = P.matcher(s);
        if (!m.matches()) throw new IllegalArgumentException("Unrecognized AutoToString template: " + s);
        String cs = m.group(1);
        String os = m.group(2);
        String ss = m.group(3);
        Class<?> c = null;
        try {
            c = com.greenlaw110.rythm.Rythm.engine().classLoader.loadClass(cs);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + cs);
        }

        ToStringOption o = ToStringOption.valueOf(os);
        ToStringStyle st = ToStringStyle.valueOf(ss);
        return new AutoToStringData(c, o, st);
    }

    public static void main(String[] args) {
        String s = templateStr(String.class, ToStringOption.defaultOption.setAppendTransient(true), ToStringStyle.DEFAULT_STYLE);
        System.out.println(s);
        AutoToStringData d = parseStr(s);
        System.out.println(d);
    }
}
