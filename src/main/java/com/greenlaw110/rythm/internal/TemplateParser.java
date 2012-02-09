package com.greenlaw110.rythm.internal;

import java.util.Stack;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.utils.TextBuilder;
import org.apache.commons.lang3.StringUtils;

public class TemplateParser implements IContext {
    private final CodeBuilder cb;
    private String template;
    private int totalLines;
    int cursor = 0;
    
    public TemplateParser(CodeBuilder cb) {
        this.template = cb.template();
        totalLines = StringUtils.countMatches(template, "\n") + 1;
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
        return cb.engine.getDialectManager().get();
    }

    public void setDialect(String dialect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemain() {
        return cursor < template.length() ? template.substring(cursor) : "";
    }

    @Override
    public int cursor() {
        return cursor;
    }

    @Override
    public boolean hasRemain() {
        return cursor < template.length();
    }

    @Override
    public char peek() {
        if (!hasRemain()) return '\u0000';
        return template.charAt(cursor);
    }

    @Override
    public char pop() {
        if (!hasRemain()) throw new ArrayIndexOutOfBoundsException();
        char c = template.charAt(cursor);
        step(1);
        return c;
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
        if (blocks.isEmpty()) throw new ParseException(currentLine(), "No open block found");
        IBlockHandler bh = blocks.pop();
        return null == bh ? "" : bh.closeBlock();
    }

    @Override
    public int currentLine() {
        if (cursor >= template.length()) return totalLines;
        //return template.substring(0, cursor).split("(\\r\\n|\\n|\\r)").length;
        return StringUtils.countMatches(template.substring(0, cursor), "\n") + 1;
    }
    
    /* this constructor is just for testing purpose */
    private TemplateParser(String s) {
        template = s;
        totalLines = template.split("(\\r\\n|\\n|\\r)").length + 1;
        cb = null;
    }
    public static void main(String[] args) {
        TemplateParser tp = new TemplateParser("\nHello \n\r\nworld!");
        System.out.println(tp.totalLines);
        System.out.println(tp.currentLine());
        tp.step(5);
        System.out.println("5 steps ahead");
        System.out.println(tp.currentLine());
        System.out.println(tp.getRemain());
        tp.step(4);
        System.out.println("4 steps ahead");
        System.out.println(tp.currentLine());
        System.out.println(tp.getRemain());
    }
}
