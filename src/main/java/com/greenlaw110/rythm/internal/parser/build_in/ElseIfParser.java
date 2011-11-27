package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.stevesoft.pat.Regex;

/**
 * <ul>Recognized the following patterns:
 * <li><code>@} else if (...) {...</code></li>
 * <li><code>@ else if (...) {...</code><li>
 * <li><code>@ else (...) {...</code><li>
 * 
 * @author luog
 *
 */
public class ElseIfParser extends BuildInSpecialParserFactory {

    @Override
    public IParser create(DialectBase dialect, IContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String patternStr() {
        return null;
    }

    public static void main(String[] args) {
        String p = String.format(new ElseIfParser().patternStr(), "@", "if");
        System.out.println(p);
        
        Regex r = new Regex(p);
        String s = "@if(user.registered()) \n dsfd";
        if (r.search(s)) {
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
    }
}
