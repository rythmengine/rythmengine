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
package org.rythmengine.internal.compiler;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.IByteCodeHelper;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.sandbox.RythmSecurityManager;
import org.rythmengine.template.ITemplate;
import org.rythmengine.utils.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/01/12
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateClassLoader extends ClassLoader {

    private static final ILogger logger = Logger.get(TemplateClass.class);

    public static class ClassStateHashCreator {

        private final Pattern classDefFinderPattern = Pattern.compile("\\s+class\\s([a-zA-Z0-9_]+)\\s+");

        private static class FileWithClassDefs {
            private final File file;
            private final long size;
            private final long lastModified;
            private final String classDefs;

            private FileWithClassDefs(File file, String classDefs) {
                this.file = file;
                this.classDefs = classDefs;

                // store size and time for this file..
                size = file.length();
                lastModified = file.lastModified();
            }

            /**
             * @return true if file has changed on disk
             */
            public boolean fileNotChanges() {
                return size == file.length() && lastModified == file.lastModified();
            }

            public String getClassDefs() {
                return classDefs;
            }
        }

        private final Map<File, FileWithClassDefs> classDefsInFileCache = new HashMap<File, FileWithClassDefs>();

        public synchronized int computePathHash(File... paths) {
            StringBuffer buf = new StringBuffer();
            for (File file : paths) {
                scan(buf, file);
            }
            // TODO: should use better hashing-algorithm.. MD5? SHA1?
            // I think hashCode() has too many collisions..
            return buf.toString().hashCode();
        }

        private void scan(StringBuffer buf, File current) {
            if (!current.isDirectory()) {
                if (current.getName().endsWith(".java")) {
                    buf.append(getClassDefsForFile(current));
                }

            } else if (!current.getName().startsWith(".")) {
                // TODO: we could later optimizie it further if we check if the entire folder is unchanged

                File[] fa = current.listFiles();
                if (null != fa) {
                    for (File file : current.listFiles()) {
                        scan(buf, file);
                    }
                }
            }
        }

        private String getClassDefsForFile(File file) {

            FileWithClassDefs fileWithClassDefs = classDefsInFileCache.get(file);
            if (fileWithClassDefs != null && fileWithClassDefs.fileNotChanges()) {
                // found the file in cache and it has not changed on disk
                return fileWithClassDefs.getClassDefs();
            }

            // didn't find it or it has changed on disk
            // we must re-parse it

            StringBuilder buf = new StringBuilder();
            Matcher matcher = classDefFinderPattern.matcher(IO.readContentAsString(file));
            buf.append(file.getName());
            buf.append("(");
            while (matcher.find()) {
                buf.append(matcher.group(1));
                buf.append(",");
            }
            buf.append(")");
            String classDefs = buf.toString();

            // store it in cache
            classDefsInFileCache.put(file, new FileWithClassDefs(file, classDefs));
            return classDefs;
        }
    }

    private final ClassStateHashCreator classStateHashCreator = new ClassStateHashCreator();

    /**
     * Each unique instance of this class represent a State of the TemplateClassLoader.
     * When some classCache is reloaded, them the TemplateClassLoader get a new state.
     * <p/>
     * This makes it easy for other parts of Play to cache stuff based on the
     * the current State of the TemplateClassLoader..
     * <p/>
     * They can store the reference to the current state, then later, before reading from cache,
     * they could check if the state of the TemplateClassLoader has changed..
     */
    public static class TemplateClassloaderState {
        private static AtomicLong nextStateValue = new AtomicLong();

        private final long currentStateValue = nextStateValue.getAndIncrement();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TemplateClassloaderState that = (TemplateClassloaderState) o;

            return (currentStateValue == that.currentStateValue);
        }

        @Override
        public int hashCode() {
            return (int) (currentStateValue ^ (currentStateValue >>> 32));
        }
    }

    /**
     * A representation of the current state of the TemplateClassLoader.
     * It gets a new value each time the state of the classloader changes.
     */
    public TemplateClassloaderState currentState = new TemplateClassloaderState();

    /**
     * This protection domain applies to all loaded classCache.
     */
    public ProtectionDomain protectionDomain;

    public RythmEngine engine;

    private RythmConfiguration conf;

    private static ClassLoader getDefParent(RythmEngine engine) {
        return engine.conf().get(RythmConfigurationKey.ENGINE_CLASS_LOADER_PARENT_IMPL);
    }

    public TemplateClassLoader(RythmEngine engine) {
        this(getDefParent(engine), engine);
    }

    public TemplateClassLoader(ClassLoader parent, RythmEngine engine) {
        super(parent);
        this.engine = engine;
        this.conf = engine.conf();
        for (TemplateClass tc : engine.classes().all()) {
            tc.uncompile();
        }
        pathHash = computePathHash();
//        try {
//            CodeSource codeSource = new CodeSource(new URL("file:" + Play.applicationPath.getAbsolutePath()), (Certificate[]) null);
//            Permissions permissions = new Permissions();
//            permissions.add(new AllPermission());
//            protectionDomain = new ProtectionDomain(codeSource, permissions);
//        } catch (MalformedURLException e) {
//            throw new UnexpectedException(e);
//        }
    }

    private static final ThreadLocal<String> sandboxPassword = new ThreadLocal<String>();

    public static void setSandboxPassword(String password) {
        sandboxPassword.set(password);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // init secure context for sandbox
        SecurityManager sm;
        RythmSecurityManager rsm = null;
        String pass = null;
        if (Rythm.insideSandbox()) {
            if (conf.restrictedClasses().contains(name)) {
                throw new ClassNotFoundException("Access to class " + name + " is restricted in sandbox mode");
            }
            sm = System.getSecurityManager();
            if (null != sm && sm instanceof RythmSecurityManager) {
                rsm = (RythmSecurityManager) sm;
                pass = sandboxPassword.get();
            }
        }

        TemplateClass tc = engine.classes().clsNameIdx.get(name);
        if (null == tc) {
            // it's not a template class, let's try to find already loaded one
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }
        }

        // First check if it's an application Class
        Class<?> TemplateClass = loadTemplateClass(name);
        if (TemplateClass != null) {
            if (resolve) {
                resolveClass(TemplateClass);
            }
            return TemplateClass;
        }

        // Delegate to the classic classloader
        boolean unlockSM = /*engine.isDevMode() && */null != rsm;
        try {
            // release sandbox password if running inside sandbox in order to load 
            // application classes when running is dev mode
            if (unlockSM) {
                rsm.unlock(pass);
            }
            return super.loadClass(name, resolve);
        } finally {
            if (unlockSM) {
                rsm.lock(pass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Class<?> loadTemplateClass(String name) {
        Class<?> maybeAlreadyLoaded = findLoadedClass(name);
        if (maybeAlreadyLoaded != null) {
            return maybeAlreadyLoaded;
        }
        long start = System.currentTimeMillis();
        TemplateClass templateClass = engine.classes().getByClassName(name);
        if (templateClass != null) {
            if (templateClass.isDefinable()) {
                return templateClass.javaClass;
            }
            byte[] bc = templateClass.enhancedByteCode;//bCache.getBytecode(name, templateClass.javaSource);
            if (!templateClass.isClass()) {
                definePackage(templateClass.getPackage(), null, null, null, null, null, null, null);
            } else {
                loadPackage(name);
            }
            if (bc != null) {
                //templateClass.enhancedByteCode = bc;
                templateClass.javaClass = (Class<ITemplate>) defineClass(templateClass.name(), templateClass.enhancedByteCode, 0, templateClass.enhancedByteCode.length, protectionDomain);
                resolveClass(templateClass.javaClass);
                if (!templateClass.isClass()) {
                    templateClass.javaPackage = templateClass.javaClass.getPackage();
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("%sms to load class %s from clsNameIdx", System.currentTimeMillis() - start, name);
                }
                return templateClass.javaClass;
            }

            if (templateClass.javaByteCode != null || templateClass.compile() != null) {
                templateClass.enhance();
                templateClass.javaClass = (Class<ITemplate>) defineClass(templateClass.name(), templateClass.enhancedByteCode, 0, templateClass.enhancedByteCode.length, protectionDomain);
                resolveClass(templateClass.javaClass);
                if (!templateClass.isClass()) {
                    templateClass.javaPackage = templateClass.javaClass.getPackage();
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("%sms to load class %s", System.currentTimeMillis() - start, name);
                }
                return templateClass.javaClass;
            }
            engine.classes().remove(name);
        } else if (name.lastIndexOf(TemplateClass.CN_SUFFIX) == -1) {
            return null;
        } else {
            int pos = name.indexOf("$");
            if (-1 != pos) {
                // should be an inner class, let's try to create it load it from cache
                // 1. find the root class
                String parentCN = name.substring(0, pos);
                TemplateClass parent = engine.classes().getByClassName(parentCN);
                if (null == parent) {
                    throw new RuntimeException("Cannot find inner class def: " + name);
                }
                TemplateClass tc = TemplateClass.createInnerClass(name, null, parent);
                engine.classCache().loadTemplateClass(tc);
                byte[] bc = tc.enhancedByteCode;
                if (null == bc) {
                    // inner class byte code cache missed some how, let's try to recover it
                    while ((null != parent) && parent.isInner()) {
                        parent = parent.root();
                    }
                    if (null == parent) {
                        throw new RuntimeException("Unexpected: cannot find the root class of inner class: " + name);
                    }
                    parent.reset();
                    parent.refresh(true);
                    parent.compile();
                    // now try again and see if we can find the class definition
                    tc = engine.classes().getByClassName(name);
                    Class<?> c = tc.javaClass;
                    if (null != c) return c;
                    bc = tc.enhancedByteCode;
                    if (null == bc) {
                        throw new RuntimeException("Cannot find bytecode cache for inner class: " + name);
                    }
                }
                tc.javaClass = (Class<ITemplate>) defineClass(tc.name(), bc, 0, bc.length, protectionDomain);
                return tc.javaClass;
            }
        }
        return null;
    }

    private String getPackageName(String name) {
        int dot = name.lastIndexOf('.');
        return dot > -1 ? name.substring(0, dot) : "";
    }

    private void loadPackage(String className) {
        // find the package class name
        int symbol = className.indexOf("$");
        if (symbol > -1) {
            className = className.substring(0, symbol);
        }
        symbol = className.lastIndexOf(".");
        if (symbol > -1) {
            className = className.substring(0, symbol) + ".package-info";
        } else {
            className = "package-info";
        }
        if (findLoadedClass(className) == null) {
            loadTemplateClass(className);
        }
    }
    
    private Set<String> notFoundTypes = null;
    private boolean typeNotFound(String name) {
        if (null == notFoundTypes) {
            notFoundTypes = engine.classes().compiler.notFoundTypes;
        }
        return notFoundTypes.contains(name);
    }
    private void setTypeNotFound(String name) {
        if (null == notFoundTypes) {
            notFoundTypes = engine.classes().compiler.notFoundTypes;
        }
        if (engine.isProdMode()) {
            notFoundTypes.add(name);
        } else if (name.matches("^(java\\.|play\\.|com\\.greenlaw110\\.).*")) {
            notFoundTypes.add(name);
        }
    }

    /**
     * Search for the byte code of the given class.
     */
    protected byte[] getClassDefinition(final String name0) {
        if (typeNotFound(name0)) return null;
        byte[] ba = null;
        String name = name0.replace(".", "/") + ".class";
        InputStream is = getResourceAsStream(name);
        if (null != is) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int count;
                while ((count = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, count);
                }
                ba = os.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (null == ba) {
            IByteCodeHelper helper = engine.conf().byteCodeHelper();
            if (null != helper) {
                ba = helper.findByteCode(name0);
            }
        }
        if (null == ba) {
            setTypeNotFound(name0);
        }
        return ba;
    }

    public void detectChange(TemplateClass tc) {
        if (engine.isProdMode() && null != tc.name()) return;
        if (!tc.refresh()) return;
        if (tc.compile() == null) {
            engine.classes().remove(tc);
            currentState = new TemplateClassloaderState();
        } else {
            throw new ClassReloadException("Need reload");
        }
    }

    /**
     * Detect Template changes
     */
    public void detectChanges() {
        if (engine.isProdMode()) return;
        // Now check for file modification
        List<TemplateClass> modifieds = new ArrayList<TemplateClass>();
        for (TemplateClass tc : engine.classes().all()) {
            if (tc.refresh()) modifieds.add(tc);
        }
        Set<TemplateClass> modifiedWithDependencies = new HashSet<TemplateClass>();
        modifiedWithDependencies.addAll(modifieds);
        List<ClassDefinition> newDefinitions = new ArrayList<ClassDefinition>();
        boolean dirtySig = false;
        for (TemplateClass tc : modifiedWithDependencies) {
            if (tc.compile() == null) {
                engine.classes().remove(tc);
                currentState = new TemplateClassloaderState();//show others that we have changed..
            } else {
                int sigChecksum = tc.sigChecksum;
                tc.enhance();
                if (sigChecksum != tc.sigChecksum) {
                    dirtySig = true;
                }
                newDefinitions.add(new ClassDefinition(tc.javaClass, tc.enhancedByteCode));
                currentState = new TemplateClassloaderState();//show others that we have changed..
            }
        }
        if (newDefinitions.size() > 0) {
            throw new ClassReloadException("Need Reload");
        }
        // Check signature (variable name & annotations aware !)
        if (dirtySig) {
            throw new ClassReloadException("Signature change !");
        }

        // Now check if there is new classCache or removed classCache
        int hash = computePathHash();
        if (hash != this.pathHash) {
            // Remove class for deleted files !!
            for (TemplateClass tc : engine.classes().all()) {
                if (!tc.templateResource.isValid()) {
                    engine.classes().remove(tc);
                    currentState = new TemplateClassloaderState();//show others that we have changed..
                }
            }
            throw new ClassReloadException("Path has changed");
        }
    }

    /**
     * Used to track change of the application sources path
     */
    int pathHash = 0;

    int computePathHash() {
        return engine.isProdMode() ? 0 : classStateHashCreator.computePathHash(engine.conf().tmpDir());
    }

}
