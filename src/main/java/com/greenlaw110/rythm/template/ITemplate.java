package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public interface ITemplate extends Cloneable {
    /**
     * Set renderArgs in name-value pair
     * @param args
     */
    void setRenderArgs(Map<String, Object> args);
    /**
     * Set renderArgs in position
     * @param args
     */
    void setRenderArgs(Object... args);

    /**
     * Set a render arg by name
     * @param name
     * @param arg
     */
    void setRenderArg(String name, Object arg);

    Map<String, Object> getRenderArgs();

    Object getRenderArg(String name);

    /**
     * Set a render arg at position
     * @param position
     * @param arg
     */
    void setRenderArg(int position, Object arg);

    /**
     * Render the output
     * @return
     */
    String render();

    /**
     * Must be called before real render() happened.
     * Also if the template extends a parent template, then
     * the parent template's init() must be called before this template's init()
     */
    void init();

    StringBuilder getOut();

    void setOut(StringBuilder sb);

    /**
     * Get a copy of this template instance and pass in the engine and caller
     *
     * @param engine the rythm engine
     * @param caller the caller template
     * @return a cloned instance of this template class
     */
    ITemplate cloneMe(RythmEngine engine, ITemplate caller);

    public static class Context {
        public Stack<Escape> escapeStack;
        public void init(TemplateBase templateBase) {
            TemplateClass tc = templateBase.getTemplateClass(true);
            if (tc.name().contains("html" + TemplateClass.CN_SUFFIX)) {
                escapeStack.push(Escape.HTML);
            } else {
                escapeStack.push(Escape.RAW);
            }
        }
        public Escape currentEscape() {
            return escapeStack.peek();
        }
        public void pushEscape(Escape escape) {
            escapeStack.push(escape);
        }
        public Escape popEscape() {
            return escapeStack.pop();
        }

        public Context() {
            escapeStack = new Stack<Escape>();
        }
    }

    public static enum Escape {
        RAW,
        CSV,
        HTML,
        JS,
        JAVA,
        XML;
        private static String[] sa_ = null;
        public static String[] stringValues() {
            if (null == sa_) {
                Escape[] ea = values();
                String[] sa = new String[ea.length];
                for (int i = 0; i < ea.length; ++i){
                    sa[i] = ea[i].toString();
                }
                Arrays.sort(sa);
                sa_ = sa;
            }
            return sa_.clone();
        }
    }

    public static class RawData {
        public String data;
        public RawData(Object val) {
            if (val == null) {
                data = "";
            } else {
                data = val.toString();
            }
        }
        @Override
        public String toString() {
            return data;
        }
        public static final RawData NULL = new RawData(null);
    }
}
