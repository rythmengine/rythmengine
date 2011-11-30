package com.greenlaw110.rythm;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.SourceFileScanner;


public class RythmCompileTask extends MatchingTask {

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public void setSrcdir(File srcDir) {
        this.srcDir = srcDir;
    }

    public void setClasspath(Path classpath) throws IOException {
        String[] paths = classpath.list();
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = new URL("file", null, paths[i]);
        }
    }

    public void setListFiles(boolean listFiles) {
        this.listFiles = listFiles;
    }

    @Override
    public void execute() throws BuildException {
        // Copied from org.apache.tools.ant.taskdefs.Javac below

        // first off, make sure that we've got a srcdir

        if (srcDir == null) {
            throw new BuildException("srcdir attribute must be set!",
                    getLocation());
        }
        if (destDir == null) {
            throw new BuildException("destdir attribute must be set!",
                    getLocation());
        }

        if (!srcDir.exists() && !srcDir.isDirectory()) {
            throw new BuildException("source directory \"" + srcDir
                    + "\" does not exist or is not a directory", getLocation());
        }

        destDir.mkdirs();
        if (!destDir.exists() || !destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + destDir
                    + "\" does not exist or is not a directory", getLocation());
        }

        if (!srcDir.exists()) {
            throw new BuildException("srcdir \"" + srcDir
                    + "\" does not exist!", getLocation());
        }

        SourceFileScanner sfs = new SourceFileScanner(this);
        File[] files = sfs
                .restrictAsFiles(
                        getDirectoryScanner(srcDir).getIncludedFiles(), srcDir,
                        destDir, new RythmFileNameMapper());

        if (files.length > 0) {
            log("Processing " + files.length + " template"
                    + (files.length == 1 ? "" : "s") + " to " + destDir);

            TemplateManager processor = new TemplateManager(destDir, srcDir);

            for (int i = 0; i < files.length; i++) {
                if (listFiles) {
                    log(files[i].getAbsolutePath());
                }

                try {
                    processor.generateSource(relativize(files[i]), true);
                } catch (Exception e) {
                    throw new BuildException("Error compile Rythm source file: " + files[i], e);
                }
            }
        }
    }

    private static class RythmFileNameMapper implements FileNameMapper {
        public void setFrom(String from) {
        }

        public void setTo(String to) {
        }

        public String[] mapFileName(String sourceName) {
            String targetFileName = sourceName;
            int i = targetFileName.lastIndexOf('.');
            if (i > 0 && targetFileName.indexOf(".rythm.") != -1) {
                targetFileName = targetFileName.substring(0, i);
                return new String[] { targetFileName + ".java"};
            }
            return new String[0];
        }
    }

    private String relativize(File file) {
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Paths must be all absolute");
        }
        String filePath = file.getPath();
        String basePath = srcDir.getAbsoluteFile().toString(); // FIXME !?

        if (filePath.startsWith(basePath)) {
            return filePath.substring(basePath.length() + 1);
        } else {
            throw new IllegalArgumentException(file + " is not based at "
                    + basePath);
        }
    }

    private File destDir = null;
    private File srcDir = null;
    private boolean listFiles = false;
}
