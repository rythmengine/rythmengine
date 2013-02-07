package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public interface ITemplate extends Cloneable {

    void setOutputStream(OutputStream os);

    void setWriter(Writer writer);

    /**
     * Set renderArgs in name-value pair
     *
     * @param args
     */
    void setRenderArgs(Map<String, Object> args);

    /**
     * Set renderArgs in position
     *
     * @param args
     */
    void setRenderArgs(Object... args);

    /**
     * Set a render arg by name
     *
     * @param name
     * @param arg
     */
    void setRenderArg(String name, Object arg);

    Map<String, Object> getRenderArgs();

    <T> T getRenderArg(String name);

    /**
     * Set a render arg at position
     *
     * @param position
     * @param arg
     */
    void setRenderArg(int position, Object arg);

    /**
     * Set renderArgs with a JSON data 
     * 
     * @param jsonData
     */
    void setRenderArg(JSONWrapper jsonData);

    /**
     * Render the output
     *
     * @return
     */
    String render();

    void render(OutputStream os);

    void render(Writer w);

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

        public Stack<ILang> langStack = new Stack<ILang>();
        public Stack<Escape> escapeStack = new Stack<Escape>();

        public void init(TemplateBase templateBase, ILang lang) {
            if (null == lang) {
                TemplateClass tc = templateBase.getTemplateClass(true);
                lang = ILang.DefImpl.probeFileName(tc.name(), templateBase._engine().conf().defaultLang());
            }
            langStack.push(lang);
        }

        public ILang currentLang() {
            if (langStack.isEmpty()) {
                return null;
            } else {
                return langStack.peek();
            }
        }

        public void pushLang(ILang lang) {
            ILang cur = currentLang();
            if (null != cur) {
                lang.setParent(cur);
            }
            langStack.push(lang);
        }

        public ILang popLang() {
            ILang cur = langStack.pop();
            cur.setParent(null);
            return cur;
        }

        public Escape currentEscape() {
            if (!escapeStack.isEmpty()) {
                return escapeStack.peek();
            } else {
                return currentLang().escape();
            }
        }

        public void pushEscape(Escape escape) {
            escapeStack.push(escape);
        }

        public Escape popEscape() {
            return escapeStack.pop();
        }

        public Context() {
            langStack = new Stack<ILang>();
            escapeStack = new Stack<Escape>();
        }

        public Context(Context clone) {
            langStack = new Stack<ILang>();
            escapeStack = new Stack<Escape>();
            langStack.addAll(clone.langStack);
            escapeStack.addAll(clone.escapeStack);
        }
    }

    public static enum Escape {
        RAW,
        CSV {
            @Override
            protected RawData apply_(String s) {
                return S.escapeCsv(s);
            }
        },
        HTML {
            @Override
            protected RawData apply_(String s) {
                return S.escapeHtml(s);
            }
        },
        JS {
            @Override
            protected RawData apply_(String s) {
                return S.escapeJavaScript(s);
            }
        },
        JSON {
            @Override
            protected RawData apply_(String s) {
                return S.escapeJson(s);
            }
        },
        XML {
            @Override
            protected RawData apply_(String s) {
                return S.escapeXml(s);
            }
        };

        public RawData apply(Object o) {
            if (null == o) return RawData.NULL;
            String s = o.toString();
            return apply_(s);
        }

        protected RawData apply_(String s) {
            return new RawData(s);
        }

        private static String[] sa_ = null;

        public static String[] stringValues() {
            if (null == sa_) {
                Escape[] ea = values();
                String[] sa = new String[ea.length];
                for (int i = 0; i < ea.length; ++i) {
                    sa[i] = ea[i].toString();
                }
                Arrays.sort(sa);
                sa_ = sa;
            }
            return sa_.clone();
        }
    }

    public static class RawData implements Serializable {
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
