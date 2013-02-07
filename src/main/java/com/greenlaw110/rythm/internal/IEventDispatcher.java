package com.greenlaw110.rythm.internal;

/**
 * Accept {@link IEvent event} posting
 */
public interface IEventDispatcher {
    void accept(IEvent event, Object param);
}
