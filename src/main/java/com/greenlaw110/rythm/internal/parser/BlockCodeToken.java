package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;

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
