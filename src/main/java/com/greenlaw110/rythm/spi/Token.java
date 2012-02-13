package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends TextBuilder {
    protected String s;
    protected IContext ctx;
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
    
    protected final void outputExpression() {
        if (S.isEmpty(s)) return;
        if (null == ctx || !ctx.getCodeBuilder().engine.enableJavaExtensions()) {
            p("\np(").p(s).p(");");
            return;
        }
        String s0 = s;
        class Pair {
            IExtension extension;
            String signature;
            Pair(IExtension e, String s) {
                extension = e;
                signature = s;
            }
        }
        Stack<Pair> allMatched = new Stack<Pair>();
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
            Pair p = allMatched.pop();
            s = p.extension.extend(s, p.signature);
        }
        p("\np(").p(s).p(");");
    }

    private void pp(String s) {
        s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        p("\np(\"").p(s).p("\");");
    }

    public static void main(String[] args) {
        Token t = new Token("a.b.escape().pad(5)", null);
        t.outputExpression();
        System.out.println(t.out());
    }
}
