package com.greenlaw110.rythm.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.TextBuilder;

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
    }
    
    public abstract static class Body {
        protected StringBuilder _out;
        protected ITemplate _context;
        public Body(ITemplate context) {
            _context = context;
            _out = ((TextBuilder)context).out();
        }
        public StringBuilder getOut() {
            return _out;
        }
        public ITemplate getContext() {
            return _context;
        }
        public void setOut(StringBuilder out) {
            _out = out;
        }
        public abstract void call();
        public abstract void setProperty(String name, Object val);
        public abstract Object getProperty(String name);
        @Override
        public String toString() {
            StringBuilder old = _out;
            _out = new StringBuilder();
            call();
            String s = _out.toString();
            _out = old;
            return s;
        }
    }

    String getName();
}
