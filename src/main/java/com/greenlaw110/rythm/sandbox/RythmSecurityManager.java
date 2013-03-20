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
import com.greenlaw110.rythm.conf.RythmConfigurationKey;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;

/**
 * The default security manager to ensure template code run in a secure mode
 */
public class RythmSecurityManager extends SecurityManager {

    public static boolean isRythmThread() {
        return (Thread.currentThread() instanceof SandboxThreadFactory.SandboxThread);
    }

    private SecurityManager osm;
    private String code = null;
    private boolean released = false;
    private RythmEngine engine = null;

    private String hash(String input) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] out = m.digest(input.getBytes());
            return new String(Base64.encode(out));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public RythmSecurityManager(SecurityManager sm, String password, RythmEngine re) {
        if (null == re) throw new NullPointerException();
        osm = sm;
        if (null == password) throw new NullPointerException();
        code = hash(password);
        engine = re;
    }

    public void unlock(String password) {
        if (code.equals(hash(password))) {
            released = true;
        } else {
            throw new SecurityException("password not match");
        }
    }

    public void lock(String password) {
        if (code.equals(hash(password))) {
            released = false;
        } else {
            throw new SecurityException("password not match");
        }
    }

    private void checkRythm() {
        if (!released && isRythmThread()) {
            throw new SecurityException("Access to protected resource is restricted in Sandbox mode");
        }
    }

//    @Override
//    public void checkCreateClassLoader() {
//        checkRythm();
//        if (null != osm) osm.checkCreateClassLoader();
//    }

    @Override
    public void checkAccess(Thread t) {
        if (!(t instanceof SandboxThreadFactory.SandboxThread)) checkRythm();
        if (null != osm) osm.checkAccess(t);
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        checkRythm();
        if (null != osm) osm.checkAccess(g);
    }

    @Override
    public void checkExit(int status) {
        checkRythm();
        if (null != osm) osm.checkExit(status);
    }

    @Override
    public void checkExec(String cmd) {
        checkRythm();
        if (null != osm) osm.checkExec(cmd);
    }

    @Override
    public void checkLink(String lib) {
        checkRythm();
        if (null != osm) osm.checkLink(lib);
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        checkRythm();
        if (null != osm) osm.checkRead(fd);
    }

//    @Override
//    public void checkRead(String file) {
//        checkRythm();
//        if (null != osm) osm.checkRead(file);
//    }
//
//    @Override
//    public void checkRead(String file, Object context) {
//        checkRythm();
//        if (null != osm) osm.checkRead(file, context);
//    }
//
    @Override
    public void checkWrite(FileDescriptor fd) {
        checkRythm();
        if (null != osm) osm.checkWrite(fd);
    }

    @Override
    public void checkWrite(String file) {
        checkRythm();
        if (null != osm) osm.checkWrite(file);
    }

    @Override
    public void checkDelete(String file) {
        checkRythm();
        if (null != osm) osm.checkDelete(file);
    }

    @Override
    public void checkConnect(String host, int port) {
        checkRythm();
        if (null != osm) osm.checkConnect(host, port);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        checkRythm();
        if (null != osm) osm.checkConnect(host, port, context);
    }

    @Override
    public void checkListen(int port) {
        checkRythm();
        if (null != osm) osm.checkListen(port);
    }

    @Override
    public void checkAccept(String host, int port) {
        checkRythm();
        if (null != osm) osm.checkAccept(host, port);
    }

    @Override
    public void checkMulticast(InetAddress maddr) {
        checkRythm();
        if (null != osm) osm.checkMulticast(maddr);
    }

    @Override
    public void checkPropertiesAccess() {
        checkRythm();
        if (null != osm) osm.checkPropertiesAccess();
    }

    @Override
    public void checkPropertyAccess(String key) {
        if (key.startsWith("rythm.")) {
            key = key.substring(7);
        }
        if (null != RythmConfigurationKey.valueOfIgnoreCase(key)) {
            return;
        }
        String s = engine.conf().allowedSystemProperties();
        if (s.indexOf(key) > -1) return; 
        checkRythm();
        if (null != osm) osm.checkPropertyAccess(key);
    }

    @Override
    public boolean checkTopLevelWindow(Object window) {
        checkRythm();
        if (null != osm) return osm.checkTopLevelWindow(window);
        else return true;
    }

    @Override
    public void checkPrintJobAccess() {
        checkRythm();
        if (null != osm) osm.checkPrintJobAccess();
    }

    @Override
    public void checkSystemClipboardAccess() {
        checkRythm();
        if (null != osm) osm.checkSystemClipboardAccess();
    }

    @Override
    public void checkAwtEventQueueAccess() {
        checkRythm();
        if (null != osm) osm.checkAwtEventQueueAccess();
    }

    @Override
    public void checkPackageAccess(String pkg) {
        if (null != osm) osm.checkPackageAccess(pkg);
        // TODO: implement Rythm restricted package check
    }

    @Override
    public void checkPackageDefinition(String pkg) {
        checkRythm();
        if (null != osm) osm.checkPackageDefinition(pkg);
    }

    @Override
    public void checkPermission(Permission perm) {
        if ("setSecurityManager".equals(perm.getName())) checkRythm();
        if (null != osm) osm.checkPermission(perm);
    }

    @Override
    public void checkMemberAccess(Class<?> clazz, int which) {
        if (null != osm) osm.checkMemberAccess(clazz, which);
        //Todo check rythm member access
    }

    @Override
    public void checkSetFactory() {
        checkRythm();
        if (null != osm) osm.checkSetFactory();
    }
}
