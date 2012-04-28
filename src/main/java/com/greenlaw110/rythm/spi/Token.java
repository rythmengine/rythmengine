package com.greenlaw110.rythm.spi;

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
    protected static final ILogger logger = Logger.get(Token.class);
    protected String s;
    protected IContext ctx;
    protected int line;
    protected boolean disableCompactMode = false;
    protected boolean compactMode() {
        boolean mode = null == ctx ? true : ctx.getEngine().compactMode();
        return !disableCompactMode && mode;
    }
    /*
     * Indicate whether token parse is good
     */
    private boolean ok = true;

    protected final void fail() {
        ok = false;
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
    static {
        String[] sa = {
            "raw", "escape", "escapeHtml", "escapeJavaScript", "escapeCsv", "escapeXml", "escapeJava", "camelCase", "capAll", "capFirst", "slugify", "noAccents"
        };
        for (String s: sa) {
            addExtension(new IJavaExtension.VoidParameterExtension("S", s));
        }
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
    protected final void outputExpression() {
        if (S.isEmpty(s)) return;
        if (null != ctx && !ctx.getCodeBuilder().engine.enableJavaExtensions()) {
            p("\ntry{pe(").p(s).p(");} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
            return;
        }
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
        boolean processed = false;
        for (IExpressionProcessor p: ctx.getEngine().getExtensionManager().expressionProcessors()) {
            if (p.process(s, this)) {
                processed = true;
                break;
            }
        }
        if (!processed) {
            p("\ntry{pe(").p(s).p(");} catch (RuntimeException e) {handleTemplateExecutionException(e);} ");
            pline();
        }
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
        s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        p("p(\"").p(s).p("\");");
        pline();
    }

    private static String compact_(String s) {
        String[] lines = s.split("[\\r\\n]+");
        TextBuilder tb = new TextBuilder();
        int i = 0;
        for (String line: lines) {
            if (i++ > 0) tb.p(" ");
            line = line.replaceAll("[ \t]+", " ");
            tb.p(line);
            if (line.contains("//")) tb.p("\n");
        }
        return tb.toString();
    }

    protected String compact(String s) {
        return compactMode() ? compact_(s) : s;
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
