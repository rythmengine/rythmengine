/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;

public abstract class CompactStateToken extends Token {
    public CompactStateToken(IContext context) {
        super("", context);
    }

    @Override
    protected final void output() {
        doBuild();
    }

    protected abstract void doBuild();
}
