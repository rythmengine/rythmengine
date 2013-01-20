package com.greenlaw110.rythm.security;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.RythmThreadFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Create secure template executing thread
 */
public class SecureThreadFactory extends RythmThreadFactory {
    private SecurityManager sm;
    private String password = null;
    
    public SecureThreadFactory(SecurityManager sm) {
        super("rythm-executor");
        if (null == sm) {
            String pass = UUID.randomUUID().toString();
            sm = new RythmSecurityManager(System.getSecurityManager(), pass);
            password = pass;
        }
        this.sm = sm;
    }

    static ConcurrentMap<String, SecureThread> runners = new ConcurrentHashMap<String, SecureThread>();
    
    static class SecureThread extends Thread {
        private SecurityManager sm;
        private SecureThreadFactory fact;
        public SecureThread(SecureThreadFactory fact, SecurityManager sm, ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
            this.sm = sm;
            this.fact = fact;
            runners.put(name, this);
        }
        @Override
        public void run() {
            SecurityManager osm = System.getSecurityManager();
            SecurityManager nsm = sm;
            if (osm != nsm) System.setSecurityManager(nsm);
            try {
                super.run();
            } finally {
                if (osm != nsm) {
                    if (nsm instanceof RythmSecurityManager) {
                        RythmSecurityManager rsm = (RythmSecurityManager)nsm;
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
        return new SecureThread(this, sm, g, r, name, stackSize);
    }
    
    static void shutdown() {
//        for (Thread t: runners.values()) {
//            t.stop();
//        }
//        runners.clear();
    }

}
