/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine;

import com.google.appengine.api.LifecycleManager;

enum GaeShutdownService implements ShutdownService {

    INSTANCE;

    @Override
    public void setShutdown(final Runnable runnable) {
        try {
            LifecycleManager.getInstance().setShutdownHook(new LifecycleManager.ShutdownHook() {
                @Override
                public void shutdown() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
        } catch (Throwable t) {
            // Nothing to do
        }
    }
}
