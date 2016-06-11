/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

/**
 * the <code>IBlockHandler</code> declare the interface to handle open/close
 * of code blocks. Usually the implementation should print some special
 * tokens in the 2 methods
 *
 * @author luog
 */
public interface IBlockHandler {
    void openBlock();

    String closeBlock();
}
