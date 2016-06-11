/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;

import java.io.Serializable;

/**
 * A do-nothing implementation of {@link org.rythmengine.extension.ICacheService}
 */
public class NoCacheService implements ICacheService {

    public static final NoCacheService INSTANCE = new NoCacheService();

    private NoCacheService() {
    }

    @Override
    public void put(String key, Serializable value, int ttl) {
    }

    @Override
    public void put(String key, Serializable value) {
    }

    @Override
    public Serializable remove(String key) {
        return null;
    }

    @Override
    public void evict(String key) {
        return;
    }

    @Override
    public Serializable get(String key) {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public void setDefaultTTL(int ttl) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void startup() {
    }
}
