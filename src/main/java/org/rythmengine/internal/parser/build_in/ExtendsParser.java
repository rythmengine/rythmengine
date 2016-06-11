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
import org.rythmengine.internal.parser.Directive;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

/**
 * Parse @extends path/to/mylayout.html or @extends path.to.mylayout.html
 */
public class ExtendsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.EXTENDS;
    }

    private static void error(IContext ctx) {
        raiseParseException(ctx, "Error parsing extends statement. The correct format is @extends(\"my.parent.template\"[, arg1=val1, val2, ...])");
    }

    public IParser create(IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    error(ctx());
                }
                final int lineNo = currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(2);
                if (null == s) {
                    error(ctx());
                }
                r = innerPattern;
                if (!r.search(s)) error(ctx());

                // process extend target
                s = r.stringMatched(1);
                s = S.stripQuotation(s);
                final String sExtend = s;

                // process extend params
                final InvokeTemplateParser.ParameterDeclarationList params = new InvokeTemplateParser.ParameterDeclarationList();
                s = r.stringMatched(2);
                if (!S.isEmpty(s)) {
                    //r = argsPattern;
                    r = new Regex("\\G(,\\s*)?((([a-zA-Z_][\\w$_]*)\\s*[=:]\\s*)?((?@())|'.'|(?@\"\")|[0-9\\.]+[l]?|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*)|[_a-zA-Z][a-z_A-Z0-9]*)");
                    while (r.search(s)) {
                        params.addParameterDeclaration(r.stringMatched(4), r.stringMatched(5), ctx());
                    }
                }


                return new Directive(s, ctx()) {
                    @Override
                    public void call() {
                        try {
                            builder().setExtended(sExtend, params, lineNo);
                        } catch (NoClassDefFoundError e) {
                            raiseParseException("error adding includes: " + e.getMessage() + "\n possible cause: lower/upper case issue on windows platform");
                        }
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(^\\n?[ \\t\\x0B\\f]*%s%s)\\s*((?@())[\\s\\r\\n;]*)";
    }

    protected static Regex innerPattern = new Regex("\\((.*?)\\s*(,\\s*(.*))?\\)");
    protected static Regex argsPattern = new Regex("\\G(,\\s*)?((([a-zA-Z_][\\w$_]*)\\s*[=:]\\s*)?((?@())|'.'|(?@\"\")|[0-9\\.]+[l]?|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))");

    protected String patternStr0() {
        return "(\\n?[ \\t\\x0B\\f]*%s%s(\\s*\\((.*)\\)|\\s+([_a-zA-Z\\\\\\\\/][a-zA-Z0-9_\\.\\\\\\\\/]+))[;]?)";
    }

}
