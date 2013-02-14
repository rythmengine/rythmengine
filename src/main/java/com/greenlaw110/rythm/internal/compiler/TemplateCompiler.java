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
package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.CompileException;
import com.greenlaw110.rythm.extension.IByteCodeHelper;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/01/12
 * Time: 7:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateCompiler {

    private ILogger logger = Logger.get(TemplateCompiler.class);

    private RythmEngine engine() {
        return classCache.engine;
    }

    Map<String, Boolean> packagesCache = new HashMap<String, Boolean>();

    // -- util methods
    private String getTemplateByClassName(String className) {
        TemplateClass tc = engine().classes().getByClassName(className);
        return null == tc ? null : tc.getKey().toString();
    }

    // -- the following code comes from PlayFramework 1.2
    TemplateClassManager classCache;
    Map<String, String> settings;

    /**
     * Try to guess the magic configuration options
     */
    public TemplateCompiler(TemplateClassManager classCache) {
        this.classCache = classCache;
        this.settings = new HashMap<String, String>();
        this.settings.put(CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE);
        this.settings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        this.settings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        this.settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
        this.settings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        this.settings.put(CompilerOptions.OPTION_Encoding, "UTF-8");
        this.settings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
        String javaVersion = CompilerOptions.VERSION_1_5;
        if (System.getProperty("java.version").startsWith("1.6")) {
            javaVersion = CompilerOptions.VERSION_1_6;
        } else if (System.getProperty("java.version").startsWith("1.7")) {
            javaVersion = CompilerOptions.VERSION_1_7;
        }
        this.settings.put(CompilerOptions.OPTION_Source, javaVersion);
        this.settings.put(CompilerOptions.OPTION_TargetPlatform, javaVersion);
        this.settings.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
        this.settings.put(CompilerOptions.OPTION_Compliance, javaVersion);
    }

    /**
     * Something to compile
     */
    final class CompilationUnit implements ICompilationUnit {

        final private String clazzName;
        final private String fileName;
        final private char[] typeName;
        final private char[][] packageName;

        CompilationUnit(String pClazzName) {
            clazzName = pClazzName;
            if (pClazzName.contains("$")) {
                pClazzName = pClazzName.substring(0, pClazzName.indexOf("$"));
            }
            fileName = pClazzName.replace('.', '/') + ".java";
            int dot = pClazzName.lastIndexOf('.');
            if (dot > 0) {
                typeName = pClazzName.substring(dot + 1).toCharArray();
            } else {
                typeName = pClazzName.toCharArray();
            }
            StringTokenizer st = new StringTokenizer(pClazzName, ".");
            packageName = new char[st.countTokens() - 1][];
            for (int i = 0; i < packageName.length; i++) {
                packageName[i] = st.nextToken().toCharArray();
            }
        }

        public char[] getFileName() {
            return fileName.toCharArray();
        }

        public char[] getContents() {
            if (null == classCache) {
                throw new NullPointerException("classCache is null");
            }
            TemplateClass tc = classCache.getByClassName(clazzName);
            if (null == tc) {
                throw new NullPointerException("Error get java source content for " + clazzName + ": template class is null");
            }
            if (null == tc.javaSource) {
                throw new NullPointerException("Error get java source content for " + clazzName + ": java source is null");
            }
            return tc.javaSource.toCharArray();
            //return classCache.getByClassName(clazzName).javaSource.toCharArray();
        }

        public char[] getMainTypeName() {
            return typeName;
        }

        public char[][] getPackageName() {
            return packageName;
        }
    }

    /**
     * Please compile this className
     */
    @SuppressWarnings("deprecation")
    public void compile(String[] classNames) {

        ICompilationUnit[] compilationUnits = new CompilationUnit[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            compilationUnits[i] = new CompilationUnit(classNames[i]);
        }
        IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.exitOnFirstError();
        IProblemFactory problemFactory = new DefaultProblemFactory(Locale.ENGLISH);

        /**
         * To find types ...
         */
        INameEnvironment nameEnvironment = new INameEnvironment() {

            public NameEnvironmentAnswer findType(final char[][] compoundTypeName) {
                final StringBuffer result = new StringBuffer();
                for (int i = 0; i < compoundTypeName.length; i++) {
                    if (i != 0) {
                        result.append('.');
                    }
                    result.append(compoundTypeName[i]);
                }
                return findType(result.toString());
            }

            public NameEnvironmentAnswer findType(final char[] typeName, final char[][] packageName) {
                final StringBuffer result = new StringBuffer();
                for (int i = 0; i < packageName.length; i++) {
                    result.append(packageName[i]);
                    result.append('.');
                }
                result.append(typeName);
                return findType(result.toString());
            }

            private NameEnvironmentAnswer findStandType(final String name) throws ClassFormatException {
                RythmEngine engine = engine();
                IByteCodeHelper helper = engine.conf().byteCodeHelper();
                byte[] bytes = engine.classLoader().getClassDefinition(name);
                if (null == bytes && null != helper) {
                    bytes = helper.findByteCode(name);
                }
                if (bytes != null) {
                    ClassFileReader classFileReader = new ClassFileReader(bytes, name.toCharArray(), true);
                    return new NameEnvironmentAnswer(classFileReader, null);
                }
                return null;
            }

            private NameEnvironmentAnswer findType(final String name) {
                try {
                    if (!name.contains(TemplateClass.CN_SUFFIX)) {
                        return findStandType(name);
                    }

                    char[] fileName = name.toCharArray();
                    TemplateClass templateClass = classCache.getByClassName(name);

                    // TemplateClass exists
                    if (templateClass != null) {
                        if (templateClass.javaByteCode != null) {
                            ClassFileReader classFileReader = new ClassFileReader(templateClass.javaByteCode, fileName, true);
                            return new NameEnvironmentAnswer(classFileReader, null);
                        }
                        // Cascade compilation
                        ICompilationUnit compilationUnit = new CompilationUnit(name);
                        return new NameEnvironmentAnswer(compilationUnit, null);
                    }

                    // So it's a standard class
                    return findStandType(name);
                } catch (ClassFormatException e) {
                    // Something very very bad
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isPackage(char[][] parentPackageName, char[] packageName) {
                // Rebuild something usable
                StringBuilder sb = new StringBuilder();
                if (parentPackageName != null) {
                    for (char[] p : parentPackageName) {
                        sb.append(new String(p));
                        sb.append(".");
                    }
                }
                sb.append(new String(packageName));
                String name = sb.toString();
                if (packagesCache.containsKey(name)) {
                    return packagesCache.get(name).booleanValue();
                }
                // Check if thera a .java or .class for this ressource
                if (engine().classLoader().getClassDefinition(name) != null) {
                    packagesCache.put(name, false);
                    return false;
                }
                if (engine().classes().getByClassName(name) != null) {
                    packagesCache.put(name, false);
                    return false;
                }
                packagesCache.put(name, true);
                return true;
            }

            public void cleanup() {
            }
        };

        final RythmEngine engine = engine();

        /**
         * Compilation result
         */
        ICompilerRequestor compilerRequestor = new ICompilerRequestor() {

            public void acceptResult(CompilationResult result) {
                // If error
                if (result.hasErrors()) {
                    for (IProblem problem : result.getErrors()) {
                        int line = problem.getSourceLineNumber();
                        int column = problem.getSourceStart();
                        String message = problem.getMessage();
                        throw CompileException.compilerException(String.valueOf(result.compilationUnit.getMainTypeName()), line, message);
                    }
                }
                // Something has been compiled
                ClassFile[] clazzFiles = result.getClassFiles();
                for (int i = 0; i < clazzFiles.length; i++) {
                    final ClassFile clazzFile = clazzFiles[i];
                    final char[][] compoundName = clazzFile.getCompoundName();
                    final StringBuffer clazzName = new StringBuffer();
                    for (int j = 0; j < compoundName.length; j++) {
                        if (j != 0) {
                            clazzName.append('.');
                        }
                        clazzName.append(compoundName[j]);
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("Compiled %s", getTemplateByClassName(clazzName.toString()));
                    }

                    String cn = clazzName.toString();
                    TemplateClass tc = classCache.getByClassName(cn);
                    if (null == tc) {
                        int pos = cn.indexOf("$");
                        if (-1 != pos) {
                            // inner class
                            TemplateClass root = classCache.getByClassName(cn.substring(0, pos));
                            tc = TemplateClass.createInnerClass(cn, clazzFile.getBytes(), root);
                            tc.delayedEnhance(root);
                            classCache.add(tc);
                        } else {
                            throw new RuntimeException("Cannot find class by name: " + cn);
                        }
                    } else {
                        tc.compiled(clazzFile.getBytes());
                    }
                }
            }
        };

        /**
         * The JDT compiler
         */
        Compiler jdtCompiler = new Compiler(nameEnvironment, policy, settings, compilerRequestor, problemFactory) {

            @Override
            protected void handleInternalException(Throwable e, CompilationUnitDeclaration ud, CompilationResult result) {
            }
        };

        // Go !
        jdtCompiler.compile(compilationUnits);

    }
}
