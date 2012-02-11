package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse tag invocation:
 *
 * @myApp.myTag(...)
 * 
 * Note since this is also a pattern for expression parser, InvokeTagParser must 
 * be put in front of expression parser
 */
public class InvokeTagParser extends CaretParserFactoryBase {

    private static class ParameterDeclaration {
        String nameDef;
        String valDef;
        ParameterDeclaration(String name, String val) {
            if (null != name) {
                if (name.startsWith("\"") || name.startsWith("'")) name = name.substring(1);
                if (name.endsWith("\"") || name.endsWith("'")) name = name.substring(0, name.length() - 1);
            }
            nameDef = name;
            valDef = val;
            //System.out.println(String.format("%s : %s", name, val));
        }
        @Override
        public String toString() {
            return String.format("%s:%s", nameDef,  valDef);
        }
    }

    private static class ParameterDeclarationList {
        private List<ParameterDeclaration> pl = new ArrayList<ParameterDeclaration>();
        void addParameterDeclaration(String nameDef, String valDef) {
            pl.add(new ParameterDeclaration(nameDef, valDef));
        }
        @Override
        public String toString() {
            return pl.toString();
        }
    }

    private static class InvokeTagToken extends CodeToken {
        String tagName;
        ParameterDeclarationList params = new ParameterDeclarationList();

        InvokeTagToken(String tagName, String paramLine, IContext context) {
            super(null, context);
            this.tagName = tagName;
            parse(paramLine);
        }

        /*
         * Parse line like (bar='c', foo=bar.length(), zee=component[foo], "hello")
         */
        private void parse(String line) {
            if (null == line || "".equals(line.trim())) return;
            // strip '(' and ')'
            line = line.substring(1).substring(0, line.length() - 2);
            Regex r = new Regex("\\G(,\\s*)?((([a-zA-Z_][\\w$_]*)\\s*[=:]\\s*)?('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))");
            while (r.search(line)) {
                params.addParameterDeclaration(r.stringMatched(4), r.stringMatched(5));
            }
        }

        @Override
        public void output() {
            p("\n{\n\tcom.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;");
            if (params.pl.size() > 0) {
                p("\n\t_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList();");
                for (int i = 0; i < params.pl.size(); ++i) {
                    ParameterDeclaration pd = params.pl.get(i);
                    //if (i == 0 && pd.nameDef == null) pd.nameDef = "arg";
                    p("\n\t_pl.add(\"").p(pd.nameDef == null ? "" : pd.nameDef).p("\",").p(pd.valDef).p(");");
                }
            }
            outputInvokeStatement();
        }
        
        protected void outputInvokeStatement() {
            p("\n\t_invokeTag(\"").p(tagName).p("\", _pl);\n}");
        }

    }

    private static class InvokeTagWithBodyToken extends InvokeTagToken implements IBlockHandler {

        InvokeTagWithBodyToken(String tagName, String paramLine, IContext context) {
            super(tagName, paramLine, context);
            context.openBlock(this);
        }

        @Override
        public void openBlock() {
        }

        @Override
        protected void outputInvokeStatement() {
            String curClassName = ctx.getCodeBuilder().className();
            p("\n\t_invokeTag(\"").p(tagName).p("\", _pl, new com.greenlaw110.rythm.runtime.ITag.Body(").p(curClassName).p(".this) {");
            p("\n\t\t@Override public void setProperty(String name, Object val) {\n\t\t\tsetRenderArg(name, val);\n\t}");
            p("\n\t\t@Override public Object getProperty(String name) {\n\t\t\treturn getRenderArg(name);}");
            p("\n\t\t@Override public void call() {");
        }

        @Override
        public String closeBlock() {
            return "\n\t\t}\n\t});\n}";
        }

    }

    private static final Pattern P_HEREDOC_SIMBOL = Pattern.compile("(\\s*<<).*", Pattern.DOTALL);

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            
            boolean isTag(String name) {
                return ctx().getCodeBuilder().engine.isTag(name);
            }
            
            @Override
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a()));
                if (!r.search(remain())) return null;
                String tagName = r.stringMatched(2);
                if (!isTag(tagName)) return null;
                String s = r.stringMatched();
                ctx().step(s.length());
                s = remain();
                Matcher m = P_HEREDOC_SIMBOL.matcher(s);
                if (m.matches()) {
                    ctx().step(m.group(1).length());
                    return new InvokeTagWithBodyToken(tagName, r.stringMatched(3), ctx());
                } else {
                    return new InvokeTagToken(tagName, r.stringMatched(3), ctx());
                }
            }
        };
    }


    private static String patternStr() {
        return "^(%s([a-zA-Z][a-zA-Z$_\\.0-9]+)\\s*((?@())*))";
    }
    
    public static void main(String[] args) {
        IContext ctx = new TemplateParser(new CodeBuilder(null, "", null, null));
        String ps = String.format(new InvokeTagParser().patternStr(), "@");
        Regex r = new Regex(ps);
        String s = "@xyz (xyz: zbc, y=component.left[bar.get(bar[123]).foo(\" hello\")].get(v[3])[3](), \"hp\")  Gren";
        //s = "<link href=\"http://abc.com/css/xyz.css\" type=\"text/css\">";
        if (r.search(s)) {
            new InvokeTagToken(r.stringMatched(2), r.stringMatched(3), ctx);
            System.out.println(r.stringMatched());
        }
        else System.out.println("not found");
        
//        String s = " << asdfuisf@";
//        Matcher m = P_HEREDOC_SIMBOL.matcher(s);
//        if (m.matches()) {
//            System.out.println(m.group(1));
//        }
    }

}
