/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;

public class PopCompactState extends CompactStateToken {

    public PopCompactState(IContext context) {
        super(context);
    }

    @Override
    protected void doBuild() {
        ctx.popCompact();
    }

}
