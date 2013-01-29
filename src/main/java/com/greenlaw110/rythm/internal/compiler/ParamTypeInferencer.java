package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Facilitate {@link com.greenlaw110.rythm.RythmEngine} to infer param types
 */
public class ParamTypeInferencer {

    private static final ThreadLocal<Map<Object, String>> typeMap = new ThreadLocal<Map<Object, String>>() {
        @Override
        protected Map<Object, String> initialValue() {
            return new HashMap<Object, String>();
        }
    };
    
    private RythmEngine engine;
    
    public ParamTypeInferencer(RythmEngine engine) {
        this.engine = engine;
    }
    
    public void registerParams(Object ... args) {
        if (!engine.enableTypeInference()) return;
        
        if (args.length == 0) return;
        
        Map<Object, String> tMap = typeMap.get();
        if (args.length == 1 && args[0] instanceof Map) {
            Map<String, Object> params = (Map)args[0];
            for (String name: params.keySet()) {
                Object val = params.get(name);
                String clsName;
                if (null == val) {
                    clsName = "Object";
                } else {
                    clsName = val.getClass().getName();
                }
                tMap.put(name, clsName);
            }
        } else {
            for (int i = 0; i < args.length; ++i) {
                Object name = Integer.valueOf(i);
                String clsName;
                Object val = args[i];
                if (null == val) {
                    clsName = "Object";
                } else {
                    clsName = val.getClass().getName();
                }
                tMap.put(name, clsName);
            }
        }
    }
    
    public static Map<Object, String> getTypeMap() {
        return typeMap.get();
    }

}
