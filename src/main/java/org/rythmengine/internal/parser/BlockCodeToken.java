/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

import org.rythmengine.internal.IBlockHandler;
import org.rythmengine.internal.IContext;

public class BlockCodeToken extends CodeToken implements IBlockHandler {

    public BlockCodeToken(String s, IContext context) {
        super(s, context);
        context.openBlock(this);
    }

    @Override
    public void openBlock() {
    }

    @Override
    public String closeBlock() {
        return "}";
    }

}
