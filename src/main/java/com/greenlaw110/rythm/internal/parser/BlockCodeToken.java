package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;

public class BlockCodeToken extends CodeToken implements IBlockHandler {

    public BlockCodeToken(String s, IContext context) {
        super(s, context);
    }

    @Override
    public void openBlock() {
    }

    @Override
    public void closeBlock() {
        p("}");
    }

}
