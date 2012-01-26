package com.greenlaw110.rythm.runtime;

import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.TextBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 23/01/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITag extends ITemplate {
    String getName();
    
    public static class Parameter {
        String name;
        Object value;
        public Parameter(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
    
    public static class ParameterList {
        private List<Parameter> lp = new ArrayList<Parameter>();
        public void add(String name, Object value) {
            lp.add(new Parameter(name, value));
        }
    }

    public static class Body extends TextBuilder {

    }
}
