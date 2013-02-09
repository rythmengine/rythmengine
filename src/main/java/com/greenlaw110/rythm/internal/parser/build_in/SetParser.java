package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.Token;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @set("name":val())
 */
public class SetParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.SET;
    }

    @Override
    protected String patternStr() {
        return "^(%s%s((?@())))";
    }

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
                step(r.stringMatched().length()); // remain: @set("name": val)...
                String s = r.stringMatched(2); // s: ("name": val)
                s = s.substring(1); // s: name: val)
                s = s.substring(0, s.length() - 1); // s: "name": val
                //r = new Regex("((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)\\s*[=:]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*)");
                r = new Regex("((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)\\s*[=:]\\s*(.*)");
                if (!r.search(s)) {
                    raiseParseException("Error parsing @set tag. Correct usage: @set(\"name\": val)");
                }
                s = r.stringMatched(1); // propName: "name"
                if (s.startsWith("\"") || s.startsWith("'")) {
                    s = s.substring(1);
                    s = s.substring(0, s.length() - 1);
                    // propName: name
                }
                final String propName = s;
                final String propVal = r.stringMatched(2);
                return new Token("", ctx()) {
                    @Override
                    protected void output() {
                        p("\n_setRenderProperty(\"").p(propName).p("\",").p(propVal).p(");");
                    }
                };
            }
        };
    }

    public static void main(String[] args) {
//        String s = "@set(name:abc.x())";
//        SetParser ap = new SetParser();
//        Regex r = ap.reg(new Rythm());
//        System.out.println(r);
//        if (r.search(s)) {
//            System.out.println("m: " + r.stringMatched());
//            System.out.println("1: " + r.stringMatched(1));
//            System.out.println("2: " + r.stringMatched(2));
//            System.out.println("3: " + r.stringMatched(3));
//            System.out.println("4: " + r.stringMatched(4));
//            System.out.println("5: " + r.stringMatched(5));
//        }
        Regex r = new Regex("((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)\\s*[=:]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*)");
        String s = " title: title";
        p(s, r);
    }

}
