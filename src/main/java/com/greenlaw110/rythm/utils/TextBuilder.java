package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.template.ITemplate;

import java.util.*;

/**
 * This class defines a chained source code builder. It's some how like a StringBuilder but it's chainable
 *
 * @author luog
 */
public class TextBuilder implements Cloneable {

    public static class TextBuilderList extends TextBuilder {
        List<TextBuilder> builders = new ArrayList<TextBuilder>();
        TextBuilderList(TextBuilder... builders) {
            this.builders.addAll(Arrays.asList(builders));
        }

        @Override
        public TextBuilder build() {
            for (TextBuilder builder: builders) {
                builder.build();
            }
            return this;
        }
    }

    protected StringBuilder _out;

    public StringBuilder out() {
        return null == _out ? _caller.out() : _out;
    }

    protected TextBuilder _caller;

    public StringBuilder getOut() {
        return out();
    }

    public void setOut(StringBuilder out) {
        if (null != _caller) ((TextBuilder)_caller).setOut(out);
        else _out = out;
    }

    public void setSelfOut(StringBuilder out) {
        _out = out;
    }

    public StringBuilder getSelfOut() {
        return _out;
    }

    /**
     * Construct a root text builder
     */
    public TextBuilder() {
        _out = new StringBuilder();
        _caller = null;
    }

    /**
     * Construct a chained text builder
     * @param caller
     */
    public TextBuilder(TextBuilder caller) {
        this._caller = caller;
        _out = (null == caller) ? new StringBuilder() : null;
    }

    private void p_(Object o) {
        if (null != _out) _out.append(o.toString());
        else _caller.p(o);
    }

    public final TextBuilder p(Object o) {
        if (null != o) p_(o);
        return this;
    }

    public final TextBuilder p(char c) {
        if (null != _out) _out.append(c);
        else _caller.p(c);
        return this;
    }

    public final TextBuilder p(int i) {
        if (null != _out) _out.append(i);
        else _caller.p(i);
        return this;
    }

    public final TextBuilder p(long l) {
        if (null != _out) _out.append(l);
        else _caller.p(l);
        return this;
    }

    public final TextBuilder p(float f) {
        if (null != _out) _out.append(f);
        else _caller.p(f);
        return this;
    }

    public final TextBuilder p(double d) {
        if (null != _out) _out.append(d);
        else _caller.p(d);
        return this;
    }

    public final TextBuilder p(boolean b) {
        if (null != _out) _out.append(b);
        else _caller.p(b);
        return this;
    }

    /**
     * Append to object specified to the string buffer and then append
     * an new line character
     *
     * @param o
     * @return
     */
    public final TextBuilder pn(Object o) {
        if (null != o) p_(o);
        p_('\n');
        return this;
    }

    public final TextBuilder pn() {
        p_('\n');
        return this;
    }

    /**
     * Append an new line character and then append the object specified
     * to the string buffer
     *
     * @param o
     * @return
     */
    public final TextBuilder np(Object o) {
        p_('\n');
        if (null != o) p_(o);
        return this;
    }

    public TextBuilder pt(Object o) {
        p("\t").p(o);
        return this;
    }

    public TextBuilder ptn(Object o) {
        p("\t").p(o).pn();
        return this;
    }

    public TextBuilder p2t(Object o) {
        p("\t\t").p(o);
        return this;
    }

    public TextBuilder p2tn(Object o) {
        p("\t\t").p(o).pn();
        return this;
    }

    public TextBuilder p3t(Object o) {
        p("\t\t\t").p(o);
        return this;
    }

    public TextBuilder p3tn(Object o) {
        p("\t\t\t").p(o).pn();
        return this;
    }

    public TextBuilder p4t(Object o) {
        p("\t\t\t\t").p(o);
        return this;
    }

    public TextBuilder p4tn(Object o) {
        p("\t\t\t\t").p(o).pn();
        return this;
    }

    /**
     * Sub class should implement this method to append the generated
     * source code to the buffer
     *
     * @return
     */
    public TextBuilder build() {
        return this;
    }

    @Override
    public String toString() {
        return null != _out ? _out.toString() : _caller.toString();
    }

    public TextBuilder clone(TextBuilder caller) {
        try {
            TextBuilder tb = (TextBuilder)super.clone();
            tb._caller = caller;
            return tb;
        } catch (CloneNotSupportedException e) {
            throw new FastRuntimeException("Unexpected");
        }
    }
}
