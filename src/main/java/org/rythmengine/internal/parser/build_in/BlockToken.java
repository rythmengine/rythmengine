/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IBlockHandler;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;

public class BlockToken extends Token implements IBlockHandler {

    public static class LiteralBlock extends BlockToken {
        public LiteralBlock(IContext context) {
            super("{", context);
        }

        @Override
        public String closeBlock() {
            return "}";
        }
    }

    public BlockToken(String s, IContext context) {
        super(s, context);
        context.openBlock(this);
    }

    @Override
    public void openBlock() {
    }

    @Override
    public String closeBlock() {
        return "\np('}');\n";
    }

}
