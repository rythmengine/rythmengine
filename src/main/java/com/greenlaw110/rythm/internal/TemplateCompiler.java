package com.greenlaw110.rythm.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.compile.CharSequenceJavaFileObject;
import com.greenlaw110.rythm.internal.compile.ClassFileManager;
import com.greenlaw110.rythm.template.ITemplate;

/**
 * Template compiler compile template into Java class
 * 
 * @author luog
 * 
 */
public class TemplateCompiler {

    // private static class JavaSourceFromString extends SimpleJavaFileObject {
    // final String code;
    //
    // JavaSourceFromString(String name, String code) {
    // super(URI.create("string:///" + name.replace('.', '/')
    // + Kind.SOURCE.extension), Kind.SOURCE);
    // this.code = code;
    // }
    //
    // @Override
    // public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    // return code;
    // }
    // }

    static class MemorySource extends SimpleJavaFileObject {
        private String src;

        public MemorySource(String name, String src) {
            super(URI.create("file:///" + name + ".java"), Kind.SOURCE);
            this.src = src;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return src;
        }

        public OutputStream openOutputStream() {
            throw new IllegalStateException();
        }

        public InputStream openInputStream() {
            return new ByteArrayInputStream(src.getBytes());
        }
    }

    static class SpecialJavaFileManager extends ForwardingJavaFileManager {
        private SpecialClassLoader xcl;

        public SpecialJavaFileManager(StandardJavaFileManager sjfm,
                SpecialClassLoader xcl) {
            super(sjfm);
            this.xcl = xcl;
        }

        public JavaFileObject getJavaFileForOutput(Location location,
                String name, JavaFileObject.Kind kind, FileObject sibling)
                throws IOException {
            MemoryByteCode mbc = new MemoryByteCode(name);
            xcl.addClass(name, mbc);
            return mbc;
        }

        public ClassLoader getClassLoader(Location location) {
            return xcl;
        }

    }

    static class MemoryByteCode extends SimpleJavaFileObject {
        private ByteArrayOutputStream baos;

        public MemoryByteCode(String name) {
            super(URI.create("byte:///" + name + ".class"), Kind.CLASS);
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            throw new IllegalStateException();
        }

        public OutputStream openOutputStream() {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        public InputStream openInputStream() {
            throw new IllegalStateException();
        }

        public byte[] getBytes() {
            return baos.toByteArray();
        }
    }

    static class SpecialClassLoader extends ClassLoader {
        private Map<String, MemoryByteCode> m = new HashMap<String, MemoryByteCode>();

        protected Class<?> findClass(String name) throws ClassNotFoundException {
            MemoryByteCode mbc = m.get(name);
            if (mbc == null) {
                mbc = m.get(name.replace(".", "/"));
                if (mbc == null) {
                    return null == Rythm.classLoader ? super.findClass(name)
                            : Rythm.classLoader.loadClass(name);
                }
            }
            return defineClass(name, mbc.getBytes(), 0, mbc.getBytes().length);
        }

        public void addClass(String name, MemoryByteCode mbc) {
            m.put(name, mbc);
        }
    
    }

    private CodeGenerator codeGen = new CodeGenerator();

    public static String uniqueClassName() {
        return "R" + UUID.randomUUID().toString().replace('-', '_');
    }

    public static final class CompiledTemplate {
        private String src;
        private String className;
        private Class<? extends ITemplate> cls;

        public CompiledTemplate(String source, String className) {
            src = source;
            this.className = className;
        }

        public String getSourceCode() {
            return src;
        }

        public String getClassName() {
            return className;
        }

        @SuppressWarnings("unchecked")
        public Class<? extends ITemplate> getTemplateClass() {
            if (null == cls) {
                try {
                    cls = (Class<? extends ITemplate>) Class.forName(className
                            .replace('/', '.'));
                } catch (Exception e) {
                    // ignore;
                }

                if (null == cls) {
                    cls = compile_(src, className);
                    if (null == cls) {
                        src = null;
                    }
                }
            }
            return cls;
        }

        public ITemplate template() {
            Class<? extends ITemplate> c = getTemplateClass();
            if (null == c)
                return null;
            try {
                return c.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CompiledTemplate compile(String template, String className) {
        return compile(template, className, true);
    }

    public CompiledTemplate compile(String template) {
        return compile(template, true);
    }

    public CompiledTemplate compile(String template, boolean requireCompiled) {
        return compile(template, uniqueClassName(), true);
    }

    public CompiledTemplate compile(String template, String className,
            boolean requireCompiled) {
        if (null == className)
            className = uniqueClassName();
        String src = codeGen.generate(template, className);
        // System.out.println(src);
        CompiledTemplate ct = new CompiledTemplate(src, className);
        if (requireCompiled) {
            ct.getTemplateClass();
        }
        return ct;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ITemplate> compile_(String srcCode,
            String className) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //JavaCompiler compiler = new EclipseCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
//        StandardJavaFileManager sjfm = compiler.getStandardFileManager(null,
//                null, null);
//        SpecialClassLoader cl = new SpecialClassLoader();
//        SpecialJavaFileManager fileManager = new SpecialJavaFileManager(sjfm,
//                cl);
        // final JavaFileManager fileManager = new
        // CustomClassloaderJavaFileManager(cl, sjfm);
        JavaFileManager fileManager = new
                ClassFileManager(compiler
                        .getStandardFileManager(null, null, null));
        List options = new ArrayList();
        options.addAll(Arrays.asList("-classpath",System.getProperty("java.class.path") + File.pathSeparator + Rythm.classPath));
        //List compilationUnits = Arrays.asList(new MemorySource(className, srcCode));
        List compilationUnits = new ArrayList<JavaFileObject>();
        compilationUnits.add(new CharSequenceJavaFileObject(className, srcCode));
        DiagnosticListener dianosticListener = null;
        Iterable classes = null;
        Writer out = new PrintWriter(System.err);
        JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager,
                dianosticListener, options, classes, compilationUnits);
        boolean success = task.call();

        for (@SuppressWarnings("rawtypes")
        Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic.getCode());
            System.out.println(diagnostic.getKind());
            System.out.println(diagnostic.getPosition());
            System.out.println(diagnostic.getStartPosition());
            System.out.println(diagnostic.getEndPosition());
            System.out.println(diagnostic.getSource());
            System.out.println(diagnostic.getMessage(null));

        }
        if (success) {
            try {
                //return (Class<? extends ITemplate>) getClass().getClassLoader().findClass(className.replace('/', '.'));
                return (Class<? extends ITemplate>) fileManager.getClassLoader(null).loadClass(className.replace('/', '.'));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;

    }

}
