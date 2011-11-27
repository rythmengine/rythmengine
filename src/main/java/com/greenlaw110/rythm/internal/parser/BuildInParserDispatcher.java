package com.greenlaw110.rythm.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

/**
 * BuildInParserDispatcher test remaining template content and try
 * to find the build in parser which is interested to the next 
 * build in keyword. If one is found then the parse process is 
 * passed to it. Otherwise null token is returned
 * 
 * @author luog
 */
public class BuildInParserDispatcher extends ParserBase {
    
    private final Pattern P;
    
    public BuildInParserDispatcher(DialectBase dialect, IContext context) {
        super(dialect, context);
        P = pattern("%s(%s)\\s*(\\(|\\{)", a(), R_VARNAME);
    }

    @Override
    public TextBuilder go() {
        Matcher m = P.matcher(remain());
        String s = m.group(1);
        IParser p = dialect().createBuildInParser(s, ctx());
        return null == p ? null : p.go();
    }

}
