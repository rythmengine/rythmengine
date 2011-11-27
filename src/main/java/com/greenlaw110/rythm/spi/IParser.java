package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.util.TextBuilder;


public interface IParser {
    /**
     * Return null if the probe failed to go
     * @return
     */
    TextBuilder go();
}
