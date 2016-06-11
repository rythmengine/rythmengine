/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;

public class PushCompactState extends CompactStateToken {
    private boolean compact;
    public PushCompactState(boolean compact, IContext context) {
        super(context);
        this.compact = compact;
    }

    @Override
    protected void doBuild() {
        ctx.pushCompact(compact);
    }

}
