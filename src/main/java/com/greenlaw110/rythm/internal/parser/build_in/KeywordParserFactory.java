package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.IKeyword;
import com.greenlaw110.rythm.internal.IKeywordParserFactory;
import com.stevesoft.pat.Regex;

import java.util.regex.Pattern;

public abstract class KeywordParserFactory extends CaretParserFactoryBase implements IKeywordParserFactory {
    public abstract IKeyword keyword();
    public String[] interests() {
        return new String[]{};
    }

    protected abstract String patternStr();

    private Pattern p = null;
    protected Pattern ptn(IDialect d) {
        if (null == p) {
            p = ParserBase.pattern(patternStr(), d.a(), keyword());
        }
        return p;
    }

    protected Regex reg(IDialect d) {
        return new Regex(String.format(patternStr(), d.a(), keyword()));
    }

}
