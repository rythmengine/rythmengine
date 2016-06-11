/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.sandbox;

import org.rythmengine.internal.RythmThreadFactory;

/**
 * Create secure template executing thread
 */
public class SandboxThreadFactory extends RythmThreadFactory {

    /**
     * Construct a Sandbox thread factory instance.
     */
    public SandboxThreadFactory() {
        super("rythm-sandbox-executor");
    }

}
