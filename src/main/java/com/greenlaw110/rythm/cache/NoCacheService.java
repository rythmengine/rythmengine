package com.greenlaw110.rythm.cache;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 2/05/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoCacheService implements ICacheService {

    public static NoCacheService INSTANCE = new NoCacheService();

    private NoCacheService() {}

    @Override
    public void put(String key, Serializable value, int ttl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(String key, Serializable value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Serializable remove(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Serializable get(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(String key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clean() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDefaultTTL(int ttl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void shutdown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
