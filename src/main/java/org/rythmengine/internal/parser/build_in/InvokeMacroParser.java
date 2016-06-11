/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.CodeBuilder;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.dialect.Rythm;
import org.rythmengine.internal.parser.ParserBase;

/**
 * Parse invocation:
 *
 * "@myMacro()" Note since this is also a pattern for expression parser, InvokeMacroParser must
 * be put in front of InvokeTagParser and expression parser
 */
public class InvokeMacroParser extends CaretParserFactoryBase {

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {

            @Override
            public Token go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a()));
                if (!r.search(remain())) return null;
                String macro = r.stringMatched(2);
                CodeBuilder cb = ctx().getCodeBuilder();
                // inline tag has higher priority than macro
                if (cb.hasInlineTagWithoutArgument(macro)) return null;
                if (!cb.hasMacro(macro)) return null;
                int curLine = currentLine();
                step(r.stringMatched().length());
                return new ExecMacroToken(macro, ctx(), curLine);
            }
        };
    }


    private static String patternStr() {
        return "^(%s([_a-zA-Z][a-zA-Z$_\\.0-9]+)[ \t]*\\([ \t]*\\))";
    }

    public static void main(String[] args) {
        InvokeMacroParser P = new InvokeMacroParser();
        Regex r = new Regex(String.format(P.patternStr(), Rythm.INSTANCE.a()));
        String s = "@ab ( )";
        p(s, r);
    }
}
