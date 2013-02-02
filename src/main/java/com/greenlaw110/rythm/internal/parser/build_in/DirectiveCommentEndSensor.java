package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.ILang;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect if a directive comment is closed and strip
 * it out from the parsing process
 */
public class DirectiveCommentEndSensor extends ParserBase {
    ILogger logger = Logger.get(DirectiveCommentEndSensor.class);

    public DirectiveCommentEndSensor(IContext context) {
        super(context);
    }

    private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        if (!ctx.insideDirectiveComment()) {
            return null;
        }
        ILang lang = ctx.peekLang();
        while (null != lang) {
            String s = lang.commentEnd();
            s = S.escapeRegex(s).toString();
            s = "(" + s + ")" + ".*";
            Pattern p = patterns.get(s);
            if (null == p) {
                p = Pattern.compile(s, Pattern.DOTALL);
                patterns.put(s, p);
            }
            Matcher m = p.matcher(remain());
            if (m.matches()) {
                s = m.group(1);
                ctx.step(s.length());
                ctx.leaveDirectiveComment();
                return new TextBuilder();
            }
            lang = lang.getParent();
        }
        
        return null;
    }

    public static void main(String[] args) {
        String s = "<!--@if(true){-->123<!--} " +
                "else  {-->456} 111 " +
                "<script>/*@for(int i = 0; i < 2; ++i){*/@i,<!--}-->" +
                "</script>";
        s = Rythm.render(s);
        System.out.println(s);
    }
}
