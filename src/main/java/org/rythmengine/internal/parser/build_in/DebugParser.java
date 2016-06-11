/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.IRemoveLeadingLineBreakAndSpaces;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/02/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DebugParser extends KeywordParserFactory implements IRemoveLeadingLineBreakAndSpaces {

    @Override
    public Keyword keyword() {
        return Keyword.DEBUG;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @debug, correct usage: @debug(\"msg\", args...)");
                }
                String matched = r.stringMatched();
                step(matched.length());
                boolean leadLB = matched.startsWith("\n"), afterLB = matched.endsWith("\n");
                if (leadLB || afterLB) {
                    ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                    if (!(leadLB && afterLB)) {
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    }
                } else {
                    Regex r0 = new Regex("([ \\t\\x0B\\f]*).*");
                    if (r0.search(matched)) {
                        String blank = r0.stringMatched(1);
                        if (blank.length() > 0) {
                            ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                        }
                    }
                }
                String s = new TextBuilder().p("__logger.debug").p(r.stringMatched(2)).p(";").toString();
                return new CodeToken(s, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(^\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))[\\n]?)";
    }

}
