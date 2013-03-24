/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.sandbox;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.RythmThreadFactory;
import com.greenlaw110.rythm.internal.compiler.TemplateClassLoader;

import java.util.UUID;

/**
 * Create secure template executing thread
 */
public class SandboxThreadFactory extends RythmThreadFactory {
    private SecurityManager sm;
    private String password = null;

    /**
     * Construct a Sandbox thread factory instance.
     * @param sm security manager, optional
     * @param re engine, optional
     */
    public SandboxThreadFactory(SecurityManager sm, String password, RythmEngine re) {
        super("rythm-executor");
        if (null == sm) {
            String pass = UUID.randomUUID().toString();
            sm = new RythmSecurityManager(System.getSecurityManager(), pass, re);
            this.password = pass;
        } else {
            this.password = password;
        }
        this.sm = sm;
    }

    //static ConcurrentMap<String, SandboxThread> runners = new ConcurrentHashMap<String, SandboxThread>();

    static class SandboxThread extends Thread {
        private SecurityManager sm;
        private SandboxThreadFactory fact;

        public SandboxThread(SandboxThreadFactory fact, SecurityManager sm, ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
            this.sm = sm;
            this.fact = fact;
            //runners.put(name, this);
        }

        @Override
        public void run() {
            SecurityManager osm = System.getSecurityManager();
            SecurityManager nsm = sm;
            boolean needsSetSM = null == osm || !osm.getClass().equals(nsm.getClass()); 
            if (needsSetSM) {
                System.setSecurityManager(nsm);
            }
            TemplateClassLoader.setSandboxPassword(fact.password);
            try {
                super.run();
            } finally {
                if (needsSetSM) {
                    if (nsm instanceof RythmSecurityManager) {
                        RythmSecurityManager rsm = (RythmSecurityManager) nsm;
                        rsm.unlock(fact.password);
                        System.setSecurityManager(osm);
                        rsm.lock(fact.password);
                    } else {
                        System.setSecurityManager(osm);
                    }
                }
            }
        }
    }

    @Override
    protected Thread newThread0(ThreadGroup g, Runnable r, String name, long stackSize) {
        return new SandboxThread(this, sm, g, r, name, stackSize);
    }

    static void shutdown() {
//        for (Thread t: runners.values()) {
//            t.stop();
//        }
//        runners.clear();
    }

}
