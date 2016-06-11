/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.toString;

import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.utils.TextBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/07/12
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppendFieldToken extends CodeToken {

    private static String getCode(String fieldName, String expression) {
        return String.format("__style.append(buffer(), \"%s\", _.%s, null);", fieldName, expression);
    }

    public AppendFieldToken(String fieldName, String expression, TextBuilder caller) {
        super(getCode(fieldName, expression), caller);
    }
}
