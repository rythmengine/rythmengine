/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IDirective;
import org.rythmengine.internal.Token;

public class Directive extends Token implements IDirective {

    public Directive() {
        super(null, (IContext) null);
    }

    public Directive(String s, IContext context) {
        super(s, context);
    }

    protected void output() {
    }

    public void call() {
    }

}
