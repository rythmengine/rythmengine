package com.greenlaw110.rythm;

import com.greenlaw110.rythm.runtime.ITag;

/**
 * Listen tag invocation action event
 */
public interface ITagInvokeListener {
    void onInvoke(ITag tag);
    void tagInvoked(ITag tag);
}
