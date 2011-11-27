package com.greenlaw110.rythm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.greenlaw110.rythm.internal.TemplateCompiler.CompiledTemplate;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.IO;

public class TemplateManager {

    private final File dst;
    private final File src;

    public TemplateManager(File destDir, File sourceDir) {
        this.dst = destDir;
        src = sourceDir;
    }
    
    public ITemplate process(String filename) throws Exception {
        CompiledTemplate ct = compile(filename, true);
        return ct.template();
    }
    
    private CompiledTemplate compile(String filename, boolean writeToDisk) {
        return compile(filename, writeToDisk, false);
    }
    
    private CompiledTemplate compile(String filename, boolean writeToDisk, boolean overwriteExisting) {
        File srcFile = new File(src, filename);
        CompiledTemplate ct = Rythm.cache().get(srcFile.getAbsolutePath());
        if (null != ct) return ct;

        // strip suffix, if any
        int pPos = filename.indexOf('.');
        String className = (pPos < 0 ? filename : filename.substring(0, pPos));
        File dstFile = new File(dst, className + ".java");
        if (!overwriteExisting && dstFile.canRead()) {
            ct = new CompiledTemplate(IO.readContentAsString(dstFile), className);
            Rythm.cache().set(srcFile.getAbsolutePath(), ct);
        } else {
            ct = Rythm.compile(srcFile, className);
            if (writeToDisk) {
                File pkgDir = dstFile.getParentFile();
                pkgDir.mkdirs();
                IO.writeContent(ct.getSourceCode(), dstFile);
            }
        }
        
        return ct;
    }

    public void generateSource(String filename, boolean overwriteExisting) throws IOException {
        compile(filename, true, overwriteExisting);
    }

    public void generateSource(String filename) throws IOException {
        compile(filename, true);
    }

    private static void showHelp() {
        System.out
                .println("Usage: java org.jamon.TemplateProcessor <args> templatePath*");
        System.out.println("  Arguments:");
        System.out.println("  -h|--help         - print this help");
        System.out.println("  -d|--directories  - treat paths as directories, "
                + "                      and parse all .jamon files therein");
        System.out.println("  " + DESTDIR
                + "<path>  - path to where compiled .java files go (required)");
        System.out.println("  " + SRCDIR
                + "<path>   - path to template directory");

    }

    private static final String DESTDIR = "--destDir=";

    private static final String SRCDIR = "--srcDir=";
    
    public static void main(String[] args) throws Exception {
        TemplateManager tp = new TemplateManager(
                new File("T:\\tmp\\template-engine-benchmarks\\java\\src"), 
                new File("T:\\tmp\\template-engine-benchmarks\\java\\templates"));
        tp.generateSource("stocks.rythm.html");
        for (int i = 0; i <2; ++i) {
            tp.process("stocks.rythm.html");
        }
    }

    public static void main1(String[] args) {
        try {
            int arg = 0;
            boolean processDirectories = false;
            File sourceDir = new File(".");
            File destDir = null;
            while (arg < args.length && args[arg].startsWith("-")) {
                if ("-h".equals(args[arg]) || "--help".equals(args[arg])) {
                    showHelp();
                    System.exit(0);
                } else if ("-d".equals(args[arg])
                        || "--directories".equals(args[arg])) {
                    processDirectories = true;
                } else if (args[arg].startsWith(DESTDIR)) {
                    destDir = new File(args[arg].substring(DESTDIR.length()));
                } else if (args[arg].startsWith(SRCDIR)) {
                    sourceDir = new File(args[arg].substring(SRCDIR.length()));
                } else {
                    System.err.println("Unknown option: " + args[arg]);
                    showHelp();
                    System.exit(1);
                }
                arg++;
            }
            if (destDir == null) {
                System.err.println("You must specify " + DESTDIR);
                showHelp();
                System.exit(1);
                return; // silence warning about possibly null destDir
            }

            destDir.mkdirs();
            if (!destDir.exists() || !destDir.isDirectory()) {
                throw new IOException("Unable to create destination dir "
                        + destDir);
            }

            TemplateManager processor = new TemplateManager(destDir, sourceDir);

            while (arg < args.length) {
                if (processDirectories) {
                    String directoryName = args[arg++];
                    String fullPath = sourceDir + directoryName;
                    File directory = new File(fullPath);
                    if (!directory.isDirectory()) {
                        System.err.println(fullPath + " is not a directory");
                    }
                    File[] files = directory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File p_dir, String name) {
                            return name.endsWith(".rythm") || name.endsWith(".rythm.html");
                        }
                    });
                    for (int i = 0; i < files.length; i++) {
                        processor.generateSource(directoryName + "/"
                                + files[i].getName());
                    }
                } else {
                    processor.generateSource(args[arg++]);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
