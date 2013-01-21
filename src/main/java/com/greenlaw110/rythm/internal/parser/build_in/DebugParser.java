package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/02/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DebugParser  extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.DEBUG;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @debug, correct usage: @debug(\"msg\", args...)");
                }
                step(r.stringMatched().length());
                String s = new TextBuilder().p("_logger.debug").p(r.stringMatched(1)).p(";").toString();
                return new CodeToken(s, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\r\\n]*";
    }

    public static void main(String[] args) {
        String s = "@debug (\"sss\", 1)\naba";
        DebugParser ap = new DebugParser();
        Regex r = ap.reg(Rythm.INSTANCE);
        if (r.search(s)) {
            p(r);
        }
    }

}
