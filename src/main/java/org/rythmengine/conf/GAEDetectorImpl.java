/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.conf;

class GAEDetectorImpl implements GAEDetector {

    @Override
    public boolean isInGaeCloud() {
        try {
            return com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Production;
        } catch (Throwable t) {
            // Nothing to do
        }
        return false;
    }
}
