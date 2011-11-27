package com.greenlaw110.rythm.internal;

import java.util.Stack;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.dialect.IDialect;
import com.greenlaw110.rythm.internal.dialect.Japid;
import com.greenlaw110.rythm.internal.dialect.Razor;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.util.TextBuilder;

class TemplateParser implements IContext {
    private final CodeBuilder cb;
    private IDialect dialect = Razor.INSTANCE;
    private String template;
    private int cursor = 0;
    
    TemplateParser(CodeBuilder cb) {
        this.template = cb.template();
        this.cb = cb;
    }
    
    void parse() {
        TemplateTokenizer tt = new TemplateTokenizer(template, this);
        for (TextBuilder builder: tt) {
            cb.addBuilder(builder);
        }
    }
    
    @Override 
    public CodeBuilder getCodeBuilder() {
        return cb;
    }

    public IDialect getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        if ("razor".equalsIgnoreCase(dialect)) {
            this.dialect = Razor.INSTANCE;  
        } else if ("japid".equalsIgnoreCase(dialect)) {
            this.dialect = Japid.INSTANCE;
        }
    }

    @Override
    public String getRemain() {
        return cursor < template.length() ? template.substring(cursor) : "";
    }
    
    @Override
    public boolean hasRemain() {
        return cursor < template.length();
    }

    @Override
    public void step(int i) {
        cursor += i;
    }
    
    private Stack<IBlockHandler> blocks = new Stack<IBlockHandler>();

    @Override
    public void openBlock(IBlockHandler bh) {
        blocks.push(bh);
    }
    

    @Override
    public IBlockHandler currentBlock() throws ParseException {
        if (blocks.isEmpty()) throw new ParseException("No open block found"); 
        return blocks.peek();
    }

    @Override
    public void closeBlock() throws ParseException {
        if (blocks.isEmpty()) throw new ParseException("No open block found"); 
        IBlockHandler bh = blocks.pop();
        bh.closeBlock();
    }
}
