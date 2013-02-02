package com.greenlaw110.rythm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Define a type to wrap a JSON string
 */
public class JSONWrapper {
    private String s_;
    private JSON j_;

    /**
     * Construct a JSONWrapper with a JSON string. If the string is not valid JSON, then
     * a RuntimeException will thrown out
     *
     * @param str
     */
    public JSONWrapper(String str) {
        if (S.empty(str)) throw new IllegalArgumentException("empty json str");
        Object o;
        try {
            o = JSON.parse(str);
        } catch (com.alibaba.fastjson.JSONException e) {
            throw new RuntimeException("Invalid JSON string: " + str);
        }
        if (o instanceof JSON) {
            j_ = (JSON) o;
        } else {
            throw new RuntimeException("JSON string parse to unknown object type: " + o.getClass());
        }
    }

    /**
     * Return true if the underline JSON data is an array
     *
     * @return
     */
    public boolean isArray() {
        return j_ instanceof JSONArray;
    }

    /**
     * Return a List of object contained in the JSON array.
     * <p/>
     * <p></p>If the data is not a JSON array then a ClassCastException will
     * be thrown out</p>
     *
     * @return
     */
    public List<Object> getArray() {
        return (JSONArray) j_;
    }

    /**
     * Return a Map of String and object contained in the JSON object.
     * <p/>
     * <p></p>If the data is a JSON array then a ClassCastException will
     * be thrown out</p>
     *
     * @return
     */
    public Map<String, Object> getObject() {
        return (JSONObject) j_;
    }
    
    public static JSONWrapper wrap(String s) {
        return new JSONWrapper(s);
    }
}
