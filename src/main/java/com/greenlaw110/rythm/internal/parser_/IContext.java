package com.greenlaw110.rythm.internal.parser_;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.dialect.IDialect;


public interface IContext {
    CodeBuilder getBuilder();
    IDialect getDialect();
    void setDialect(String dialect);
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
     * Move the current cursor i steps ahead
     * @param i
     */
    void step(int i);
    
    /**
     * Notify context to enter a block
     * 
     * @param probe
     */
    void openBlock(ParserBase probe);
    
    /**
     * Get current block type
     * @return
     */
    Class<? extends ParserBase> currentBlock();
    
    /**
     * Notify context to close current block
     */
    Class<? extends ParserBase> closeBlock();
}
