package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.spi.IContext;

public class ForEachCodeToken extends BlockCodeToken {

    private String type;
    private String varname;
    private String iterable;
    private int openPos;
    private int closePos;

    /**
     *
     * @each String [str]: myStrList @
     *         ^     ^       ^        ^
     *         |     |       |        |
     *        type varname  iterable endloop
     *
     * @param type
     * @param varname
     * @param iterable
     * @param context
     */
    public ForEachCodeToken(String type, String varname, String iterable, IContext context) {
        super(null, context);
        if (null == type || null == iterable) throw new NullPointerException();
        this.type = type;
        this.varname = null == varname ? "_" : varname;
        this.iterable = iterable;
        line--;
        openPos = context.cursor();
        ctx.pushBreak(IContext.Break.RETURN);
        ctx.pushContinue(IContext.Continue.RETURN);
    }

    @Override
    public void output() {
        String prefix = "_".equals(varname) ? "" : varname;
        String curClassName = ctx.getCodeBuilder().className();
        int bodySize = closePos - openPos;
        //p("\nnew com.greenlaw110.rythm.runtime.Each(").p(curClassName).p(".this).render(").p(iterable)
        p("com.greenlaw110.rythm.runtime.Each.INSTANCE.render(").p(iterable);
        p(", new com.greenlaw110.rythm.runtime.Each.Looper<").p(type).p(">(");
        p(curClassName).p(".this,").p(bodySize).p("){");
        pline();
        pt("public boolean render(final ");
        p(type).p(" ").p(varname).p(", final int ").p(prefix).p("_size, final int ").p(prefix).p("_index, final boolean ");
        p(prefix).p("_isOdd, final String ").p(prefix).p("_parity, final boolean ");
        p(prefix).p("_isFirst, final boolean ").p(prefix).p("_isLast) { ");
        pline();
//        p("\ncom.greenlaw110.rythm.runtime.Each.INSTANCE.render(").p(iterable)
//            .p(", new com.greenlaw110.rythm.runtime.Each.Looper<").p(type).p(">(").p(curClassName).p(".this, ").p(bodySize).p(") { //lines:").p(ctx.currentLine()).p("\n\tpublic boolean render(final ")
//            .p(type).p(" ").p(varname).p(", final int ").p(prefix).p("_size, final int ").p(prefix).p("_index, final boolean ")
//            .p(prefix).p("_isOdd, final String ").p(prefix).p("_parity, final boolean ")
//            .p(prefix).p("_isFirst, final boolean ").p(prefix).p("_isLast) { //lines: ").pn(ctx.currentLine());
    }

    @Override
    public String closeBlock() {
        ctx.popBreak();
        closePos = ctx.cursor();
        return "\n\t return true;\n\t}});";
    }
}
