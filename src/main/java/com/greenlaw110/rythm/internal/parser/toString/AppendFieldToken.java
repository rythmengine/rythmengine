package com.greenlaw110.rythm.internal.parser.toString;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/07/12
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppendFieldToken extends CodeToken {

    private static String getCode(String fieldName, String expression) {
        return Rythm.render("__style.append(out(), \"@fieldName\", _.@expression, null);", fieldName, expression);
    }

    public AppendFieldToken(String fieldName, String expression, TextBuilder caller) {
        super(getCode(fieldName, expression), caller);
    }
}
