package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;

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
     * Move the current cursor i steps ahead
     * @param i
     */
    void step(int i);
    
    /**
     * Notify context to enter a block
     * 
     * @param parser
     */
    void openBlock(IBlockHandler bh);
    
    /**
     * Return current block handleer
     * 
     * @return
     * @throws ParseException 
     */
    IBlockHandler currentBlock() throws ParseException;
    
    /**
     * Notify context to close current block
     * @throws ParseException 
     */
    void closeBlock() throws ParseException;
    
}
