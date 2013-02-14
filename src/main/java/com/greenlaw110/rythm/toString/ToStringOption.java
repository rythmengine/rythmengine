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
package com.greenlaw110.rythm.toString;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.utils.S;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the options can be set when output an object into string
 */
public class ToStringOption {

    /**
     * Construct an new default option
     *
     * @see #DEFAULT_OPTION
     */
    public ToStringOption() {
    }

    /**
     * Construct an option with specified appendStatic and appendTransient switch.
     *
     * @param appendStatic    when set to true the static fields will be output
     * @param appendTransient when set to true the transient fields will be output
     */
    public ToStringOption(boolean appendStatic, boolean appendTransient) {
        this.appendStatic = appendStatic;
        this.appendTransient = appendTransient;
    }

    /**
     * Construct an option with static, transient output switch and upToClass
     *
     * @param appendStatic    when set to true the static fields will be output
     * @param appendTransient when set to true the trasient fields will be output
     * @param upToClass       when set then the fields up to this parent class will be output,
     *                        otherwise it will be up to {@link java.lang.Object} root
     */
    public ToStringOption(boolean appendStatic, boolean appendTransient, Class<?> upToClass) {
        this.appendStatic = appendStatic;
        this.appendTransient = appendTransient;
        this.upToClass = upToClass;
    }

    /**
     * The default option instance, with
     * <ul>
     * <li>{@link #upToClass}set to <code>null</code>, meaning up to {@link java.lang.Object} root</li>
     * <li>{@link #appendTransient} set to <code>false</code>, will not output transient fields</li>
     * <li>{@link #appendStatic} set to <code>false</code>, will not output static fields</li>
     * </ul>
     */
    public static final ToStringOption DEFAULT_OPTION = new ToStringOption();

    /**
     * Specify the up most parent class whose declared fields will be output.
     * Default value is <code>null</code>, meaning all parent class declared
     * fields will be output
     */
    public Class<?> upToClass = null;

    /**
     * Whether output transient fields
     */
    public boolean appendTransient = false;

    /**
     * Whether output static fields
     */
    public boolean appendStatic = false;

    /**
     * Return a <code>ToStringOption</code> instance with {@link #appendTransient} option set.
     * if the current instance is not {@link #DEFAULT_OPTION default instance} then set
     * on the current instance and return the current instance. Otherwise, clone the default
     * instance and set on the clone and return the clone
     *
     * @param appendTransient
     * @return
     */
    public ToStringOption setAppendTransient(boolean appendTransient) {
        ToStringOption op = this;
        if (this == DEFAULT_OPTION) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.appendTransient = appendTransient;
        return op;
    }

    /**
     * Return a <code>ToStringOption</code> instance with {@link #appendStatic} option set.
     * if the current instance is not {@link #DEFAULT_OPTION default instance} then set
     * on the current instance and return the current instance. Otherwise, clone the default
     * instance and set on the clone and return the clone
     *
     * @param appendStatic
     * @return
     */
    public ToStringOption setAppendStatic(boolean appendStatic) {
        ToStringOption op = this;
        if (this == DEFAULT_OPTION) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.appendStatic = appendStatic;
        return op;
    }

    /**
     * Return a <code>ToStringOption</code> instance with {@link #upToClass} option set.
     * if the current instance is not {@link #DEFAULT_OPTION default instance} then set
     * on the current instance and return the current instance. Otherwise, clone the default
     * instance and set on the clone and return the clone
     *
     * @param c
     * @return
     */
    public ToStringOption setUpToClass(Class<?> c) {
        ToStringOption op = this;
        if (this == DEFAULT_OPTION) {
            op = new ToStringOption(this.appendStatic, this.appendTransient);
        }
        op.upToClass = c;
        return op;
    }

    /**
     * Print this to string option instance using the following logic:
     * <p/>
     * <pre><code>
     * return Rythm.toString(
     *    "{appendStatic: @_.appendStatic; " +
     *    "appendTransient: @_.appendTransient; " +
     *    upToClass: @_.upToClass?.getName()}", this);
     * </code></pre>
     *
     * @return
     */
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
            ToStringOption that = (ToStringOption) obj;
            return that.appendStatic == this.appendStatic && that.appendTransient == this.appendTransient;
        }
        return false;
    }

    /**
     * Construct a <code>ToStringOption</code> instance out from a string. The format
     * of the String should be the same as the format output of {@link #toString()}
     * method
     *
     * @param s
     * @return
     */
    public static ToStringOption valueOf(String s) {
        Pattern p = Pattern.compile("\\{appendStatic *\\: *(true|false) *; *appendTransient *\\: *(true|false) *; *upToClass *: *(.*)\\}");
        Matcher m = p.matcher(s);
        if (!m.matches()) throw new IllegalArgumentException("Unknown ToStringOption: " + s);
        boolean appendStatic = Boolean.valueOf(m.group(1));
        boolean appendTransient = Boolean.valueOf(m.group(2));
        String upToClassStr = m.group(3);
        Class<?> upToClass = null;
        if (S.isEmpty(upToClassStr)) upToClass = null;
        else try {
            upToClass = Class.forName(upToClassStr);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find upToClass: " + upToClassStr);
        }
        return new ToStringOption(appendStatic, appendTransient, upToClass);
    }

    public static void main(String[] args) {
        ToStringOption o = ToStringOption.DEFAULT_OPTION;
        System.out.println(o.toString());
        System.out.println(ToStringOption.valueOf(o.setAppendStatic(true).setUpToClass(String.class).toString()));
    }
}
