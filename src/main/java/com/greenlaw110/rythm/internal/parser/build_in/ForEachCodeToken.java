package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.spi.IContext;

public class ForEachCodeToken extends BlockCodeToken {

    private String type;
    private String varname;
    private String iterable;
    
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
    }

    @Override
    public void output() {
        String prefix = "_".equals(varname) ? "" : varname;
        String curClassName = ctx.getCodeBuilder().className();
        p("\nnew com.greenlaw110.rythm.runtime.Each(").p(curClassName).p(".this).render(").p(iterable)
            .p(", new com.greenlaw110.rythm.runtime.Each.IBody<").p(type).p(">(){\n\tpublic void render(final ")
            .p(type).p(" ").p(varname).p(", final int size, final int ").p(prefix).p("_index, final boolean ")
            .p(prefix).p("_isOdd, final String ").p(prefix).p("_parity, final boolean ")
            .p(prefix).p("_first, final boolean ").p(prefix).p("_last) {");
    }

    @Override
    public String closeBlock() {
        return "\n\t}\n});";
    }
}
