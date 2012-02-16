package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends TextBuilder {
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
        super(null == context ? null : context.getCodeBuilder());
        this.s = s;
        ctx = context;
        line = (null == context) ? -1 : context.currentLine() - 1;
    }
    
    public Token(String s, IContext context, boolean disableCompactMode) {
        this(s, context);
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
        pp(s);
    }

    static interface IExtension {
        String extend(String s, String signature);
        Pattern pattern1();
        Pattern pattern2();
    }
    
    private static class VoidSignatureExtension implements IExtension {
        private String methodName = null;
        private String fullMethodName = null;
        private Pattern pattern1 = null;
        private Pattern pattern2 = null;
        public VoidSignatureExtension(String name) {
            this(name, String.format("com.greenlaw110.rythm.utils.S.%s", name));
        }
        public VoidSignatureExtension(String name, String fullName) {
            methodName = name;
            fullMethodName = fullName;
            pattern1 = Pattern.compile(String.format(".*\\.%s\\s*\\(\\s*\\)\\s*$", methodName));
            pattern2 = Pattern.compile(String.format("\\.%s\\s*\\(\\s*\\)\\s*$", methodName));
        }

        @Override
        public Pattern pattern1() {
            return pattern1;
        }
        
        public Pattern pattern2() {
            return pattern2;
        }

        @Override
        public String extend(String s, String signature) {
            return String.format("%s(%s)", fullMethodName, s);
        }
    }
    
    private static class SignatureExtension implements IExtension {
        private String methodName = null;
        private String signature = null;
        private String fullMethodName = null;
        private Pattern pattern1 = null;
        private Pattern pattern2 = null;
        public SignatureExtension(String name, String signature) {
            this(name, signature, String.format("com.greenlaw110.rythm.utils.S.%s", name));
        }
        public SignatureExtension(String name, String signature, String fullName) {
            methodName = name;
            this.signature = signature;
            fullMethodName = fullName;
            pattern1 = Pattern.compile(String.format(".*\\.%s\\s*\\((\\s*%s\\s*)\\)\\s*$", methodName, signature));
            pattern2 = Pattern.compile(String.format("\\.%s\\s*\\((\\s*%s\\s*)\\)\\s*$", methodName, signature));
        }

        @Override
        public Pattern pattern1() {
            return pattern1;
        }

        @Override
        public Pattern pattern2() {
            return pattern2;
        }

        @Override
        public String extend(String s, String signature) {
            return String.format("%s(%s, %s)", fullMethodName, s, signature);
        }
    }
    
    static final List<IExtension> extensions = new ArrayList<IExtension>();
    static final void addExtension(IExtension extension) {
        extensions.add(extension);
    }
    static {
        String[] sa = {
            "escape", "escapeHtml", "escapeJavaScript", "escapeCsv", "escapeXml", "capitalizeWords", "shrinkSpace"
        };
        for (String s: sa) {
            addExtension(new VoidSignatureExtension(s));
        }
        addExtension(new SignatureExtension("pad",  "[0-9]+"));
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
            p("\np(").p(s).p(");");
            return;
        }
        String s0 = s;
        boolean outerBracketsStripped = false;
        s = stripOuterBrackets(s);
        outerBracketsStripped = s != s0;
        class Pair {
            IExtension extension;
            String signature;
            Pair(IExtension e, String s) {
                extension = e;
                signature = s;
            }
        }
        Stack<Pair> allMatched = new Stack<Pair>();
        // try parse java extension first
        while(true) {
            boolean matched = false;
            for(IExtension e: extensions) {
                Pattern p = e.pattern1();
                Matcher m = p.matcher(s);
                if (m.matches()) {
                    matched = true;
                    String signature = (e instanceof VoidSignatureExtension) ? null : m.group(1);
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
            System.out.println(s);
            s = processElvis(s);
            System.out.println(s);
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
                for(IExtension e: extensions) {
                    Pattern p = e.pattern1();
                    Matcher m = p.matcher(s);
                    if (m.matches()) {
                        matched = true;
                        String signature = (e instanceof VoidSignatureExtension) ? null : m.group(1);
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
        if (compactMode()) {
            s = s.replaceAll("[\\s\\r\\n\\t]+", " ");
        }
        p("\np(").p(s).p(");");
    }

    private void pp(String s) {
        if (compactMode()) {
            s = s.replaceAll("[\\s\\r\\n\\t]+", " ");
        }
        s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        p("p(\"").p(s).p("\"); //line: ").p(line).p("\n");
    }

    public static void main(String[] args) {
        Token t = new Token("((a.b?:far).escape())", null);
        t.outputExpression();
        System.out.println(t.out());
    }
}