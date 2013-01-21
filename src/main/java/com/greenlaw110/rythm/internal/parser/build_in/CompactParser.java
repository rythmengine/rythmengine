package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @compact() {...}
 */
public class CompactParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.COMPACT;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());
                if (!m.matches()) return null;
                step(m.group(1).length());
                return new BlockCodeToken("", ctx()) {
                    @Override
                    public void openBlock() {
                        ctx().getCodeBuilder().addBuilder(new Token("", ctx()){
                            @Override
                            protected void output() {
                                ctx().pushCompact(true);
                                super.output();
                            }
                        });
                    }

                    @Override
                    public String closeBlock() {
                        ctx().getCodeBuilder().addBuilder(new Token("", ctx()){
                            @Override
                            protected void output() {
                                ctx().popCompact();
                                super.output();
                            }
                        });
                        return "";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s\\s*\\(\\s*\\)[\\s]*\\{).*";
    }

    public static void main(String[] args) {
        Pattern p = new CompactParser().ptn(Rythm.INSTANCE);
        Matcher m = p.matcher("@compact() {\n" +
                "    @body\n" +
                "}");
        if (m.find()) {
            System.out.println(m.group(1));
        }
    }

}
