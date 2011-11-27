package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;

public abstract class BuildInSpecialParserFactory extends BuildInParserFactory {

    @Override
    public final Keyword keyword() {
        throw new UnsupportedOperationException("Special Parser is not driven by keyword");
    }

}
