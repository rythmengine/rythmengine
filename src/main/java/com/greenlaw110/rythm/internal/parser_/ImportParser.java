package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.CodeBuilder;

public class ImportParser extends ParserBase {
    
    public ImportParser(IContext context) {
        super(context);
    }
    
    private static final String PTN = "(%s%s[\\s]+[a-zA-Z0-9_\\.*,\\s]+(;|\\r?\\n)+).*";

    @Override
    public Token go() {
        // @import x.y.z,a.b.c,...;
        Pattern p = Pattern.compile(String.format(PTN, a(), _import()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.step(s.length());
        String imports = s.replaceFirst(String.format("%s%s[\\s]+", a(), _import()), "").replaceFirst("(;|\\r?\\n)+$", "");
        return new Directive(imports, ctx) {
            @Override
            public void call() {
                String[] sa = s.split("[,\\s]+");
                CodeBuilder b = builder();
                for (String imp: sa) {
                    b.addImport(imp);
                }
            }
        };
    }
    
    public static void main(String[] args) {
        String sp = String.format(PTN, "@", "import");
        System.out.println(sp);
        Pattern p = Pattern.compile(sp, Pattern.DOTALL);
        String s1 = "@import java.util.* java.io.*;\n@arg String name;\nHello World";
        System.out.println(s1);
        Matcher m = p.matcher(s1);
        if (m.matches()) {
            String s = m.group(1);
            String imports = s.replaceFirst(String.format("%s%s[\\s]+", "@", "import"), "");
            for (String s0: imports.split("[,\\s]+")) {
                System.out.println(s0);
            }
        } else {
            System.out.println("not match");
        }
    }

}
