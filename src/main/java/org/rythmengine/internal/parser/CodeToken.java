/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;
import org.rythmengine.utils.TextBuilder;


public class CodeToken extends Token {

    protected CodeToken(String s, TextBuilder caller) {
        super(s, caller);
    }

    public CodeToken(String s, IContext context) {
        super(s, context);
    }

    @Override
    public void output() {
        p(s);
        pline();
    }
}
