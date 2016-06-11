/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

/**
 * Accept {@link IEvent event} posting
 */
public interface IEventDispatcher {
    Object accept(IEvent event, Object param);
}
