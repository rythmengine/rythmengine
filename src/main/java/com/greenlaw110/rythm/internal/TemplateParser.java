package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
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

    public static class ExitInstruction extends FastRuntimeException {
    }

    private static abstract class RewindableException extends ParseException {
        public RewindableException(IContext ctx, String msg, Object... args) {
            super(ctx.getEngine(), ctx.getTemplateClass(), ctx.currentLine(), msg, args);
        }
    }

    public static class NoFreeLoopException extends RewindableException {
        public NoFreeLoopException(IContext ctx) {
            super(ctx, "Free loop style (@for(;;)) not allowed in current dialect[%s]", ctx.getDialect());
        }
    }
    
    public static class ScriptingDisabledException extends RewindableException {
        public ScriptingDisabledException(IContext ctx) {
            super(ctx, "Scripting not allowed in current dialect[%s]", ctx.getDialect());
        }
    }
    
    public static class ComplexExpressionException extends RewindableException {
        public ComplexExpressionException(IContext ctx) {
            super(ctx, "Complex expression not allowed in current dialect[%s]", ctx.getDialect());
        }
    }

    void parse() {
        DialectManager dm = cb.engine.getDialectManager();
        while (true) {
            dm.beginParse(this);
            cursor = 0;
            cb.rewind();
            try {
                TemplateTokenizer tt = new TemplateTokenizer(this);
                for (TextBuilder builder : tt) {
                    cb.addBuilder(builder);
                }
                dm.endParse(this);
                break;
            } catch (ExitInstruction e) {
                dm.endParse(this);
                break;
            } catch (RewindableException e) {
                if (null != cb.requiredDialect) {
                    dm.endParse(this);
                    throw e;
                }
                continue;
            } catch (RuntimeException e) {
                dm.endParse(this);
                throw e;
            }
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

    private IDialect dialect = null;

    public IDialect getDialect() {
        return dialect;
    }

    public void setDialect(IDialect dialect) {
        this.dialect = dialect;
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
        if (blocks.isEmpty())
            throw new ParseException(getEngine(), cb.getTemplateClass(), currentLine(), "No open block found");
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

    @Override
    public boolean compactMode() {
        if (!compactStack.empty()) return compactStack.peek();
        return getEngine().compactMode();
    }

    private Stack<Boolean> compactStack = new Stack<Boolean>();

    @Override
    public void pushCompact(Boolean compact) {
        compactStack.push(compact);
    }

    @Override
    public Boolean peekCompact() {
        if (compactStack.empty()) return null;
        return compactStack.peek();
    }

    @Override
    public Boolean popCompact() {
        if (compactStack.empty()) return null;
        return compactStack.pop();
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

    private Stack<Continue> continueStack = new Stack<Continue>();

    @Override
    public void pushContinue(Continue b) {
        continueStack.push(b);
    }

    @Override
    public Continue peekContinue() {
        if (continueStack.empty()) return null;
        return continueStack.peek();
    }

    @Override
    public Continue popContinue() {
        if (continueStack.empty()) return null;
        return continueStack.pop();
    }

    @Override
    public boolean insideBody() {
        if (!inBodyStack.empty()) return inBodyStack.peek();
        return getEngine().compactMode();
    }

    private Stack<Boolean> inBodyStack = new Stack<Boolean>();

    @Override
    public void pushInsideBody(Boolean b) {
        inBodyStack.push(b);
    }

    @Override
    public Boolean peekInsideBody() {
        if (inBodyStack.empty()) return false;
        return inBodyStack.peek();
    }

    @Override
    public Boolean popInsideBody() {
        if (inBodyStack.empty()) return null;
        return inBodyStack.pop();
    }

    @Override
    public boolean insideBody2() {
        if (!inBodyStack2.empty()) return inBodyStack2.peek();
        return getEngine().compactMode();
    }

    private Stack<Boolean> inBodyStack2 = new Stack<Boolean>();

    @Override
    public void pushInsideBody2(Boolean b) {
        inBodyStack2.push(b);
    }

    @Override
    public Boolean peekInsideBody2() {
        if (inBodyStack2.empty()) return false;
        return inBodyStack2.peek();
    }

    @Override
    public Boolean popInsideBody2() {
        if (inBodyStack2.empty()) return null;
        return inBodyStack2.pop();
    }

    public void shutdown() {
        dialect = null;
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
