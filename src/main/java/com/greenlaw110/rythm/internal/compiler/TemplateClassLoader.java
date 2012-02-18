package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.IHotswapAgent;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.IO;

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
            public boolean fileNotChanges( ) {
                return size == file.length() && lastModified == file.lastModified();
            }

            public String getClassDefs() {
                return classDefs;
            }
        }

        private final Map<File, FileWithClassDefs> classDefsInFileCache = new HashMap<File, FileWithClassDefs>();

        public synchronized int computePathHash(File... paths) {
            StringBuffer buf = new StringBuffer();
            for (File file: paths) {
                scan(buf, file);
            }
            // TODO: should use better hashing-algorithm.. MD5? SHA1?
            // I think hashCode() has too many collisions..
            return buf.toString().hashCode();
        }

        private void scan(StringBuffer buf, File current) {
            if (!current.isDirectory()) {
                if (current.getName().endsWith(".java")) {
                    buf.append( getClassDefsForFile(current));
                }
            } else if (!current.getName().startsWith(".")) {
                // TODO: we could later optimizie it further if we check if the entire folder is unchanged
                for (File file : current.listFiles()) {
                    scan(buf, file);
                }
            }
        }

        private String getClassDefsForFile( File file ) {

            FileWithClassDefs fileWithClassDefs = classDefsInFileCache.get( file );
            if( fileWithClassDefs != null && fileWithClassDefs.fileNotChanges() ) {
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
            classDefsInFileCache.put( file, new FileWithClassDefs(file, classDefs));
            return classDefs;
        }
    }
    private final ClassStateHashCreator classStateHashCreator = new ClassStateHashCreator();

    /**
     * Each unique instance of this class represent a State of the TemplateClassLoader.
     * When some classCache is reloaded, them the TemplateClassLoader get a new state.
     *
     * This makes it easy for other parts of Play to cache stuff based on the
     * the current State of the TemplateClassLoader..
     *
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

            if (currentStateValue != that.currentStateValue) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (currentStateValue ^ (currentStateValue >>> 32));
        }
    }    /**
     * A representation of the current state of the TemplateClassLoader.
     * It gets a new value each time the state of the classloader changes.
     */
    public TemplateClassloaderState currentState = new TemplateClassloaderState();

    /**
     * This protection domain applies to all loaded classCache.
     */
    public ProtectionDomain protectionDomain;

    public RythmEngine engine;

    private static ClassLoader getDefParent() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return null == cl ? RythmEngine.class.getClassLoader() : cl;
    }

    public TemplateClassLoader(RythmEngine engine) {
        this(getDefParent(), engine);
    }

    public TemplateClassLoader(ClassLoader parent, RythmEngine engine) {
        super(parent);
        this.engine = engine;
        for (TemplateClass tc: engine.classes.all()) {
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


    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        TemplateClass tc = engine.classes.clsNameIdx.get(name);
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
        return super.loadClass(name, resolve);
    }

    @SuppressWarnings("unchecked")
    private Class<?> loadTemplateClass(String name) {
        Class<?> maybeAlreadyLoaded = findLoadedClass(name);
        if(maybeAlreadyLoaded != null) {
            return maybeAlreadyLoaded;
        }
        long start = System.currentTimeMillis();
        TemplateClass templateClass = engine.classes.getByClassName(name);
        if (templateClass != null) {
            if (templateClass.isDefinable()) {
                return templateClass.javaClass;
            }
            byte[] bc = null;//bCache.getBytecode(name, templateClass.javaSource);
            logger.trace("Compiling code for %s", name);
            if (!templateClass.isClass()) {
                definePackage(templateClass.getPackage(), null, null, null, null, null, null, null);
            } else {
                loadPackage(name);
            }
            if (bc != null) {
                templateClass.enhancedByteCode = bc;
                templateClass.javaClass = (Class<ITemplate>)defineClass(templateClass.name(), templateClass.enhancedByteCode, 0, templateClass.enhancedByteCode.length, protectionDomain);
                resolveClass(templateClass.javaClass);
                if (!templateClass.isClass()) {
                    templateClass.javaPackage = templateClass.javaClass.getPackage();
                }
                logger.trace("%sms to load class %s from clsNameIdx", System.currentTimeMillis() - start, name);
                return templateClass.javaClass;
            }

            if (templateClass.javaByteCode != null || templateClass.compile() != null) {
                templateClass.enhance();
                templateClass.javaClass = (Class<ITemplate>)defineClass(templateClass.name(), templateClass.enhancedByteCode, 0, templateClass.enhancedByteCode.length, protectionDomain);
                resolveClass(templateClass.javaClass);
                if (!templateClass.isClass()) {
                    templateClass.javaPackage = templateClass.javaClass.getPackage();
                }
                logger.trace("%sms to load class %s", System.currentTimeMillis() - start, name);
                return templateClass.javaClass;
            }
            engine.classes.remove(name);
        } else if (name.lastIndexOf(TemplateClass.CN_SUFFIX) == -1) {
            return null;
        } else {
            int pos = name.lastIndexOf("$");
            if (-1 != pos) {
                // should be an inner class, let's try to create it load it from cache
                // 1. find the root class
                String parentCN = name.substring(0, pos);
                TemplateClass parent = engine.classes.getByClassName(parentCN);
                if (null == parent) {
                    throw new RuntimeException("Cannot find inner class def: " + name);
                }
                TemplateClass tc = TemplateClass.createInnerClass(name, null, parent);
                engine.cache.loadTemplateClass(tc);
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
                    tc = engine.classes.getByClassName(name);
                    Class<?> c = tc.javaClass;
                    if (null != c) return c;
                    bc = tc.enhancedByteCode;
                    if (null == bc) {
                        throw new RuntimeException("Cannot find bytecode cache for inner class: " + name);
                    }
                }
                tc.javaClass = (Class<ITemplate>)defineClass(tc.name(), bc, 0, bc.length, protectionDomain);
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

    /**
     * Search for the byte code of the given class.
     */
    protected byte[] getClassDefinition(String name) {
        name = name.replace(".", "/") + ".class";
        InputStream is = getResourceAsStream(name);
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = is.read(buffer, 0, buffer.length)) > 0) {
                os.write(buffer, 0, count);
            }
            return os.toByteArray();
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
    
    public void detectChange(TemplateClass tc) {
        if (!engine.refreshOnRender() && null != tc.name()) return;
        if (!tc.refresh()) return;
        if (tc.compile() == null) {
            engine.classes.remove(tc);
            currentState = new TemplateClassloaderState();
        } else {
            int sigChecksum = tc.sigChecksum;
            tc.enhance();
            if (sigChecksum != tc.sigChecksum) {
                throw new RuntimeException("Signature change !");
            }
//            bCache.cacheBytecode(tc.enhancedByteCode, tc.name(), tc.javaSource);
            IHotswapAgent agent = engine.hotswapAgent;
            if (null != agent) {
                List<ClassDefinition> newDefinitions = new ArrayList<ClassDefinition>();
                if (null == tc.javaClass) {
                    tc.javaClass = (Class<ITemplate>)defineClass(tc.name(), tc.enhancedByteCode, 0, tc.enhancedByteCode.length, protectionDomain);
                    resolveClass(tc.javaClass);
                }
                newDefinitions.add(new ClassDefinition(tc.javaClass, tc.enhancedByteCode));
                List<TemplateClass> allEmbedded = engine.classes.getEmbeddedClasses(tc.name0());
                for (TemplateClass ec: allEmbedded) {
                    newDefinitions.add(new ClassDefinition(ec.javaClass, ec.enhancedByteCode));
                }
                currentState = new TemplateClassloaderState();//show others that we have changed..
                try {
                    agent.reload(newDefinitions.toArray(new ClassDefinition[newDefinitions.size()]));
                } catch (Throwable e) {
                    engine.classes.remove(tc);
                    throw new ClassReloadException("Need reload", e);
                }
            } else {
                // we have v version scheme to handle class hotswap now #throw new RuntimeException("Need reload");
            }
        }
//        // Now check if there is new classCache or removed classCache
//        int hash = computePathHash();
//        if (hash != this.pathHash) {
//            // Remove class for deleted files !!
//            for (TemplateClass tc0 : engine.classes.all()) {
//                if (!tc0.templateResource.isValid()) {
//                    engine.classes.remove(tc0);
//                    currentState = new TemplateClassloaderState();//show others that we have changed..
//                }
//            }
//            throw new RuntimeException("Path has changed");
//        }
    }

    /**
     * Detect Template changes
     */
    public void detectChanges() {
        if (engine.refreshOnRender()) return;
        // Now check for file modification
        List<TemplateClass> modifieds = new ArrayList<TemplateClass>();
        for (TemplateClass tc : engine.classes.all()) {
            if (tc.refresh()) modifieds.add(tc);
        }
        Set<TemplateClass> modifiedWithDependencies = new HashSet<TemplateClass>();
        modifiedWithDependencies.addAll(modifieds);
        List<ClassDefinition> newDefinitions = new ArrayList<ClassDefinition>();
        boolean dirtySig = false;
        for (TemplateClass tc : modifiedWithDependencies) {
            if (tc.compile() == null) {
                engine.classes.remove(tc);
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
            IHotswapAgent agent = engine.hotswapAgent;
            if (null != agent) {
                try {
                    agent.reload(newDefinitions.toArray(new ClassDefinition[newDefinitions.size()]));
                } catch (Throwable e) {
                    throw new ClassReloadException("Need Reload");
                }
            } else {
                throw new ClassReloadException("Need Reload");
            }
        }
        // Check signature (variable name & annotations aware !)
        if (dirtySig) {
            throw new ClassReloadException("Signature change !");
        }

        // Now check if there is new classCache or removed classCache
        int hash = computePathHash();
        if (hash != this.pathHash) {
            // Remove class for deleted files !!
            for (TemplateClass tc : engine.classes.all()) {
                if (!tc.templateResource.isValid()) {
                    engine.classes.remove(tc);
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
        return classStateHashCreator.computePathHash(engine.tmpDir);
    }

}
