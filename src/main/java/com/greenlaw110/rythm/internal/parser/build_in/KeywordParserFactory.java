package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.stevesoft.pat.Regex;

public abstract class KeywordParserFactory extends CaretParserFactoryBase {
    public abstract Keyword keyword();
    public String[] interests() {
        return new String[]{};
    }
    
    protected abstract String patternStr();
    
    private Pattern p = null;
    protected Pattern ptn(DialectBase d) {
        if (null == p) {
            p = ParserBase.pattern(patternStr(), d.a(), keyword());
        }
        return p;
    }
    
    private Regex r = null;
    protected Regex reg(DialectBase d) {
        if (null == r) {
            r = new Regex(String.format(patternStr(), d.a(), keyword()));
        }
        return r;
    }
}
