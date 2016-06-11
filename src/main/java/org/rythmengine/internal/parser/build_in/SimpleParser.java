/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.dialect.Rythm;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

/**
 * parse @__simple__: mark the current template is simple template
 */
@Deprecated
public class SimpleParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.SIMPLE;
    }

    public IParser create(IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @__simple__ statement");
                }
                step(r.stringMatched().length());
                //ctx().getCodeBuilder().setSimpleTemplate(ctx().currentLine());
                return new CodeToken("", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s(?@())?)\\s+";
    }

    public static void main(String[] args) {
        Regex r = new SimpleParser().reg(Rythm.INSTANCE);
        if (r.search("@__simple__() ad")) {
            p(r, 3);
        }
    }

}
