/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.internal.dialect.AutoToString;
import org.rythmengine.internal.parser.toString.AppendEndToken;
import org.rythmengine.internal.parser.toString.AppendFieldToken;
import org.rythmengine.internal.parser.toString.AppendStartToken;
import org.rythmengine.toString.ToStringOption;
import org.rythmengine.utils.S;
import org.rythmengine.utils.TextBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/07/12
 * Time: 8:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class AutoToStringCodeBuilder extends CodeBuilder {
    public AutoToStringCodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine, IDialect dialect) {
        super(template, className, tagName, templateClass, engine, dialect);
        // skip annotations
        skipAnnotations.addAll(Arrays.asList("org.codehaus.jackson.annotate.JsonIgnore org.rythmengine.toString.NoExpose ".split(" +")));
        transientAnnotations.addAll(Arrays.asList("javax.persistence.Transient com.google.code.morphia.annotations.Transient".split(" +")));
        if (dialect instanceof AutoToString) {
            meta = ((AutoToString) dialect).meta;
        } else {
            throw new IllegalArgumentException("AutoToString expected, found: " + dialect.getClass());
        }
    }

    @Override
    protected String extended() {
        return ToStringTemplateBase.class.getName();
    }

    @Override
    protected void pSetup() {
        ptn("protected void __setup() {");
        if (logTime) {
            p2tn("__logTime = true;");
        }
        p2t("__style = ").p(meta.style.toCode()).p(";").pn();
        for (Map.Entry<String, RenderArgDeclaration> entry : renderArgs.entrySet()) {
            RenderArgDeclaration arg = entry.getValue();
            p2t("if (").p(entry.getKey()).p(" == null) {");
            p(entry.getKey()).p("=(").p(arg.objectType()).p(")__get(\"").p(entry.getKey()).p("\");}\n");
        }
        ptn("}");
    }

    @Override
    public TextBuilder build() {
        parse();
        pImports();
        pClassOpen();
        pRenderArgs();
        pSetup();
        pBuild();
        pClassClose();
        return this;
    }

    private AutoToString.AutoToStringData meta = null;

    // <fieldName, expression>
    private Map<String, String> expressions = new HashMap<String, String>();
    private Set<String> transients = new HashSet<String>();
    private Set<String> skips = new HashSet<String>();

    private Set<String> skipAnnotations = new HashSet<String>();
    private Set<String> transientAnnotations = new HashSet<String>();

    private boolean shouldSkip(Annotation[] aa) {
        for (Annotation a : aa) {
            String an = a.annotationType().getName();
            if (skipAnnotations.contains(an)) return true;
            if (transientAnnotations.contains(an) && !meta.option.appendTransient) return true;
        }
        return false;
    }

    private boolean shouldSkip(Field f) {
        String fn = f.getName();
        Annotation[] aa = f.getAnnotations();
        if (shouldSkip(aa)) {
            skips.add(fn);
            return true;
        }

        int mod = f.getModifiers();
        ToStringOption op = meta.option;
        if (!op.appendTransient && Modifier.isTransient(mod) || !op.appendStatic && Modifier.isStatic(mod)) {
            skips.add(fn);
            return true;
        }

        return !Modifier.isPublic(mod);
    }

    private boolean shouldSkip(Method m, String fn) {
        Annotation[] aa = m.getAnnotations();
        if (shouldSkip(aa)) {
            skips.add(fn);
            return true;
        }

        int mod = m.getModifiers();
        ToStringOption op = meta.option;
        if (!op.appendTransient && Modifier.isTransient(mod) || !op.appendStatic && Modifier.isStatic(mod)) {
            skips.add(fn);
            return true;
        }

        if (!Modifier.isPublic(mod)) return true;


        return false;
    }

    private List<String> tokenList = new ArrayList<String>();

    private void appendFieldsIn(Class<?> c) {
        Field[] fa = c.getDeclaredFields();
        for (Field f : fa) {
            String fn = f.getName();
            if (!tokenList.contains(fn)) tokenList.add(fn);
            if (shouldSkip(f)) {
                continue;
            }
            expressions.put(fn, fn);
        }
    }

    private void appendMethodsIn(Class<?> c) {
        Method[] ma = c.getDeclaredMethods();
        for (Method m : ma) {
            String mn = m.getName();
            if ("getClass".equals(mn)) continue;
            String fn = null;
            if (mn.startsWith("get")) {
                fn = mn.replaceFirst("get", "");
            } else if (mn.startsWith("is")) {
                fn = mn.replaceFirst("is", "");
            }
            if (S.isEmpty(fn)) continue;
            fn = S.lowerFirst(fn);

            if (shouldSkip(m, fn)) {
                continue;
            }

            mn = mn + "()";
            if (!tokenList.contains(fn)) tokenList.add(fn);
            expressions.put(fn, mn);
        }
    }

    private void appendIn(Class<?> c) {
        if (engine().conf().playFramework()) {
            appendMethodsIn(c);
            appendFieldsIn(c);
        } else {
            appendFieldsIn(c);
            appendMethodsIn(c);
        }
    }

    private void parse() {
        Class<?> c = meta.clazz;
        ToStringOption o = meta.option;
        addBuilder(new AppendStartToken(this));
        this.appendIn(c);
        while (c.getSuperclass() != null && c != o.upToClass) {
            c = c.getSuperclass();
            this.appendIn(c);
        }
        for (String fn : skips) expressions.remove(fn);
        for (String fn : tokenList) {
            if (!expressions.containsKey(fn)) continue;
            String exp = expressions.get(fn);

            addBuilder(new AppendFieldToken(fn, exp, this));
        }
        addBuilder(new AppendEndToken(this));
        this.addRenderArgs(-1, meta.clazz.getName().replace('$', '.'), "_");
    }

}
