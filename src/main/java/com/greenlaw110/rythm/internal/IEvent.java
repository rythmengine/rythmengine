package com.greenlaw110.rythm.internal;

/**
 * Defines event to be used in rythm system
 */
public interface IEvent<RETURN, PARAM> {
    RETURN trigger(IEventDispatcher eventBus, PARAM eventParams);
}
