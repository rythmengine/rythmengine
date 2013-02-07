package com.greenlaw110.rythm.extension;

import com.greenlaw110.rythm.runtime.ITag;

/**
 * Listen to tag invocation event
 */
public interface ITagInvokeListener {
    void onInvoke(ITag tag);
    void invoked(ITag tag);
}
