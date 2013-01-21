package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse invocation:
 *
 * @myMacro()
 *
 * Note since this is also a pattern for expression parser, InvokeMacroParser must
 * be put in front of InvokeTagParser and expression parser
 */
public class InvokeMacroParser extends CaretParserFactoryBase {

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a()));
                if (!r.search(remain())) return null;
                String macro = r.stringMatched(2);
                if (!ctx().getCodeBuilder().hasMacro(macro)) return null;
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
