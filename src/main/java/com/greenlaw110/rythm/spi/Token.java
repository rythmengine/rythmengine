package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.parser.build_in.BlockToken;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.IJavaExtension;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends TextBuilder {

    public static class StringToken extends Token {
        public StringToken(String s, IContext ctx) {
            super(s, ctx);
        }

        public StringToken(String s, IContext context, boolean disableCompactMode) {
            super(s, context, disableCompactMode);
        }

        public StringToken mergeWith(BlockToken.LiteralBlock block) {
            StringToken merged = new StringToken(s, ctx, disableCompactMode);
            merged.line = line;
            merged.s += "{";
            return merged;
        }

        public StringToken mergeWith(StringToken st) {
            StringToken merged = new StringToken(s, ctx, disableCompactMode);
            merged.line = line;
            String s = st.s;
            s = st.compact(s);
            merged.s += s;
            return merged;
        }
    }

    protected static final ILogger logger = Logger.get(Token.class);
    protected String s;
    protected IContext ctx;
    protected int line;
    protected boolean disableCompactMode = false;
    protected boolean compactMode() {
        if (disableCompactMode) return false;
        return (null == ctx ? true : ctx.compactMode());
    }
    /*
     * Indicate whether token parse is good
     */
    private boolean ok = true;

    protected final void fail() {
        ok = false;
    }

    protected Token(String s, TextBuilder caller) {
        this(s, caller, false);
    }

    protected Token(String s, TextBuilder caller, boolean disableCompactMode) {
        super(caller);
        this.s = s;
        line = -1;
        this.disableCompactMode = disableCompactMode;
    }

    public Token(String s, IContext context) {
        this(s, context, false);
    }

    public Token(String s, IContext context, boolean disableCompactMode) {
        super(null == context ? null : context.getCodeBuilder());
        this.s = s;
        ctx = context;
        line = (null == context) ? -1 : context.currentLine();
        this.disableCompactMode = disableCompactMode;
    }

    public boolean test(String line) {
        return true;
    }

    public boolean isOk() {
        return ok;
    }

    public final TextBuilder build() {
        if (ok) output();
        else {
            pp(s);
        }
        return this;
    }

    protected void output() {
        if (null == s || "".equals(s)) return;
        pp(s);
    }

    static final List<IJavaExtension> extensions = new ArrayList<IJavaExtension>();
    public static final void addExtension(IJavaExtension extension) {
        extensions.add(extension);
    }
    public static boolean isJavaExtension(String methodName) {
        for (IJavaExtension ext : extensions) {
            if (S.isEqual(methodName, ext.methodName())) {
                return true;
            }
        }
        return false;
    }
    static {
        String[] sa = {
            "raw", "escape", "escapeHtml", "escapeJavaScript", "escapeJson", "escapeCsv", "escapeXml", "escapeJava", "camelCase", "capAll", "capFirst", "slugify", "noAccents"
        };
        for (String s: sa) {
            addExtension(new IJavaExtension.VoidParameterExtension("S", s));
        }
        addExtension(new IJavaExtension.ParameterExtension("S", "escape", ".+"));
        //addExtension(new IJavaExtension.ParameterExtension("pad",  "[0-9]+"));
    }

    private static final Regex R_ = new Regex("^\\s*(?@())\\s*$");
    private static String stripOuterBrackets(String s) {
        if (S.isEmpty(s)) return s;
        if (R_.search(s)) {
            // strip out the outer brackets
            s = R_.stringMatched();
            s = s.substring(1);
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static final Pattern P_ELVIS = Pattern.compile("(.*)(\\s*\\?\\s*:\\s*.*)");
    private static String[] stripElvis(String s) {
        if (S.isEmpty(s)) return new String[]{"", ""};
        s = stripOuterBrackets(s);
        Matcher m = P_ELVIS.matcher(s);
        if (m.matches()) {
            String s0 = m.group(1);
            String s1 = m.group(2);
            return new String[] {s0, s1};
        } else {
            return new String[] {s, ""};
        }
    }
    private static String processElvis(String s) {
        if (S.isEmpty(s)) return s;
        String[] sa = stripElvis(s);
        s = sa[0];
        String elvis = sa[1];
        if (S.isEmpty(elvis)) return s;
        elvis = elvis.replaceFirst("^\\s*\\?\\s*:\\s*", "");
        return String.format("((null == %1$s) ? %2$s : %1$s)", s, elvis);
    }
    protected final void outputExpression(List<String> nullValueTester) {
        int size = nullValueTester.size();
        for (String s: nullValueTester) {
            p("if (null != ").p(s).p(") {\n\t");
        }
        outputExpression();
        pn();
        for (int i = 0; i < size; ++i) {
            pn("}");
        }
    }
    protected final void outputExpression() {
        outputExpression(true);
    }
    protected final void outputExpression(boolean needsPrint) {
        if (S.isEmpty(s)) return;
        if (null != ctx && !ctx.getCodeBuilder().engine.enableJavaExtensions()) {
            if (needsPrint) p("\ntry{pe(").p(s).p(");} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
            else p("\ntry{").p(s).p(";} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
        } else {
            RythmEngine engine = null == ctx ? com.greenlaw110.rythm.Rythm.engine() : ctx.getEngine();
            String s = processExtensions(engine);
            if (needsPrint) p("\ntry{pe(").p(s).p(");} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
            else p("\ntry{").p(s).p(";} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
            pline();
        }
    }
    private String processExtensions(RythmEngine engine) {
        String s0 = s;
        boolean outerBracketsStripped = false;
        s = stripOuterBrackets(s);
        outerBracketsStripped = s != s0;
        class Pair {
            IJavaExtension extension;
            String signature;
            Pair(IJavaExtension e, String s) {
                extension = e;
                signature = s;
            }
        }
        Stack<Pair> allMatched = new Stack<Pair>();
        // try parse java extension first
        while(true) {
            boolean matched = false;
            for(IJavaExtension e: extensions) {
                Pattern p = e.pattern1();
                Matcher m = p.matcher(s);
                if (m.matches()) {
                    matched = true;
                    String signature = (e instanceof IJavaExtension.VoidParameterExtension) ? null : m.group(1);
                    m = e.pattern2().matcher(s);
                    s = m.replaceAll("");
                    allMatched.push(new Pair(e, signature));
                }
            }
            if (!matched) break;
        }
        boolean hasJavaExtension = !allMatched.empty();
        if (hasJavaExtension) {
            // process inner elvis expression
            s = processElvis(s);
            while (!allMatched.empty()){
                Pair p = allMatched.pop();
                s = p.extension.extend(s, p.signature);
            }
        } else {
            // then check elvsi and then java extensions again
            String[] sa = stripElvis(s);
            s = sa[0];
            String elvis = sa[1];
            while(true) {
                boolean matched = false;
                for(IJavaExtension e: extensions) {
                    Pattern p = e.pattern1();
                    Matcher m = p.matcher(s);
                    if (m.matches()) {
                        matched = true;
                        String signature = (e instanceof IJavaExtension.VoidParameterExtension) ? null : m.group(1);
                        m = e.pattern2().matcher(s);
                        s = m.replaceAll("");
                        allMatched.push(new Pair(e, signature));
                    }
                }
                if (!matched) break;
            }
            while (!allMatched.empty()){
                // process inner elvis expression
                s = processElvis(s);
                Pair p = allMatched.pop();
                s = p.extension.extend(s, p.signature);
            }
            if (!S.isEmpty(elvis)) {
                // process outer elvis expression
                elvis = elvis.replaceFirst("^\\s*\\?\\s*:\\s*", "");
                s = String.format("((null == %1$s) ? %2$s : %1$s)", s, elvis);
            }
        }
        if (outerBracketsStripped) {
            s = String.format("(%s)", s);
        }
        s = compact(s);
        for (IExpressionProcessor p: engine.getExtensionManager().expressionProcessors()) {
            String result = p.process(s, this);
            if (null != result) {
                // remove line breaks so that we can easily handle line numbers
                return S.removeAllLineBreaks(result);
            }
        }
        return s;
    }

    public Token ptline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p("\t").p(s);
        pline();
        return this;
    }

    public Token p2tline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p("\t\t").p(s);
        pline();
        return this;
    }

    public Token p3tline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p("\t\t\t").p(s);
        pline();
        return this;
    }

    public Token p4tline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p("\t\t\t\t").p(s);
        pline();
        return this;
    }

    public Token p5tline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p("\t\t\t\t\t").p(s);
        pline();
        return this;
    }

    public Token pline(String msg, Object ... args) {
        String s = String.format(msg, args);
        p(s);
        pline();
        return this;
    }

    public Token pline() {
        p(" //line: ").pn(line);
        return this;
    }

    protected void pp(String s) {
        s = compact(s);
        if (compactMode()) {
            s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        } else {
            s = s.replaceAll("(\\r?\\n)", "\\\\n").replaceAll("\"", "\\\\\"");
        }
        p("p(\"").p(s).p("\");");
        pline();
    }

    private static String compact_(String s) {
        String[] lines = s.split("[\\r\\n]+");
        TextBuilder tb = new TextBuilder();
        int i = 0;
        boolean startsWithSpace = s.startsWith(" ") || s.startsWith("\t");
        boolean endsWithSpace = s.endsWith(" ") || s.endsWith("\t");
        if (startsWithSpace) tb.p(" ");
        for (String line: lines) {
            if (i++ > 0) tb.p("\n");
            line = line.replaceAll("[ \t]+", " ").trim();
            tb.p(line);
        }
        if (endsWithSpace) tb.p(" ");
        return tb.toString();
    }

    protected String compact(String s) {
        return compactMode() ? compact_(s) : s;
    }
    
    public static String processRythmExpression(String s, RythmEngine eninge) {
        Token token = new Token(s, (IContext)null);
        return token.processExtensions(eninge);
    }

    public static void main(String[] args) {
        String s = "try {_.bindModel(_.campaign.designer.componentPanel.data, \n$('#panDesigner .design-panel.component')[0]);}";
        System.out.println(compact_(s));
    }

    public static void main1(String[] args) {
//        Token t = new Token("(S?.escape())", null);
//        t.outputExpression();
//        System.out.println(t.out());

//        Pattern p = Pattern.compile(String.format(".*\\.%s\\s*\\((\\s*%s?\\s*)\\)\\s*$", "format", ".*"));
//        Pattern p2 = Pattern.compile(String.format("\\.%s\\s*\\((\\s*%s?\\s*)\\)\\s*$", "format", ".*"));
//        String s = "((a.b?:far).format(\"EEEE',' MMMM dd',' yyyy\", 10, true))";
//        Matcher m = p.matcher(s);
//        if (m.matches()) {
//            String signature = m.group(1);
//            m = p2.matcher(s);
//            s = m.replaceAll("");
//            System.out.println("s: " + s + ", signature: " + signature);
//        }

//        Pattern p = Pattern.compile(String.format(".*(?<!%s)\\.%s\\s*\\((\\s*%s?\\s*)\\)\\s*$", "JavaExtensions", "format", ".*"));
//        String s = "JavaExtensions.format(abc, \"EEE\")";
//        Matcher m = p.matcher(s);
//        if (m.matches()) {
//            System.out.println(m.group());
//            System.out.println(m.group(1));
//            //System.out.println(m.group(2));
//        }
//
//        TextBuilder tb = new TextBuilder();
//        tb.p(com.greenlaw110.rythm.utils.S.escape("<abcd>"));
//        System.out.println(tb.toString());
//        String waiveName = "S";
//        String methodName = "format";
//        String s = "abc?.format()";
//        Pattern pattern1 = Pattern.compile(String.format(".*(?<!%s)(\\?\\.%s|\\.%s)\\s*\\(\\s*\\)\\s*$", waiveName, methodName, methodName));
//        Matcher m = pattern1.matcher(s);
//        if (m.matches()) {
//            System.out.println(m.group());
//            System.out.println(m.group(1));
            //System.out.println(m.group(2));
            //System.out.println(m.group(3));
//        }
    }
}
