/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine;


enum DefaultShutdownService implements ShutdownService {

    INSTANCE;

    // Runtime.addShutdownHook might lead to memory leak
    // checkout https://github.com/greenlaw110/Rythm/issues/199
    // Updates: another issue #296 indicate the shutdown service
    // is okay to be called only on Rythm.engine instance. Thus
    // the comment out code has been re-enabled
    @Override
    public void setShutdown(final Runnable runnable) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (runnable != null)
                        runnable.run();
                }
            });
        } catch (Throwable t) {
            // Nothing to do
        }
    }
}
