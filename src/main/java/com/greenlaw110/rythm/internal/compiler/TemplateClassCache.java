package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.io.*;
import java.security.MessageDigest;
import java.util.HashSet;

/**
 * Used to speed up compilation time
 */
public class TemplateClassCache {
    private static final ILogger logger = Logger.get(TemplateClassCache.class);

    private final RythmEngine engine;

    public TemplateClassCache(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
    }
    
    /**
     * is class cache enabled on the {@link #engine} instance 
     */
    private boolean enabled() {
        if (engine.mode.isDev() || engine.conf.loadPrecompiled()) {
            return true;
        }
        if (engine.isPrecompiling() && !engine.conf.disableFileWrite()) {
            return true;
        }
        return false;
    }

    /**
     * Delete the bytecode
     *
     * @param tc The template class
     */
    public void deleteCache(TemplateClass tc) {
        try {
            File f = getCacheFile(tc);
            if (f.exists()) {
                f.delete();
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
        if (!enabled()) {
            return;
        }
        try {
            File f = getCacheFile(tc);
            if (!f.exists() || !f.canRead()) return;
            InputStream is = new BufferedInputStream(new FileInputStream(f));

            // --- check hash
            int offset = 0;
            int read = -1;
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
            if(!engine.conf.loadPrecompiled()){
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
                tc.includeTemplateClassNames = sa[0];
                s = sa[1];
                sa = s.split(";");
                tc.importPaths = new HashSet<String>();
                for (String path: sa) {
                    if ("java.lang".equals(path)) continue;
                    tc.importPaths.add(path);
                }
            } // else it must be an inner class

//            read = -1;
//            StringBuilder imported = new StringBuilder();
//            while ((read = is.read()) != 0) {
//                imported.append((char)read);
//                offset++;
//            }
//            if (imported.length() != 0) {
//                String s = imported.toString();
//                String[] sa = s.split(";");
//                tc.importPaths = new HashSet<String>();
//                for (String path: sa) {
//                    if ("java.lang.*".equals(path)) continue;
//                    tc.importPaths.add(path);
//                }
//            }

//            // --- load version info
//            while ((read = is.read()) != 0) {
//                if (engine.isDevMode() && engine.hotswapAgent == null && !tc.isInner()) {
//                    tc.setVersion(read);
//                }
//                offset++;
//            }

//            // -- load included template classes
//            StringBuilder included = new StringBuilder();
//            while((read = is.read()) != 0) {
//                included.append((char)read);
//                offset++;
//            }
//            if (included.length() != 0) {
//                String s = included.toString();
//                tc.includeTemplateClassNames = s;
//            }

            // --- load byte code
            byte[] byteCode = new byte[(int) f.length() - (offset + 2)];
            is.read(byteCode);
            tc.loadCachedByteCode(byteCode);

            is.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheTemplateClassSource(TemplateClass tc) {
        if (!enabled()) {
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
        if (!enabled()) {
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
                    tc.importPaths = new HashSet<String>(0);
                }
                if (tc.importPaths.isEmpty()) {
                    tc.importPaths.add("java.lang");
                }
                boolean  first = true;
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

//            // --- cache class version
//            os.write(0);
//            if (engine.reloadByIncClassVersion() && !tc.isInner()) {
//                // find out version number
//                final String sep = TemplateClass.CN_SUFFIX + "v";
//                String cn = tc.name();
//                int pos = cn.lastIndexOf(sep);
//                String sv = cn.substring(pos + sep.length());
//                int nv = Integer.valueOf(sv);
//                os.write(nv);
//            }

//            // --- cache included template class names
//            os.write(0);
//            tc.refreshIncludeTemplateClassNames();
//            os.write(tc.includeTemplateClassNames.getBytes("utf-8"));

            //}

//            // -- cache import paths
//            os.write(0);
//            if (tc.importPaths == null) {
//                tc.importPaths = new HashSet<String>(0);
//                tc.importPaths.add("java.lang.*");
//            }
//            TextBuilder tb = new TextBuilder();
//            boolean  first = true;
//            for (String s : tc.importPaths) {
//                if (!first) {
//                    tb.p(";");
//                } else {
//                    first = false;
//                }
//                tb.p(s);
//            }
//            os.write(tb.toString().getBytes("utf-8"));

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
            StringBuffer enhancers = new StringBuffer();
            for (ITemplateClassEnhancer plugin : engine.templateClassEnhancers) {
                enhancers.append(plugin.getClass().getName());
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update((engine.versionSignature() + enhancers.toString() + tc.getTemplateSource(true)).getBytes("utf-8"));
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
        if (engine.conf.loadPrecompiled() || (engine.preCompiling && (null != engine.preCompiledHome() && engine.preCompiledHome().exists()))) {
            return new File(engine.preCompiledHome(), fileName);
        } else {
            File f = new File(engine.tmpDir, fileName);
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
