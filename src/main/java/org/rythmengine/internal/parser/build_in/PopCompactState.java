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
