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
public class AppendStartToken extends CodeToken {

    private static String getCode() {
        return "__style.appendStart(buffer(), _);";
    }

    public AppendStartToken(TextBuilder caller) {
        super(getCode(), caller);
    }
}
