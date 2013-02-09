package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.utils.TextBuilder;


public interface IParser {
    /**
     * Return null if the probe failed to go
     *
     * @return
     */
    TextBuilder go();
}
