package com.greenlaw110.rythm.internal;

/**
 * Defines event to be used in rythm system
 */
public interface IEvent<PARAM> {
    void trigger(IEventDispatcher eventBus, PARAM eventParams);
}
