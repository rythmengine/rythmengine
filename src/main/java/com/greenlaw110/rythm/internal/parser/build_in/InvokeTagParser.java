package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse tag invocation:
 *
 * @myApp.myTag(...)
 *
 * Note since this is also a pattern for expression parser, InvokeTagParser must
 * be put in front of expression parser
 */
public class InvokeTagParser extends CaretParserFactoryBase {

    public static class ParameterDeclaration {
        public String nameDef;
        public String valDef;
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

    public static class ParameterDeclarationList {
        public List<ParameterDeclaration> pl = new ArrayList<ParameterDeclaration>();
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
        protected boolean enableCache = false;
        protected String cacheDuration = null;
        protected String cacheArgs = null;

        protected String cacheKey() {
            return tagName;
        }

        InvokeTagToken(String tagName, String paramLine, boolean cacheFor, String duration, String cacheForArgs, IContext context) {
            super(null, context);
            this.tagName = tagName;
            this.enableCache = cacheFor;
            this.cacheDuration = S.isEmpty(duration) ? "null" : duration;
            this.cacheArgs = S.isEmpty(cacheForArgs) ? ", _pl.toUUID()" : cacheArgs;
            parse(paramLine);
        }

        /*
         * Parse line like (bar='c', foo=bar.length(), zee=component[foo], "hello")
         */
        private void parse(String line) {
            if (null == line || "".equals(line.trim())) return;
            // strip '(' and ')'
            line = line.substring(1).substring(0, line.length() - 2);
            Regex r = new Regex("\\G(,\\s*)?((([a-zA-Z_][\\w$_]*)\\s*[=:]\\s*)?('.'|(?@\"\")|[0-9\\.]+[l]?|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))");
            while (r.search(line)) {
                params.addParameterDeclaration(r.stringMatched(4), r.stringMatched(5));
            }
        }

        @Override
        public void output() {
            p("{");
            pline();
            pt("com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null; ");
            pline();
            if (params.pl.size() > 0) {
                pt("_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList();");
                pline();
                for (int i = 0; i < params.pl.size(); ++i) {
                    ParameterDeclaration pd = params.pl.get(i);
                    //if (i == 0 && pd.nameDef == null) pd.nameDef = "arg";
                    pt("_pl.add(\"").p(pd.nameDef == null ? "" : pd.nameDef).p("\",").p(pd.valDef).p(");");
                    pline();
                }
            }
            if (enableCache) {
                pt("String s = _engine().cached(\"").p(cacheKey()).p("\"");
                p(cacheArgs).p(");");
                pline();
                pt("if (null != s) {");
                pline();
                p2t("p(s);");
                pline();
                pt("} else {");
                pline();
                p2t("StringBuilder sbOld = getOut();");
                pline();
                p2t("StringBuilder sbNew = new StringBuilder();");
                pline();
                p2t("setOut(sbNew);");
                pline();
            }
            outputInvokeStatement();
        }

        protected void outputInvokeStatement() {
            if (enableCache) {
                p2t("_invokeTag(\"").p(tagName).p("\", _pl);");
                pline();
                p2t("s = sbNew.toString();");
                pline();
                p2t("setOut(sbOld);");
                pline();
                p2t("_engine().cache(\"").p(cacheKey()).p("\", s, ").p(cacheDuration).p(cacheArgs).p(");");
                pline();
                p2t("p(s);");
                pline();
                pt("}");
                pline();
                p("}");
                pline();
            } else {
                pt("_invokeTag(\"").p(tagName).p("\", _pl);");
                pline();
                p("}");
                pline();
            }
        }

    }

    private static class InvokeTagWithBodyToken extends InvokeTagToken implements IBlockHandler {
        private String textListenerKey = UUID.randomUUID().toString();
        private StringBuilder tagBodyBuilder = new StringBuilder();
        private int startIndex = 0;
        private int endIndex = 0;
        private String key = null;
        InvokeTagWithBodyToken(String tagName, String paramLine, boolean cacheFor, String cacheForDuration, String cacheForArgs, IContext context) {
            super(tagName, paramLine, cacheFor, cacheForDuration, cacheForArgs, context);
            context.openBlock(this);
            startIndex = ctx.cursor();
        }

        @Override
        protected String cacheKey() {
            return tagName + key;
        }

        @Override
        public void openBlock() {
        }

        @Override
        protected void outputInvokeStatement() {
            String curClassName = ctx.getCodeBuilder().className();
            p2t("_invokeTag(\"").p(tagName).p("\", _pl, new com.greenlaw110.rythm.runtime.ITag.Body(").p(curClassName).p(".this) {");
            pline();
            p3t("@Override public void setProperty(String name, Object val) {");
            pline();
            p4t("setRenderArg(name, val);");
            pline();
            p3t("}");
            pline();
            p3t("@Override public Object getProperty(String name) {");
            pline();
            p4t("return getRenderArg(name); ");
            pline();
            p3t("}");
            pline();
            p3t("@Override public void call() {");
            pline();
        }

        @Override
        public String closeBlock() {
            if (!enableCache) {
                return "\n\t\t}\n\t});\n}";
            }
            endIndex = ctx.cursor();
            String body = ctx.getTemplateSource(startIndex, endIndex);
            key = UUID.nameUUIDFromBytes(body.getBytes()).toString();
            StringBuilder sbOld = getOut();
            StringBuilder sbNew = new StringBuilder();
            setOut(sbNew);
            p3t("}");
            pline();
            p2t("});");
            pline();
            p2t("s = sbNew.toString();");
            pline();
            p2t("setOut(sbOld);");
            pline();
            p2t("_engine().cache(\"").p(cacheKey()).p("\",s,").p(cacheDuration).p(cacheArgs).p(");");
            pline();
            p2t("p(s);");
            pline();
            pt("}");
            pline();
            p("}");
            pline();
            String s = sbNew.toString();
            setOut(sbOld);
            return s;
        }

    }

    private static final Pattern P_HEREDOC_SIMBOL = Pattern.compile("(\\s*<<).*", Pattern.DOTALL);
    private static final Pattern P_STANDARD_BLOCK = Pattern.compile("(\\s*\\{).*", Pattern.DOTALL);

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {

            boolean isTag(String name) {
                return ctx().getCodeBuilder().engine.isTag(name, ctx().getTemplateClass());
            }

            @Override
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a()));
                if (!r.search(remain())) return null;
                String tagName = r.stringMatched(2);
                if (!isTag(tagName)) return null;
                String s = r.stringMatched();
                ctx().step(s.length());
                String cacheFor = r.stringMatched(5);
                boolean enableCacheFor = false;
                String cacheForDuration = null;
                String cacheForArgs = null;
                if (null != cacheFor) {
                    enableCacheFor = true;
                    cacheFor = S.stripBrace(cacheFor); // "1h",1,foo.bar()
                    String[] cacheForArray = cacheFor.split(",");
                    if (cacheForArray.length > 0) {
                        cacheForDuration = cacheForArray[0]; // "1h"
                    }
                    if (cacheForArray.length > 1) {
                        cacheForArgs = cacheFor.replaceFirst(cacheForDuration, "");
                    }
                }
                s = remain();
                Matcher m0 = P_HEREDOC_SIMBOL.matcher(s);
                Matcher m1 = P_STANDARD_BLOCK.matcher(s);
                if (m0.matches()) {
                    ctx().step(m0.group(1).length());
                    return new InvokeTagWithBodyToken(tagName, r.stringMatched(3), enableCacheFor, cacheForDuration, cacheForArgs, ctx());
                } else if (m1.matches()) {
                    ctx().step(m1.group(1).length());
                    return new InvokeTagWithBodyToken(tagName, r.stringMatched(3), enableCacheFor, cacheForDuration, cacheForArgs, ctx());
                } else {
                    return new InvokeTagToken(tagName, r.stringMatched(3), enableCacheFor, cacheForDuration, cacheForArgs, ctx());
                }
            }
        };
    }


    private static String patternStr() {
        return "^(%s([a-zA-Z][a-zA-Z$_\\.0-9]+)\\s*((?@()))(\\.cache((?@())))?)";
    }

    public static void main(String[] args) {
        IContext ctx = new TemplateParser(new CodeBuilder(null, "", null, null, null));
        String ps = String.format(new InvokeTagParser().patternStr(), "@");
        Regex r = new Regex(ps);
        String s = "@xyz (xyz: zbc, y=component.left[bar.get(bar[123]).foo(\" hello\")].get(v[3])[3](), \"hp\").cacheFor(\"1h\", 1 , foo.bar())  Gren";
        //String s = "@xyz().cacheFor(\"1h\")";
        //s = "<link href=\"http://abc.com/css/xyz.css\" type=\"text/css\">";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(3));
            System.out.println(r.stringMatched(4));
            System.out.println(r.stringMatched(5));
            //InvokeTagToken t = new InvokeTagToken(r.stringMatched(2), r.stringMatched(3), ctx);
            //System.out.println(t.params);
        }
        else System.out.println("not found");

//        String s = " << asdfuisf@";
//        Matcher m = P_HEREDOC_SIMBOL.matcher(s);
//        if (m.matches()) {
//            System.out.println(m.group(1));
//        }
    }

}
