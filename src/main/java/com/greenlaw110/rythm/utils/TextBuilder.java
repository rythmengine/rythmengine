package com.greenlaw110.rythm.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines a chained source code builder. It's some how like a StringBuilder but it's chainable
 *
 * @author luog
 */
public class TextBuilder {

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
        String s = toString(o);
        if (null != _out) _out.append(s);
        else _caller.p(s);
    }

    protected String toString(Object o) {
        return null == o ? "" : o.toString();
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
}
