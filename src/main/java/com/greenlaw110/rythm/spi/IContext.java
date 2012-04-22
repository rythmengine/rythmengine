package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;

public interface IContext {

    /**
     * Return the root source code builder
     * @return
     */
    CodeBuilder getCodeBuilder();

    /**
     * Return the remaining template string that has not parsed yet
     * @return
     */
    String getRemain();
    /**
     * Do have have remain template content to be parsed
     * @return
     */
    boolean hasRemain();
    /**
     * return the first remain character without moving cursor
     */
    char peek();
    /**
     * return the first remain character and move the cursor one step
     */
    char pop();
    /**
     * Move the current cursor i steps ahead
     * @param i
     */
    void step(int i);

    /**
     * Return any segment of template source
     * @param start
     * @param end
     * @return
     */
    String getTemplateSource(int start, int end);

    /**
     * Notify context to enter a block
     *
     * @param bh the block handler
     */
    void openBlock(IBlockHandler bh);

    /**
     * Return current block handleer
     *
     * @return
     * @throws ParseException
     */
    IBlockHandler currentBlock();

    /**
     * Notify context to close current block and return
     * the block close carets
     *
     * @throws ParseException
     */
    String closeBlock() throws ParseException;

    void setDialect(String dialect);
    IDialect getDialect();

    /**
     * Return current line number
     * @return
     */
    int currentLine();

    int cursor();

    TemplateClass getTemplateClass();

    RythmEngine getEngine();

    public static enum Break {
        BREAK("break;"), RETURN("return false;");
        private String statement;
        private Break(String statement) {
            this.statement = statement;
        }
        public String getStatement() {
            return statement;
        }
    }

    public static enum Continue {
        CONTINUE("continue;"), RETURN("return true;");
        private String statement;
        private Continue(String statement) {
            this.statement = statement;
        }
        public String getStatement() {
            return statement;
        }
    }

    void pushBreak(Break b);
    Break peekBreak();
    Break popBreak();

    void pushContinue(Continue c);
    Continue peekContinue();
    Continue popContinue();
}
