/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.IParserFactory;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.dialect.DialectBase;
import org.rythmengine.utils.F;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BuildInParserDispatcher test remaining template content and try
 * to find the build in parser which is interested to the next
 * build in keyword. If one is found then the parse process is
 * passed to it. Otherwise null token is returned
 *
 * @author luog
 */
public class ParserDispatcher extends ParserBase {

    private final Pattern P;

    public ParserDispatcher(IContext context) {
        super(context);
        P = pattern("\\n?[ \\t\\x0B\\f]*%s(%s)(\\s*|\\(|\\{).*", a(), Patterns.VarName);
    }
    
    public F.T2<IParser, Token> go2() {
        DialectBase d = (DialectBase) dialect();
        IContext c = ctx();
        Matcher m = P.matcher(remain());
        if (m.matches()) {
            String s = m.group(1);
            IParser p = d.createBuildInParser(s, c);
            if (null != p) {
                Token tb = p.go();
                if (null != tb) return F.T2(p, tb);
            }
        }
        for (IParserFactory f : d.freeParsers()) {
            IParser p = f.create(c);
            Token tb = p.go();
            if (null != tb) return F.T2(p, tb);
        }
        return null;
    }

    public Token go() {
        throw new UnsupportedOperationException();
    }

}
