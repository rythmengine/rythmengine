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
import org.rythmengine.internal.parser.ParserBase;

/**
 * Parse @inherited - render default section content
 * shall be used only within a section context
 */
public class RenderInheritedParser extends KeywordParserFactory {
    @Override
    public Keyword keyword() {
        return Keyword.RENDER_INHERITED;
    }

    private static class RenderInheritedToken extends CodeToken {
        private String section;

        RenderInheritedToken(String section, IContext ctx) {
            super("", ctx);
            this.section = section;
        }

        @Override
        public void output() {
            p2t("__pLayoutSectionInherited(\"").p(section).p("\");\n");
        }
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("bad @inherited statement. Correct usage: @inherited()");
                }
                String section = ctx.currentSection();
                if (null == section) {
                    raiseParseException("@inherited() shall be used only within a @section context");
                }
                final String matched = r.stringMatched();
                step(matched.length());
                return new RenderInheritedToken(section, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))\\s*)";
    }

    public static void main(String[] args) {
        Regex r = new RenderInheritedParser().reg(Rythm.INSTANCE);
        if (r.search("@renderBody(ab: 1, foo.bar())")) ;
        p(r, 6);
    }

}
