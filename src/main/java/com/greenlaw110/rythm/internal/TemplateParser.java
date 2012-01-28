package com.greenlaw110.rythm.internal;

import java.util.Stack;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.util.TextBuilder;

public class TemplateParser implements IContext {
    private final CodeBuilder cb;
    private IDialect dialect = new Rythm();
    private String template;
    int cursor = 0;
    
    public TemplateParser(CodeBuilder cb) {
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
        throw new UnsupportedOperationException();
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
    public IBlockHandler currentBlock() {
        return blocks.isEmpty() ? null : blocks.peek();
    }

    @Override
    public String closeBlock() throws ParseException {
        if (blocks.isEmpty()) throw new ParseException("No open block found"); 
        IBlockHandler bh = blocks.pop();
        return bh.closeBlock();
    }
}
