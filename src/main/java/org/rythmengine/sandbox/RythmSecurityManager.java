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
package org.rythmengine.sandbox;

import org.rythmengine.RythmEngine;
import org.rythmengine.Sandbox;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.utils.S;
import sun.security.util.SecurityConstants;

import java.io.File;
import java.io.FilePermission;
import java.net.InetAddress;
import java.security.Permission;

/**
 * The default security manager to ensure template code run in a secure mode
 */
public class RythmSecurityManager extends SecurityManager {

    private SecurityManager osm;
    private SecurityManager csm; // customized security manager
    private String code = null;
    private RythmEngine engine = null;
    private RythmEngine engine() {
        return null == engine ? RythmEngine.get() : engine;
    }
    
    public String getCode() {
        Throwable t = new Throwable();
        StackTraceElement[] st = t.getStackTrace();
        StackTraceElement ste = st[1];
        if (S.ne(ste.getClassName(), RythmEngine.class.getName())) {
            forbidden();
        }
        return code;
    }

    public RythmSecurityManager(SecurityManager customSecurityManager, String password, RythmEngine re) {
        osm = System.getSecurityManager();
        csm = customSecurityManager;
        if (null == password) throw new NullPointerException();
        code = password;
        engine = re;
    }
    
    private static void forbidden() {
        throw new SecurityException("Access to protected resource is restricted in Sandbox mode");
    }
    
    public void forbiddenIfCodeNotMatch(String code) {
        if (S.ne(code, this.code)) {
            forbidden();
        }
    }
    
    private void checkRythm() {
        if (Sandbox.isRestricted()) {
            forbidden();
        }
    }

//    @Override
//    public void checkCreateClassLoader() {
//        checkRythm();
//        if (null != osm) osm.checkCreateClassLoader();
//    }

    @Override
    public void checkAccess(Thread t) {
        //if (!(t instanceof SandboxThreadFactory.SandboxThread)) checkRythm();
        if (null != osm) osm.checkAccess(t);
        if (null != csm) csm.checkAccess(t);
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        checkRythm();
        if (null != csm) csm.checkAccess(g);
        if (null != osm) osm.checkAccess(g);
    }

    @Override
    public void checkExit(int status) {
        checkRythm();
        if (null != osm) osm.checkExit(status);
        if (null != csm) csm.checkExit(status);
    }

    @Override
    public void checkExec(String cmd) {
        checkRythm();
        if (null != osm) osm.checkExec(cmd);
        if (null != csm) csm.checkExec(cmd);
    }

    @Override
    public void checkLink(String lib) {
        checkRythm();
        if (null != osm) osm.checkLink(lib);
        if (null != csm) csm.checkLink(lib);
    }
    
    private interface IFilePathValidator {
        boolean isValid(String path);
    }
    
    private static boolean allowTmpDirIO(String path) {
        RythmConfiguration conf = RythmEngine.get().conf();
        if (conf.sandboxTmpIO()) {
            String tmp = System.getProperty("java.io.tmpdir");
            if (path.startsWith(tmp)) {
                return true;
            } else if ((path + File.separator).startsWith(tmp)) {
                return true;
            }
        }
        return false;
    }
    
    private static IFilePathValidator readable = new IFilePathValidator() {
        @Override
        public boolean isValid(String path) {
            String uxPath = path;
            if (path.matches("^(jar:file:)?[a-zA-Z]:.*")) {
                uxPath = "/" + path.replace("\\", "/").toLowerCase();
            }
            if (uxPath.startsWith(BASE_RYTHM) || uxPath.startsWith(BASE_JDK)) {
                return true;
            }
            return allowTmpDirIO(path);
        }
    };
    
    private static IFilePathValidator writable = new IFilePathValidator() {
        @Override
        public boolean isValid(String path) {
            return allowTmpDirIO(path);
        }
    };
    
    private static IFilePathValidator deletable = writable;
    
    private void safeCheckFile(String path, IFilePathValidator validator) {
        if (!Sandbox.isRestricted()) return;
        Sandbox.enterSafeZone(code);
        try {
            if (!validator.isValid(path)) {
                forbidden();
            }
        } finally {
            Sandbox.leaveCurZone(code);
        }
    }

    @Override
    public void checkRead(String file) {
        safeCheckFile(file, readable);
        if (null != csm) csm.checkRead(file);
        if (null != osm) osm.checkRead(file);
    }
    
    private static final String BASE_RYTHM = RythmEngine.class.getResource(RythmEngine.class.getSimpleName() + ".class").getFile().replace("RythmEngine.class", "").toLowerCase(); 
    private static final String BASE_JDK = Integer.class.getResource(Integer.class.getSimpleName() + ".class").getFile().replace("Integer.class", "").toLowerCase();
    
    @Override
    public void checkWrite(String file) {
        safeCheckFile(file, writable);
        if (null != csm) csm.checkWrite(file);
        if (null != osm) osm.checkWrite(file);
    }

    @Override
    public void checkDelete(String file) {
        safeCheckFile(file, deletable);
        if (null != csm) csm.checkDelete(file);
        if (null != osm) osm.checkDelete(file);
    }

    @Override
    public void checkConnect(String host, int port) {
        checkRythm();
        if (null != csm) csm.checkConnect(host, port);
        if (null != osm) osm.checkConnect(host, port);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        checkRythm();
        if (null != csm) csm.checkConnect(host, port, context);
        if (null != osm) osm.checkConnect(host, port, context);
    }

    @Override
    public void checkListen(int port) {
        checkRythm();
        if (null != csm) csm.checkListen(port);
        if (null != osm) osm.checkListen(port);
    }

    @Override
    public void checkAccept(String host, int port) {
        checkRythm();
        if (null != csm) csm.checkAccept(host, port);
        if (null != osm) osm.checkAccept(host, port);
    }

    @Override
    public void checkMulticast(InetAddress maddr) {
        checkRythm();
        if (null != csm) csm.checkMulticast(maddr);
        if (null != osm) osm.checkMulticast(maddr);
    }

    @Override
    public void checkPropertiesAccess() {
        checkRythm();
        if (null != csm) csm.checkPropertiesAccess();
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
        RythmEngine e = engine();
        if (null == e) {
            // not in Rendering process yet, let's assume it's safe to check system properties
            return;
        }
        String s = e.conf().allowedSystemProperties();
        if (s.indexOf(key) > -1) return; 
        checkRythm();
        if (null != csm) csm.checkPropertyAccess(key);
        if (null != osm) osm.checkPropertyAccess(key);
    }

    @Override
    public boolean checkTopLevelWindow(Object window) {
        checkRythm();
        if (null != csm) return csm.checkTopLevelWindow(window);
        else if (null != osm) return osm.checkTopLevelWindow(window);
        else return true;
    }

    @Override
    public void checkPrintJobAccess() {
        checkRythm();
        if (null != csm) csm.checkPrintJobAccess();
        if (null != osm) osm.checkPrintJobAccess();
    }

    @Override
    public void checkSystemClipboardAccess() {
        checkRythm();
        if (null != osm) osm.checkSystemClipboardAccess();
        if (null != csm) csm.checkSystemClipboardAccess();
    }

    @Override
    public void checkAwtEventQueueAccess() {
        checkRythm();
        if (null != osm) osm.checkAwtEventQueueAccess();
        if (null != csm) csm.checkAwtEventQueueAccess();
    }

    @Override
    public void checkPackageAccess(String pkg) {
        if (null != osm) osm.checkPackageAccess(pkg);
        if (null != csm) csm.checkPackageAccess(pkg);
        // TODO: implement Rythm restricted package check
    }

    @Override
    public void checkPackageDefinition(String pkg) {
        checkRythm();
        if (null != osm) osm.checkPackageDefinition(pkg);
        if (null != osm) osm.checkPackageDefinition(pkg);
    }
    
    private void checkFilePermission(FilePermission fp) {
        String actions = fp.getActions();
        String name = fp.getName();
        if (actions.contains(SecurityConstants.FILE_READ_ACTION)){
            checkRead(name);
        }
        if (actions.contains(SecurityConstants.FILE_WRITE_ACTION)) {
            checkWrite(name);
        }
        if (actions.contains(SecurityConstants.FILE_DELETE_ACTION)) {
            checkDelete(name);
        }
        if (actions.contains(SecurityConstants.FILE_EXECUTE_ACTION)) {
            checkExec(name);
        }
    }

    @Override
    public void checkPermission(Permission perm) {
        if (perm instanceof FilePermission) {
            FilePermission fp = (FilePermission)perm;
            checkFilePermission(fp);
        } else if ("setSecurityManager".equals(perm.getName())) {
            checkRythm();
        }
        
        if (null != osm) osm.checkPermission(perm);
        if (null != csm) csm.checkPermission(perm);
    }

    @Override
    public void checkMemberAccess(Class<?> clazz, int which) {
        if (null != osm) osm.checkMemberAccess(clazz, which);
        if (null != csm) csm.checkMemberAccess(clazz, which);
        //Todo check rythm member access
    }

    @Override
    public void checkSetFactory() {
        checkRythm();
        if (null != osm) osm.checkSetFactory();
        if (null != csm) csm.checkSetFactory();
    }
}
