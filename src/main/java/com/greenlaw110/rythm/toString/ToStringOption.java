package com.greenlaw110.rythm.toString;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.utils.S;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 14/07/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToStringOption {
    public ToStringOption() {}

    public ToStringOption(boolean appendStatic, boolean appendTransient) {
        this.appendStatic = appendStatic;
        this.appendTransient = appendTransient;
    }

    public ToStringOption(boolean appendStatic, boolean appendTransient, Class<?> upToClass) {
        this.appendStatic = appendStatic;
        this.appendTransient = appendTransient;
        this.upToClass = upToClass;
    }

    public static ToStringOption defaultOption = new ToStringOption();

    public Class<?> upToClass = null;
    public boolean appendTransient = false;
    public boolean appendStatic = false;
    public ToStringOption setAppendTransient(boolean appendTransient) {
        ToStringOption op = this;
        if (this == defaultOption) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.appendTransient = appendTransient;
        return op;
    }
    public ToStringOption setAppendStatic(boolean appendStatic) {
        ToStringOption op = this;
        if (this == defaultOption) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.appendStatic = appendStatic;
        return op;
    }
    public ToStringOption setUpToClass(Class<?> c) {
        ToStringOption op = this;
        if (this == defaultOption) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.upToClass = c;
        return op;
    }

    @Override
    public String toString() {
        return Rythm.toString("{appendStatic: @_.appendStatic; appendTransient: @_.appendTransient; upToClass: @_.upToClass?.getName()}", this);
    }

    @Override
    public int hashCode() {
        return (31 + Boolean.valueOf(appendTransient).hashCode()) * 17 + Boolean.valueOf(appendStatic).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ToStringOption) {
            ToStringOption that = (ToStringOption)obj;
            return that.appendStatic == this.appendStatic && that.appendTransient == this.appendTransient;
        }
        return false;
    }

    public static ToStringOption valueOf(String s) {
        Pattern p = Pattern.compile("\\{appendStatic *\\: *(true|false) *; *appendTransient *\\: *(true|false) *; *upToClass *: *(.*)\\}");
        Matcher m = p.matcher(s);
        if (!m.matches()) throw new IllegalArgumentException("Unknown ToStringOption: " + s);
        boolean appendStatic = Boolean.valueOf(m.group(1));
        boolean appendTransient = Boolean.valueOf(m.group(2));
        String upToClassStr = m.group(3);
        Class<?> upToClass = null;
        if (S.isEmpty(upToClassStr)) upToClass = null;
        else try {upToClass = Class.forName(upToClassStr);} catch(ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find upToClass: " + upToClassStr);
        }
        return new ToStringOption(appendStatic, appendTransient, upToClass);
    }

    public static void main(String[] args) {
        ToStringOption o = ToStringOption.defaultOption;
        System.out.println(o.toString());
        System.out.println(ToStringOption.valueOf(o.setAppendStatic(true).setUpToClass(String.class).toString()));
    }
}
