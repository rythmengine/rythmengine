/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.internal.parser.build_in.BlockToken;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;
import org.rythmengine.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends TextBuilder {

    private Token() {}
    public static final Token EMPTY_TOKEN = new Token();
    // for https://github.com/greenlaw110/Rythm/issues/146
    public static final Token EMPTY_TOKEN2 = new Token(); 

    public static class StringToken extends Token {
        public String constId = null;

        public StringToken(String s, IContext ctx) {
            super(s, ctx);
        }

        public StringToken(String s, IContext context, boolean disableCompactMode) {
            super(s, context, disableCompactMode);
        }

        public StringToken mergeWith(BlockToken.LiteralBlock block) {
            StringToken merged = new StringToken(s, ctx, disableCompactMode);
            merged.line = block.line;
            merged.s += "{";
            return merged;
        }

        public StringToken mergeWith(StringToken st) {
            StringToken merged = new StringToken(s, ctx, disableCompactMode);
            merged.line = st.line;
            String s = st.s;
            s = st.compact(s);
            merged.s += s;
            return merged;
        }

        @Override
        public boolean removeLeadingLineBreak() {
            //String s0 = s;
            s = s.replaceFirst("^[ \\t\\x0B\\f]*\\n", "");
            return true;
        }

        public int getLineNo() {
            return line;
        }

        public String s() {
            return s;
        }

        @Override
        public String toString() {
            return s;
        }

        @Override
        protected void output() {
            RythmEngine.OutputMode mode = RythmEngine.outputMode();
            if (mode.writeOutput()) {
                if (null == constId) return;
                p("p(").p(constId).p(");");
                pline();
            } else {
                super.output();
            }
        }

        @Override
        public int hashCode() {
            return s.hashCode() + (compactMode() ? 1 : -1);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof StringToken) {
                StringToken st = (StringToken) obj;
                return st.compactMode() == compactMode() && st.s.equals(s);
            }
            return false;
        }
        
        public boolean empty() {
            return S.empty(s);
        }
    }

    protected static final ILogger logger = Logger.get(Token.class);
    protected String s;
    protected IContext ctx;
    protected int line;
    protected boolean disableCompactMode = false;
    public boolean removeNextLineBreak = false;
    
    public boolean removeLeadingLineBreak() {
        return false;
    }

    protected boolean compactMode() {
        if (disableCompactMode) return false;
        return (null == ctx ? true : ctx.compactMode());
    }

    private RythmEngine engine = null;
    private Iterable<IJavaExtension> javaExtensions = null;
    private boolean transformEnabled = true;
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
        this.s = checkDynaExp(s);
        line = -1;
        this.disableCompactMode = disableCompactMode;
        //TODO: dangerous engine assignment here. only called by AppendXXToken in AutoToStringCodeBuilder
        this.engine = Rythm.engine();
        this.javaExtensions = engine.extensionManager().javaExtensions();
        RythmConfiguration conf = engine.conf();
        this.transformEnabled = conf.transformEnabled();
    }

    public Token(String s, IContext context) {
        this(s, context, false);
    }

    public Token(String s, IContext context, boolean disableCompactMode) {
        super(null == context ? null : context.getCodeBuilder());
        this.s = s;
        ctx = context;
        line = (null == context) ? -1 : context.currentLine();
        this.engine = null == ctx ? Rythm.engine() : ctx.getEngine();
        this.javaExtensions = engine.extensionManager().javaExtensions();
        this.disableCompactMode = disableCompactMode;
        RythmConfiguration conf = engine.conf();
        this.transformEnabled = conf.transformEnabled();
    }
    
    public boolean test(String line) {
        return true;
    }

    public boolean isOk() {
        return ok;
    }

    public final Token build() {
        if (ok) output();
        else {
            pp(s);
        }
        return this;
    }

    public final Token build(IContext includeCtx) {
        IContext ctx0 = ctx;
        ctx = includeCtx;
        try {
            build();
        } finally {
            ctx = ctx0;
        }
        return this;
    }

    protected void output() {
        if (null == s || "".equals(s)) return;
        pp(s);
    }

    private static final Regex R_ = new Regex("^\\s*(?@())\\s*$");

    /**
     * strip the outer brackets of the given string s
     * 
     * @param s
     * @return - the stripped string
     */
    private static String stripOuterBrackets(String s) {
        try {
            if (S.isEmpty(s))
                return s;
            if (R_.search(s)) {
                // strip out the outer brackets
                s = R_.stringMatched();
                s = s.substring(1);
                s = s.substring(0, s.length() - 1);
            }
        } catch (RuntimeException re) {
            // this unfortunately happens - so at least make it debuggable
            throw re;
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
            return new String[]{s0, s1};
        } else {
            return new String[]{s, ""};
        }
    }

    private static String processElvis(String s) {
        if (S.isEmpty(s)) return s;
        String[] sa = stripElvis(s);
        s = sa[0];
        String elvis = sa[1];
        if (S.isEmpty(elvis)) return s;
        elvis = elvis.replaceFirst("^\\s*\\?\\s*:\\s*", "");
        return String.format("((__isDefVal(%1$s)) ? %2$s : %1$s)", s, elvis);
    }

    protected final void outputExpression(List<String> nullValueTester) {
        int size = nullValueTester.size();
        for (String s : nullValueTester) {
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
        String s = processExtensions(false);
        if (needsPrint) p("\ntry{pe(").p(s).p(");} catch (RuntimeException e) {__handleTemplateExecutionException(e);} ");
        else p("\ntry{").p(s).p(";} catch (RuntimeException e) {__handleTemplateExecutionException(e);} ");
        pline();
    }
    
    private boolean dynaExp = false;
    
    private String evalStr(String s) {
        if (!dynaExp) return s;
        return "__eval(\"" + S.escapeJava(s) + "\")"; 
    }
    
    private String checkDynaExp(String s) {
        if (S.empty(s)) return s;
        boolean b = (s.endsWith("@"));
        if (b) {
            dynaExp = true;
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    private String processExtensions(boolean stripExtensions) {
        if (!transformEnabled) return evalStr(s);
        RythmEngine engine = this.engine;
        String s0 = s;
        boolean outerBracketsStripped;
        s = stripOuterBrackets(s);
        s = checkDynaExp(s);
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
        while (true) {
            boolean matched = false;
            for (IJavaExtension e : javaExtensions) {
                Pattern p = e.pattern1();
                Matcher m = p.matcher(s);
                if (m.matches()) {
                    matched = true;
                    String signature = null;
                    if (!(e instanceof IJavaExtension.VoidParameterExtension)) {
                        signature = m.group(1);
                        if (null == signature) {
                            signature = m.group(2);
                        }
                    }
                    
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
            s = evalStr(s);
            while (!allMatched.empty()) {
                Pair p = allMatched.pop();
                if (!stripExtensions) {
                    s = p.extension.extend(s, p.signature);
                }
            }
        } else {
            // then check elvsi and then java extensions again
            String[] sa = stripElvis(s);
            s = sa[0];
            String elvis = sa[1];
            while (true) {
                boolean matched = false;
                for (IJavaExtension e : javaExtensions) {
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
            s = evalStr(s);
            while (!stripExtensions && !allMatched.empty()) {
                // process inner elvis expression
                s = processElvis(s);
                Pair p = allMatched.pop();
                s = p.extension.extend(s, p.signature);
            }
            if (!S.isEmpty(elvis)) {
                // process outer elvis expression
                elvis = elvis.replaceFirst("^\\s*\\?\\s*:\\s*", "");
                s = String.format("((__isDefVal(%1$s)) ? %2$s : %1$s)", s, elvis);
            }
        }
        if (outerBracketsStripped) {
            s = String.format("(%s)", s);
        }
        s = compact(s);
        for (IExpressionProcessor p : engine.extensionManager().expressionProcessors()) {
            String result = p.process(s, this);
            if (null != result) {
                // remove line breaks so that we can easily handle line numbers
                return S.removeAllLineBreaks(result);
            }
        }
        return s;
    }

    public Token ptline(String msg, Object... args) {
        String s = String.format(msg, args);
        p("\t").p(s);
        pline();
        return this;
    }

    public Token p2tline(String msg, Object... args) {
        String s = String.format(msg, args);
        p("\t\t").p(s);
        pline();
        return this;
    }

    public Token p3tline(String msg, Object... args) {
        String s = String.format(msg, args);
        p("\t\t\t").p(s);
        pline();
        return this;
    }

    public Token p4tline(String msg, Object... args) {
        String s = String.format(msg, args);
        p("\t\t\t\t").p(s);
        pline();
        return this;
    }

    public Token p5tline(String msg, Object... args) {
        String s = String.format(msg, args);
        p("\t\t\t\t\t").p(s);
        pline();
        return this;
    }

    public Token pline(String msg, Object... args) {
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

    public Token clone(TextBuilder caller) {
        return (Token)super.clone(caller);
    }

    private static final Pattern P_C1 = Pattern.compile("\\n+", Pattern.DOTALL);
    private static final Pattern P_C2 = Pattern.compile("[ \\t\\x0B\\f]+", Pattern.DOTALL);
    private static final Pattern P_C3 = Pattern.compile("[ \\t\\x0B\\f]+\\n", Pattern.DOTALL);
    private static final Pattern P_C4 = Pattern.compile("\\n[ \\t\\x0B\\f]+", Pattern.DOTALL);
    private static String compact_(String s) {
        if (s.matches("(\\n\\r|\\r\\n|[\\r\\n])+")) {
            return "\n";
        }
        Matcher m = P_C1.matcher(s);
        s = m.replaceAll("\n");
        m = P_C2.matcher(s);
        s = m.replaceAll(" ");
        m = P_C3.matcher(s);
        s = m.replaceAll("\n");
        m = P_C4.matcher(s);
        s = m.replaceAll("\n");
        return s;
//        String[] lines = s.split("[\\r\\n]+");
//        if (0 == lines.length) return "";
//        TextBuilder tb = new TextBuilder();
//        int i = 0;
//        boolean startsWithSpace = s.startsWith(" ") || s.startsWith("\t");
//        boolean endsWithSpace = s.endsWith(" ") || s.endsWith("\t");
//        if (startsWithSpace) tb.p(" ");
//        for (String line : lines) {
//            if (i++ > 0) tb.p("\n");
//            line = line.replaceAll("[ \t]+", " ").trim();
//            tb.p(line);
//        }
//        if (endsWithSpace) tb.p(" ");
//        return tb.toString();
    }
    
    private static String processLineBreaks_(String s) {
        return s;
    }
    
    public void compact() {
        s = compact(s);
    }

    protected String compact(String s) {
        return compactMode() ? compact_(s) : processLineBreaks_(s);
    }

    public static String processRythmExpression(String s, IContext ctx) {
        Token token = new Token(s, ctx);
        return token.processExtensions(false);
    }
    
    public static String stripJavaExtension(String s, IContext ctx) {
        Token token = new Token(s, ctx);
        return token.processExtensions(true);
    }
}
