package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Facilitate {@link com.greenlaw110.rythm.RythmEngine} to infer param types
 */
public class ParamTypeInferencer {

    public static String typeTransform(String type) {
        if (type.contains("boolean")) return type.replace("boolean", "Boolean");
        else if (type.contains("int")) return type.replace("int", "Integer");
        else if (type.contains("float")) return type.replace("float", "Float");
        else if (type.contains("double")) return type.replace("double", "Double");
        else if (type.contains("char")) return type.replace("char", "Character");
        else if (type.contains("long")) return type.replace("long", "Long");
        else if (type.contains("byte")) return type.replace("byte", "Byte");
        else return type;
    }
    
    private static final ThreadLocal<Map<String, String>> typeMap = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };
    
    public static void registerParams(RythmEngine engine, Object... args) {
        if (!engine.enableTypeInference()) return;
        
        if (args.length == 0) return;
        
        Map<String, String> tMap = typeMap.get();
        if (args.length == 1 && args[0] instanceof Map) {
            Map<String, Object> params = (Map)args[0];
            for (String name: params.keySet()) {
                Object val = params.get(name);
                String clsName;
                if (null == val) {
                    clsName = "Object";
                } else {
                    Class c = val.getClass();
                    clsName = c.getName();
                    if (c.isArray()) {
                        Class cc = c.getComponentType();
                        while(cc.isArray()) cc = cc.getComponentType();
                        String cName = cc.getName();
                        String s = clsName;
                        // now count the number of '[' to see how many dimension this array has
                        int d = 0;
                        for (int i = 0; i < s.length(); i++) {
                            if (s.charAt(i) == '['){
                                d++;
                            } else {
                                break;
                            }
                        }
                        StringBuilder sb = new StringBuilder(cName);
                        for (int i = 0; i < d; ++i) {
                            sb.append("[]");
                        }
                        clsName = sb.toString();
                    }
                }
                tMap.put(name, clsName);
            }
        } else {
            // Type inference support passing params as Map only
        }
    }
    
    public static Map<String, String> getTypeMap() {
        return typeMap.get();
    }

}
