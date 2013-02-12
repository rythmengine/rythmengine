package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingSpacesIfLineBreakParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class ForEachParser extends KeywordParserFactory {
    private static final ILogger logger = Logger.get(ForEachParser.class);

    public IParser create(IContext ctx) {

        return new RemoveLeadingSpacesIfLineBreakParser(ctx) {
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr2(), dialect().a(), keyword()));
                String remain = remain();
                if (!r.search(remain)) {
                    raiseParseException("Error parsing @for statement, correct usage: @for(Type var: Iterable){...} or @for(int i = ...)");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(2);
                if (s.contains(";")) {
                    if (!ctx().getDialect().enableFreeForLoop()) {
                        throw new TemplateParser.NoFreeLoopException(ctx());
                    }
                    return new BlockCodeToken("for " + s + "{ //line: " + ctx().currentLine() + "\n\t", ctx()) {
                        @Override
                        public void openBlock() {
                            ctx().pushBreak(IContext.Break.BREAK);
                            ctx().pushContinue(IContext.Continue.CONTINUE);
                        }

                        @Override
                        public void output() {
                            super.output();
                        }

                        @Override
                        public String closeBlock() {
                            ctx().popBreak();
                            return super.closeBlock();
                        }
                    };
                } else {
                    s = S.stripBrace(s);
                    int pos0 = -1, pos1 = -1;
                    String iterable = null, varname = null, type = null;
                    if (s.matches("\\s*\".*")) {
                        iterable = s;
                    } else if (s.contains(" in ")) {
                        pos0 = s.indexOf(" in ");
                        pos1 = pos0 + 4;
                    } else if (s.contains(" <- ")) {
                        pos0 = s.indexOf(" <- ");
                        pos1 = pos0 + 4;
                    } else if (s.contains(":")) {
                        pos0 = s.indexOf(":");
                        pos1 = pos0 + 1;
                    } else {
                        // the for(Iterable) style
                        iterable = s;
                    }
                    if (-1 != pos0) {
                        String s1 = s.substring(0, pos0).trim();
                        iterable = s.substring(pos1, s.length());
                        if (s1.contains(" ")) {
                            pos0 = s1.indexOf(" ");
                            type = s1.substring(0, pos0);
                            varname = s1.substring(pos0, s1.length());
                        } else {
                            varname = s1;
                        }
                    }
                    return new ForEachCodeToken(type, varname, iterable, ctx());
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EACH;
    }

    // match for(int i=0; i<100;++i) {
    protected String patternStr2() {
        return "^%s%s\\s*((?@()))([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?)";
    }

    @Override
    protected String patternStr() {
        //return "^(%s%s(\\s*\\(\\s*)(((" + Patterns.Type + "\\s+)?)((" + Patterns.VarName + "))?)\\s*([\\:]?)\\s*(" + Patterns.Expression2 + ")(\\s*\\)?[\\s\\r\\n]*|[\\s\\r\\n]+)\\{?[\\s\\r\\n]*).*";
        //return "^(((" + Patterns.Type + ")\\s+)?(" + Patterns.VarName + ")\\s*\\:\\s*)?(" + Patterns.Expression2 + ")$";
        //return "^((([a-zA-Z0-9_\\.]+)(\\s*\\[\\s*\\]|\\s*(?@<>))?\\s+)?(" + Patterns.VarName + ")\\s*\\:\\s*)?(" + Patterns.Expression2 + ")$";
        //return "^((([a-zA-Z0-9_\\.]+)(\\s*\\[\\s*\\]|\\s*(?@<>))?\\s+)?(" + Patterns.VarName + ")\\s*\\:\\s*)?(" + Patterns.Expression2 + ")$";
        // this method not used anymore
        return null;
    }
}
