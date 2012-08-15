package com.greenlaw110.rythm.runtime;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.TemplateBuilder;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 23/01/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITag extends ITemplate {

    public static class Parameter {
        public String name;
        public Object value;
        public Parameter(String name, Object value) {
            this.name = "".equals(name) ? null : name;
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringBuilder("[").append(name).append("]:").append(value).toString();
        }
    }

    public static class ParameterList implements Iterable<Parameter> {
        private List<Parameter> lp = new ArrayList<Parameter>();
        public void add(String name, Object value) {
            lp.add(new Parameter(name, value));
        }

        public Object getByName(String name) {
            for (Parameter para: lp) {
                if (name.equals(para.name)) return para.value;
            }
            return null;
        }

        public <T> T getByName(String name, T defVal) {
            for (Parameter para: lp) {
                if (name.equals(para.name)) return (T) para.value;
            }
            return defVal;
        }

        public Object getDefault() {
            return getByPosition(0);
        }

        public Object getByPosition(int pos) {
            if (pos >= lp.size()) return null;
            return lp.get(pos).value;
        }

        @Override
        public Iterator<Parameter> iterator() {
            return lp.iterator();
        }

        public int size() {
            return lp.size();
        }

        public Parameter get(int i) {
            return lp.get(i);
        }

        public Map<String, Object> asMap() {
            Map<String, Object> m = new HashMap<String, Object>();
            for (Parameter p: lp) {
                if (p.name != null) m.put(p.name, p.value);
            }
            return m;
        }

        private String uuid = null;

        /**
         * Used to create unique key for cacheFor extension
         * @return
         */
        public String toUUID() {
            if (null == uuid) {
                StringBuilder sb = new StringBuilder();
                for (Parameter p: lp) {
                    sb.append(";").append(p.name).append("=").append(p.value);
                }
                String s = sb.toString();
                if (S.isEmpty(s)) s = "EMPTY_PARAMETER_LIST";
                uuid = UUID.nameUUIDFromBytes(s.getBytes()).toString();
            }
            return uuid;
        }
    }

    public abstract static class Body extends TemplateBuilder {
        protected ILogger logger = Logger.get(ITag.class);
        protected TemplateBase _context;
        protected Body self = this;
        public Body(TemplateBase context) {
            _context = context;
        }
        public StringBuilder getOut() {
            //return _context.getOut();
            return _out;
        }
        public ITemplate getContext() {
            return _context;
        }
        public void setOut(StringBuilder out) {
            //_context.setOut(out);
        }
        private void call(StringBuilder out) {
            _out = out;
            try {
                _call();
            } finally {
                _out = null;
            }
        }

        public final Body pe(Object o) {
            if (null == o) return this;
            if (o instanceof ITemplate.RawData) {
                return (Body)p(o);
            }
            ITemplate.Escape escape = _context.__ctx.currentEscape();
            return (Body)pe(o, escape);
        }

        protected abstract void setBodyArgByName(String name, Object val);
        protected abstract void setBodyArgByPos(int pos, Object val);
        public String render(Object ... vals) {
            StringBuilder sb = new StringBuilder();
            render(sb, vals);
            return sb.toString();
        }
        public void render(StringBuilder out, Object ... vals) {
            for (int i = vals.length - 1; i > -1; --i) {
                setBodyArgByPos(i, vals[i]);
            }
            call(out);
        }
        public void render(ParameterList parameterList, StringBuilder out) {
            if (null != parameterList) {
                for (int i = 0; i < parameterList.size(); ++i) {
                    Parameter p = parameterList.get(i);
                    if (!S.isEmpty(p.name)) {
                        setBodyArgByName(p.name, p.value);
                    } else {
                        setBodyArgByPos(i, p.value);
                    }
                }
            }
            call(out);
        }
        protected abstract void _call();
        public abstract void setProperty(String name, Object val);
        public abstract Object getProperty(String name);
//        @Override
//        public String toString() {
////            StringBuilder old = getOut();
////            setOut(new StringBuilder());
////            call();
////            String s = getOut().toString();
////            setOut(old);
////            return s;
//            StringBuilder sbNew = new StringBuilder();
//            StringBuilder sbOld = _context.getOut();
//            _context.setOut(sbNew);
//            this._out = sbNew;
//            _call();
//            String s = sbNew.toString();
//            _context.setOut(sbOld);
//            this._out = null;
//            return s;
////            _out.setLength(0);
////            call();
////            return _out.toString();
//        }
    }

    String getName();

    void call();
}
