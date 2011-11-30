package com.greenlaw110.rythm.spi;

/**
 * the <code>IBlockHandler</code> declare the interface to handle open/close
 * of code blocks. Usually the implementation should print some special
 * tokens in the 2 methods 
 * 
 * @author luog
 *
 */
public interface IBlockHandler {
    void openBlock();
    String closeBlock();
}
