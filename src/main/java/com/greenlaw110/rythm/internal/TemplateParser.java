package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IBlockHandler;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.utils.TextBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

public class TemplateParser implements IContext {
    private final ILogger logger = Logger.get(TemplateParser.class);
    private final CodeBuilder cb;
    private String template;
    private int totalLines;
    int cursor = 0;

    public TemplateParser(CodeBuilder cb) {
        this.template = cb.template();
        totalLines = StringUtils.countMatches(template, "\n") + 1;
        this.cb = cb;
    }

    public static class ExitInstruction extends RuntimeException {
    }

    void parse() {
        TemplateTokenizer tt = new TemplateTokenizer(template, this);
        try {
            for (TextBuilder builder: tt) {
                cb.addBuilder(builder);
            }
        } catch (ExitInstruction e) {
            // ignore, just break the parsing process
        }
    }

    @Override
    public TemplateClass getTemplateClass() {
        return cb.getTemplateClass();
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

    @Override
    public String getTemplateSource(int start, int end) {
        return template.substring(start, end);
    }

    private Stack<IBlockHandler> blocks = new Stack<IBlockHandler>();

    @Override
    public void openBlock(IBlockHandler bh) {
        bh.openBlock();
        blocks.push(bh);
    }

    @Override
    public IBlockHandler currentBlock() {
        return blocks.isEmpty() ? null : blocks.peek();
    }

    @Override
    public String closeBlock() throws ParseException {
        if (blocks.isEmpty()) throw new ParseException(cb.getTemplateClass(), currentLine(), "No open block found");
        IBlockHandler bh = blocks.pop();
        return null == bh ? "" : bh.closeBlock();
    }

    @Override
    public int currentLine() {
        if (null == template) return -1; // for testing purpose only
        if (cursor >= template.length()) return totalLines;
        //return template.substring(0, cursor).split("(\\r\\n|\\n|\\r)").length;
        return StringUtils.countMatches(template.substring(0, cursor), "\n") + 1;
    }

    @Override
    public RythmEngine getEngine() {
        return cb.engine;
    }

    private Stack<Break> breakStack = new Stack<Break>();

    @Override
    public void pushBreak(Break b) {
        breakStack.push(b);
    }

    @Override
    public Break peekBreak() {
        if (breakStack.empty()) return null;
        return breakStack.peek();
    }

    @Override
    public Break popBreak() {
        if (breakStack.empty()) return null;
        return breakStack.pop();
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
