/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.IKeyword;
import org.rythmengine.internal.IKeywordParserFactory;
import org.rythmengine.internal.parser.ParserBase;
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
