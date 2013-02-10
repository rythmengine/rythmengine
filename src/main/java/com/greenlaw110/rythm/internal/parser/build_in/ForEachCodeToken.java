package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.utils.S;
import com.stevesoft.pat.Regex;

public class ForEachCodeToken extends BlockCodeToken {

    private String type;
    private String varname;
    private String iterable;
    private int openPos;
    private int closePos;

    /**
     * @param type
     * @param varname
     * @param iterable
     * @param context
     * @each String [str]: myStrList @
     * ^     ^       ^        ^
     * |     |       |        |
     * type varname  iterable endloop
     */
    public ForEachCodeToken(String type, String varname, String iterable, IContext context) {
        super(null, context);
        if (null == iterable) throw new NullPointerException();
        this.type = ObjectType(type);
        this.varname = null == varname ? "_" : varname;
        if (iterable.contains("..") || iterable.contains(" to ") || iterable.contains(" till ")) {
            iterable = "com.greenlaw110.rythm.utils.Range.valueOf(\"" + iterable + "\")";
        }
        this.iterable = iterable;
        openPos = context.cursor();
        IContext ctx = context;
        ctx.pushBreak(IContext.Break.BREAK);
        ctx.pushContinue(IContext.Continue.CONTINUE);
        CodeBuilder cb = context.getCodeBuilder();
        if (S.isEmpty(type)) {
            String itrType = cb.getRenderArgType(iterable);
            if (null != itrType) {
                Regex r = new Regex(".*((?@<>))");
                if (r.search(itrType)) {
                    type = r.stringMatched(1);
                }
                this.type = S.strip(type, "<", ">");
                boolean key = iterable.endsWith("keySet()");
                boolean val = iterable.endsWith("values()");
                if (key || val) {
                    r = new Regex("([a-zA-Z0-9\\[\\]_]+(?@<>)?)\\s*\\,\\s*([a-zA-Z0-9\\[\\]_]+(?@<>)?)");
                    if (r.search(this.type)) {
                        if (key) this.type = r.stringMatched(1);
                        else this.type = r.stringMatched(2);
                    } else {
                        throw new ParseException(ctx.getEngine(), ctx.getTemplateClass(), line, "Invalid for loop iterator type declaration: %s", itrType);
                    }
                }
                if (S.isEqual(this.type, this.varname)) this.varname = "_";
            } else {
                this.type = "Object";
            }
        }
        if (ctx.getDialect() instanceof BasicRythm) {
            ExpressionParser.assertBasic(iterable, context);
            context.getCodeBuilder().addRenderArgsIfNotDeclared(line, "Iterable<?>", iterable);
        }
    }

    private String ObjectType(String type) {
        if (null == type) return "Object";
        if ("int".equals(type)) return "Integer";
        if ("float".equals(type)) return "Float";
        if ("double".equals(type)) return "Double";
        if ("boolean".equals(type)) return "Boolean";
        if ("char".equals(type)) return "Character";
        if ("long".equals(type)) return "Long";
        if ("byte".equals(type)) return "Byte";
        if ("short".equals(type)) return "Integer";
        return type;
    }

    @Override
    public void output() {
        String prefix = "_".equals(varname) ? "" : varname + "";
        CodeBuilder cb = ctx.getCodeBuilder();
        String varId = prefix + "_index";
        String varIsOdd = prefix + "_isOdd";
        String varSize = prefix + "_size";
        String varParity = prefix + "_parity";
        String varIsFirst = prefix + "_isFirst";
        String varIsLast = prefix + "_isLast";
        String varSep = prefix + "_sep";
        String varUtils = prefix + "_utils";

        String varItr = cb.newVarName();
        p("{\n_Itr<").p(type).p("> ").p(varItr).p(" = new _Itr(").p(iterable).p(");");
        pline();
        p("int ").p(varSize).p(" = ").p(varItr).p(".size();");
        pline();
        p("if (").p(varSize).p(" > 0) {");
        pline();
        p("int ").p(varId).p(" = 0;");
        pline();
        p("for(").p(type).p(" ").p(varname).p(" : ").p(varItr).p(") {");
        pline();
        p(varId).p("++;");
        pline();
        p("boolean ").p(varIsOdd).p(" = ").p(varId).p(" % 2 == 1;");
        pline();
        p("String ").p(varParity).p(" = ").p(varIsOdd).p(" ? \"odd\" : \"even\";");
        pline();
        p("boolean ").p(varIsFirst).p(" = ").p(varId).p(" == 1;");
        pline();
        p("boolean ").p(varIsLast).p(" = ").p(varId).p(" >= ").p(varSize).p(";");
        pline();
        p("String ").p(varSep).p(" = ").p(varIsLast).p(" ? \"\" : \",\";");
        pline();
        p("com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils ").p(varUtils).p(" = new com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils(").p(varIsFirst).p(", ").p(varIsLast).p(");");
        pline();
    }

    public void output1() {
        String prefix = "_".equals(varname) ? "" : varname;
        String curClassName = ctx.getCodeBuilder().includingClassName();
        int bodySize = closePos - openPos;
        String varName = ctx.getCodeBuilder().newVarName();
        p("_Itr<").p(type).p("> ").p(varName).p(" = new _Itr(").p(iterable).p(");");
        pline();
        p("if (").p(varName).p(".size() > 0) {");
        pline();
        p("com.greenlaw110.rythm.runtime.Each.INSTANCE.render(").p(varName);
        p(", new com.greenlaw110.rythm.runtime.Each.Looper<").p(type).p(">(");
        p(curClassName).p(".this,").p(bodySize).p("){");
        pline();
        pt("public boolean render(final ");
        p(type).p(" ").p(varname).p(", final int ").p(prefix).p("_size, final int ").p(prefix).p("_index, final boolean ");
        p(prefix).p("_isOdd, final String ").p(prefix).p("_parity, final boolean ");
        p(prefix).p("_isFirst, final boolean ").p(prefix).p("_isLast, final String ").p(prefix).p("_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils ").p(prefix).p("_utils) { ");
        pline();
    }

    @Override
    public String closeBlock() {
        return "\n\t}\n}\n}\n";
    }

    public String closeBlock1() {
        ctx.popBreak();
        closePos = ctx.cursor();
        return "\n\t return true;\n\t}});\n}\n";
    }
}
