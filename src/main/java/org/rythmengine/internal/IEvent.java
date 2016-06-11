/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

/**
 * Defines event to be used in rythm system
 */
public interface IEvent<RETURN, PARAM> {
    RETURN trigger(IEventDispatcher eventBus, PARAM eventParams);
    boolean isSafe();
}
