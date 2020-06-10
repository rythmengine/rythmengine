/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.compiler;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.TextBuilder;

import java.io.*;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Used to speed up compilation time
 */
public class TemplateClassCache {
    private static final ILogger logger = Logger.get(TemplateClassCache.class);

    private final RythmEngine engine;
    private final RythmConfiguration conf;
    private final Rythm.Mode mode;

    public TemplateClassCache(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
        this.conf = engine.conf();
        this.mode = engine.mode();
    }

    private boolean readEnabled() {
        return (mode.isDev() || conf.loadPrecompiled()) && !RythmEngine.insideSandbox();
    }
    
    private boolean writeEnabled() {
        return (mode.isDev() || !conf.disableFileWrite() || conf.precompileMode()) && !RythmEngine.insideSandbox();
    }

    /**
     * Delete the bytecode
     *
     * @param tc The template class
     */
    public void deleteCache(TemplateClass tc) {
        if (!writeEnabled()) return;
        try {
            File f = getCacheFile(tc);
            if (f.exists() && !f.delete()) {
                f.deleteOnExit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve java source and bytecode if template content has not changed
     *
     * @param tc
     */
    public void loadTemplateClass(TemplateClass tc) {
        if (!readEnabled()) {
            return;
        }
        InputStream is = null;
        try {
            File f = getCacheFile(tc);
            if (!f.exists() || !f.canRead()) return;
            is = new BufferedInputStream(new FileInputStream(f));

            // --- check hash
            int offset = 0;
            int read;
            StringBuilder hash = new StringBuilder();
            while ((read = is.read()) != 0) {
                if (read == -1) {
                    logger.error("Failed to read cache file for template class: %s", tc);
                    return;
                }
                hash.append((char) read);
                offset++;
            }

            //check hash only in non precompiled mode
            if (!conf.loadPrecompiled()) {
                String curHash = hash(tc);
                if (!curHash.equals(hash.toString())) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Bytecode too old (%s != %s)", hash, curHash);
                    }
                    return;
                }
            }

            // --- load java source
            read = -1;
            StringBuilder source = new StringBuilder();
            while ((read = is.read()) != 0) {
                source.append((char) read);
                offset++;
            }
            if (source.length() != 0) {
                String s = source.toString();
                String[] sa = s.split("__INCLUDED_TAG_TYPES__");
                tc.javaSource = sa[0];
                s = sa[1];
                sa = s.split("__INCULDED_TEMPLATE_CLASS_NAME_LIST__");
                tc.deserializeIncludeTagTypes(sa[0]);
                s = sa[1];
                sa = s.split("__IMPORT_PATH_LIST__");
                tc.setIncludeTemplateClassNames(sa[0]);
                if (sa.length > 1) {
                    s = sa[1];
                    sa = s.split(";");
                    Set<String> importPaths = new CopyOnWriteArraySet<String>();
                    for (String path : sa) {
                        if ("java.lang".equals(path)) continue;
                        importPaths.add(path);
                    }
                    tc.replaceImportPath(importPaths);
                }
            } // else it must be an inner class

            // --- load byte code
            byte[] byteCode = new byte[(int) f.length() - (offset + 2)];
            is.read(byteCode);
            tc.loadCachedByteCode(byteCode);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
        }
    }

    public void cacheTemplateClassSource(TemplateClass tc) {
        if (!writeEnabled()) {
            return;
        }
        try {
            File f = getCacheSourceFile(tc);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
            os.write(tc.javaSource.getBytes("utf-8"));
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheTemplateClass(TemplateClass tc) {
        if (!writeEnabled()) {
            return;
        }
        String hash = hash(tc);
        try {
            File f = getCacheFile(tc);
            // --- write hash value
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
            os.write(hash.getBytes("utf-8"));

            // --- cache java source
            os.write(0);
            if (null != tc.javaSource) {
                TextBuilder tb = new TextBuilder();
                tb.p(tc.javaSource);
                tb.p("__INCLUDED_TAG_TYPES__").p(tc.serializeIncludeTagTypes());
                tb.p("__INCULDED_TEMPLATE_CLASS_NAME_LIST__").p(tc.refreshIncludeTemplateClassNames())
                        .p("__IMPORT_PATH_LIST__");
                if (tc.importPaths == null) {
                    tc.importPaths = new CopyOnWriteArraySet<String>();
                }
                if (tc.importPaths.isEmpty()) {
                    tc.importPaths.add("java.lang");
                }
                boolean first = true;
                for (String s : tc.importPaths) {
                    if (!first) {
                        tb.p(";");
                    } else {
                        first = false;
                    }
                    tb.p(s);
                }
                os.write(tb.toString().getBytes("utf-8"));
            } // else the tc is an inner class thus we don't have javaSource at all

            // --- cache byte code
            os.write(0);
            //if (null != tc.enhancedByteCode) {
            os.write(tc.enhancedByteCode);

            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build a hash of the source code.
     * To efficiently track source code modifications.
     */
    String hash(TemplateClass tc) {
        try {
            //Object enhancer = engine.conf().byteCodeEnhancer();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update((engine.version() + tc.getTemplateSource(true)).getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < digest.length; ++i) {
                int value = digest[i];
                if (value < 0) {
                    value += 256;
                }
                builder.append(Integer.toHexString(value));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String cacheFileName(TemplateClass tc, String suffix) {
        return tc.name0() + suffix;
    }

    private File getCacheFile(String fileName) {
        RythmConfiguration conf = engine.conf();
        if (conf.loadPrecompiled() || conf.precompileMode()) {
            URI uri = conf.get(RythmConfigurationKey.HOME_PRECOMPILED);
            File precompileDir = new File(uri);
            return new File(precompileDir, fileName);
        } else {
            File f = new File(conf.tmpDir(), fileName);
            return f;
        }
    }

    /**
     * Retrieve the real file that will be used as cache.
     */
    File getCacheFile(TemplateClass tc) {
        String id = cacheFileName(tc, ".rythm");
        return getCacheFile(id);
    }

    File getCacheSourceFile(TemplateClass tc) {
        String id = cacheFileName(tc, ".java");
        return getCacheFile(id);
    }

}
