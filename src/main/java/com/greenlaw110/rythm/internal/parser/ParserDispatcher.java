package com.greenlaw110.rythm.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.IParserFactory;
import com.greenlaw110.rythm.util.TextBuilder;

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
        P = pattern("%s(%s)(\\s*|\\(|\\{).*", a(), PatternStr.VarName);
    }

    public TextBuilder go() {
        DialectBase d = dialect();
        IContext c = ctx();
        Matcher m = P.matcher(remain());
        if (m.matches()) {
            String s = m.group(1);
            IParser p = d.createBuildInParser(s, c);
            if (null != p) return p.go();
        }
        for (IParserFactory f: d.freeParsers()) {
            IParser p = f.create(c);
            TextBuilder t = p.go();
            if (null != t) return t;
        }
        return null;
    }
    
    public static void main(String[] args) {
        Pattern p = Pattern.compile("@([a-zA-Z0-9_]+)(\\s*|\\(|\\{).*");
        Matcher m = p.matcher("@var String name;Hello @name");
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
