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
package org.rythmengine.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.RythmEngine.TemplateTestResult;
import org.rythmengine.Sandbox;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.exception.ParseException;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.internal.compiler.ParamTypeInferencer;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.internal.dialect.BasicRythm;
import org.rythmengine.internal.dialect.SimpleRythm;
import org.rythmengine.internal.parser.BlockCodeToken;
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.NotRythmTemplateException;
import org.rythmengine.internal.parser.build_in.BlockToken;
import org.rythmengine.internal.parser.build_in.CompactStateToken;
import org.rythmengine.internal.parser.build_in.InvokeTemplateParser;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.StringTemplateResource;
import org.rythmengine.template.JavaTagBase;
import org.rythmengine.template.TagBase;
import org.rythmengine.template.TemplateBase;
import org.rythmengine.utils.S;
import org.rythmengine.utils.TextBuilder;

import com.stevesoft.pat.Regex;


public class CodeBuilder extends TextBuilder {

    protected final static ILogger logger = Logger.get(CodeBuilder.class);

    private int renderArgCounter = 0;

    public static class RenderArgDeclaration implements Comparable<RenderArgDeclaration> {
        public int no;
        public String name;
        public String type;
        public String defVal;
        public int lineNo;
        
        private static final Map<String, String> byPrimitive; static {
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("int", "Integer");
            m.put("long", "Long");
            m.put("short", "Short");
            m.put("byte", "Byte");
            m.put("float", "Float");
            m.put("double", "Double");
            m.put("char", "Character");
            m.put("boolean", "Boolean");
            byPrimitive = m;
        }
        
        private static char DEF_CHAR;

        public String objectType() {
            String s = byPrimitive.get(type);
            return null != s ? s : toNonGeneric(type);
        }
        
        private static final Map<String, String> nullVals; static {
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("int", "0");
            m.put("long", "0L");
            m.put("short", "0");
            m.put("byte", "0");
            m.put("float", "0f");
            m.put("double", "0d");
            m.put("char", String.valueOf(DEF_CHAR));
            m.put("boolean", "false");
            nullVals = m;
        }
        
        public String nullVal() {
            String s = nullVals.get(type);
            return (null == s) ? "null" : s;
        }
        
        public RenderArgDeclaration(int lineNo, String type, String name) {
            this(-1, lineNo, type, name);
        }

        public RenderArgDeclaration(int lineNo, String type, String name, String defVal) {
            this(-1, lineNo, type, name, defVal);
        }

        public RenderArgDeclaration(int no, int lineNo, String type, String name) {
            this(no, lineNo, type, name, null);
        }

        public RenderArgDeclaration(int no, int lineNo, String type, String name, String defVal) {
            this.no = no;
            this.lineNo = lineNo;
            this.name = name;
            this.type = ParamTypeInferencer.typeTransform(type);
            defVal = defValTransform(type, defVal);
            this.defVal = null == defVal ? defVal(type) : defVal;
        }

        private static String defValTransform(String type, String defVal) {
            if (S.isEmpty(defVal)) return null;
            if ("String".equalsIgnoreCase(type) && "null".equalsIgnoreCase(defVal)) return "\"\"";
            if ("boolean".equalsIgnoreCase(type)) defVal = defVal.toLowerCase();
            if ("long".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "L";
            if ("float".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "f";
            if ("double".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "d";
            if ("short".equalsIgnoreCase(type) && defVal.matches("[0-9]+]")) return "((short)" + defVal + ")";
            if ("byte".equalsIgnoreCase(type) && defVal.matches("[0-9]+]")) return "((byte)" + defVal + ")";
            return defVal;
        }

        private static String defVal(String type) {
            if (type.equalsIgnoreCase("String")) {
                return "\"\"";
            } else if (type.equalsIgnoreCase("boolean"))
                return "false";
            else if (type.equalsIgnoreCase("int"))
                return "0";
            else if (type.equalsIgnoreCase("long"))
                return "0L";
            else if (type.equals("char") || type.equals("Character"))
                return "(char)0";
            else if (type.equalsIgnoreCase("byte"))
                return "(byte)0";
            else if (type.equalsIgnoreCase("short"))
                return "(short)0";
            else if (type.equalsIgnoreCase("float"))
                return "0f";
            else if (type.equalsIgnoreCase("double"))
                return "0d";
            return "null";
        }

        @Override
        public int compareTo(RenderArgDeclaration o) {
            return no - o.no;
        }

    }

    private RythmEngine engine;

    public RythmEngine engine() {
        return engine;
    }

    private RythmConfiguration conf;

    private TemplateParser parser;
    private TemplateClass templateClass;
    private boolean isNotRythmTemplate = false;
    public ICodeType templateDefLang;

    public boolean isRythmTemplate() {
        return !isNotRythmTemplate;
    }

    protected String tmpl;
    private String cName;
    public String includingCName;
    private String pName;
    private String tagName;

    private String initCode = null;
    private String finalCode = null;

    private Set<InlineClass> inlineClasses = new HashSet<InlineClass>();
    private List<String> staticCodes = new ArrayList<String>();

    public void setInitCode(String code) {
        if (S.empty(initCode)) {
            initCode = code;
        } else {
            initCode = initCode + ";\n" + code;
        }
    }

    public void setFinalCode(String code) {
        if (S.empty(finalCode)) {
            finalCode = code;
        } else {
            finalCode = finalCode + ";\n" + code;
        }
    }

    private String extended; // the cName of the extended template

    protected String extended() {
        String defClass = TagBase.class.getName();
        return null == extended ? defClass : extended;
    }

    private String extendedResourceMark() {
        TemplateClass tc = extendedTemplateClass;
        return (null == tc) ? "" : String.format("//<extended_resource_key>%s</extended_resource_key>", tc.templateResource.getKey());
    }

    private TemplateClass extendedTemplateClass;

    public TemplateClass getExtendedTemplateClass() {
        return extendedTemplateClass;
    }

    private InvokeTemplateParser.ParameterDeclarationList extendArgs = null;
    public Set<String> imports = new HashSet<String>();
    private int extendDeclareLineNo = -1;
    // <argName, argClass>
    public Map<String, RenderArgDeclaration> renderArgs = new LinkedHashMap<String, RenderArgDeclaration>();
    private List<Token> builders = new ArrayList<Token>();
    private List<Token> builders() {
        if (macroStack.empty()) return builders;
        String macro = macroStack.peek();
        List<Token> bl = macros.get(macro);
        if (null == bl) {
            bl = new ArrayList<Token>();
            macros.put(macro, bl);
        }
        return bl;
    }
    public boolean isLastBuilderLiteral() {
        List<Token> bl = builders();
        for (int i = bl.size() - 1; i >= 0; --i) {
            Token tb = bl.get(i);
            if (tb instanceof Token.StringToken) {
                if (((Token.StringToken)tb).empty()) continue;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public TemplateClass getTemplateClass() {
        return templateClass;
    }

    private boolean simpleTemplate() {
        return parser.getDialect() instanceof SimpleRythm;
    }

    // public because event handler needs this method
    public boolean basicTemplate() {
        return parser.getDialect() instanceof BasicRythm;
    }

    transient public IDialect requiredDialect = null;

    public CodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine, IDialect requiredDialect) {
        tmpl = template;
        className = className.replace('/', '.');
        this.tagName = (null == tagName) ? className : tagName;
        cName = className;
        int i = className.lastIndexOf('.');
        if (-1 < i) {
            cName = className.substring(i + 1);
            pName = className.substring(0, i);
        }
        this.engine = null == engine ? Rythm.engine() : engine;
        this.conf = this.engine.conf();
        this.requiredDialect = requiredDialect;
        this.templateClass = templateClass;
        ICodeType type = templateClass.codeType;
        this.templateDefLang = type;
        String tmpl = RythmEvents.ON_PARSE.trigger(this.engine, this);
        this.tmpl = tmpl;
        this.parser = new TemplateParser(this);
    }

    /**
     * Reset to the state before construction
     */
    public void clear() {
        buffer().ensureCapacity(0);
        this.engine = null;
        this.tmpl = null;
        this.cName = null;
        this.pName = null;
        this.tagName = null;
        this.initCode = null;
        this.finalCode = null;
        this.extended = null;
        this.extendedTemplateClass = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.imports.clear();
        this.extendDeclareLineNo = 0;
        this.renderArgs.clear();
        this.builders.clear();
        this.parser = null;
        this.templateClass = null;
        this.inlineClasses.clear();
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
        this.importLineMap.clear();
        this.logTime = false;
        this.macros.clear();
        this.macroStack.clear();
        this.buildBody = null;
        this.templateDefLang = null;
        this.staticCodes.clear();
    }

    /**
     * Rewind to the state when construction finished
     */
    public void rewind() {
        renderArgCounter = 0;
        this.initCode = null;
        this.finalCode = null;
        this.extended = null;
        this.extendedTemplateClass = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.imports.clear();
        this.extendDeclareLineNo = 0;
        this.renderArgs.clear();
        this.builders.clear();
        this.inlineClasses.clear();
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
        this.importLineMap.clear();
        this.logTime = false;
        this.macros.clear();
        this.macroStack.clear();
        this.buildBody = null;
        this.staticCodes.clear();
    }

    public void merge(CodeBuilder codeBuilder) {
        if (null == codeBuilder) return;
        this.imports.addAll(codeBuilder.imports);
        for (InlineTag tag : codeBuilder.inlineTags) {
            inlineTags.add(tag.clone(this));
        }
        for (InlineClass clz : codeBuilder.inlineClasses) {
            inlineClasses.add(clz.clone(this));
        }
        this.initCode = new StringBuilder(S.toString(this.initCode)).append(S.toString(codeBuilder.initCode)).toString();
        this.finalCode = new StringBuilder(S.toString(this.finalCode)).append(S.toString(codeBuilder.finalCode)).toString();
        this.renderArgs.putAll(codeBuilder.renderArgs);
        this.importLineMap.putAll(codeBuilder.importLineMap);
        this.staticCodes.addAll(codeBuilder.staticCodes);
        renderArgCounter += codeBuilder.renderArgCounter;
    }

    public CodeBuilder() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String className() {
        return cName;
    }

    public String includingClassName() {
        return null == includingCName ? cName : includingCName;
    }

    private Map<String, Integer> importLineMap = new HashMap<String, Integer>();

    public void addImport(String imprt, int lineNo) {
        imports.add(imprt);
        if (imprt.endsWith(".*")) {
            imprt = imprt.substring(0, imprt.lastIndexOf(".*"));
            templateClass.importPaths.add(imprt);
        }
        importLineMap.put(imprt, lineNo);
    }

    public static class InlineClass {
        String className;
        String body;

        InlineClass(String className, String body) {
            this.className = className;
            this.body = body;
        }

        InlineClass clone(CodeBuilder newCaller) {
            InlineClass clz = new InlineClass(className, body);
            return clz;
        }

        @Override
        public int hashCode() {
            return 37 + className.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof InlineClass) {
                return ((InlineClass) obj).className.equals(className);
            }
            return false;
        }
    }

    public static class InlineTag {
        String tagName;
        String signature;
        String retType = "void";
        String body;
        boolean autoRet = false;
        List<Token> builders = new ArrayList<Token>();

        InlineTag(String name, String ret, String sig, String body) {
            tagName = name;
            signature = sig;
            retType = null == ret ? "void" : ret;
            this.body = body;
        }

        public boolean noArgs() {
            return S.empty(signature) || signature.matches("\\(\\s*\\)");
        }

        InlineTag clone(CodeBuilder newCaller) {
            InlineTag tag = new InlineTag(tagName, retType, signature, body);
            tag.builders.clear();
            for (Token tb : builders) {
                Token newTb = tb.clone(newCaller);
                tag.builders.add(newTb);
            }
            tag.autoRet = autoRet;
            return tag;
        }

        @Override
        public int hashCode() {
            return (37 + tagName.hashCode()) * 31 + signature.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof InlineTag) {
                InlineTag that = (InlineTag) obj;
                return S.isEqual(that.signature, this.signature) && S.isEqual(that.tagName, this.tagName);
            }
            return false;
        }
    }

    private Set<InlineTag> inlineTags = new HashSet<InlineTag>();

    public boolean hasInlineTagWithoutArgument(String tagName) {
        for (InlineTag tag : inlineTags) {
            if (S.eq(tag.tagName, tagName) && tag.noArgs()) {
                return true;
            }
        }
        return false;
    }

    public boolean needsPrint(String tagName) {
        return templateClass.returnObject(tagName);
    }

    private Stack<List<Token>> inlineTagBodies = new Stack<List<Token>>();

    public void addStaticCode(String codeSnippet) {
        staticCodes.add(codeSnippet);
    }

    public InlineClass defClass(String className, String body) {
        className = className.trim();
        InlineClass clz = new InlineClass(className, body);
        if (inlineClasses.contains(clz)) {
            throw new ParseException(engine, templateClass, parser.currentLine(), "inline class already defined: %s", className);
        }
        inlineClasses.add(clz);
        return clz;
    }

    public InlineTag defTag(String tagName, String retType, String signature, String body) {
        tagName = tagName.trim();
        InlineTag tag = new InlineTag(tagName, retType, signature, body);
        if (inlineTags.contains(tag)) {
            throw new ParseException(engine, templateClass, parser.currentLine(), "inline tag already defined: %s", tagName);
        }
        inlineTags.add(tag);
        inlineTagBodies.push(builders);
        builders = tag.builders;
        if ("void".equals(tag.retType)) {
            tag.retType = "org.rythmengine.utils.RawData";
            tag.autoRet = true;
            String code = "StringBuilder __sb = this.getSelfOut();this.setSelfOut(new StringBuilder());";
            builders.add(new CodeToken(code, parser));
        }
        templateClass.setTagType(tagName, tag.retType);
        return tag;
    }

    public void endTag(InlineTag tag) {
        if (inlineTagBodies.empty())
            throw new ParseException(engine, templateClass, parser.currentLine(), "Unexpected tag definition close");
        if (tag.autoRet) {
            builders.add(new CodeToken("String __s = toString();this.setSelfOut(__sb);return s().raw(__s);", parser));
        }
        builders = inlineTagBodies.pop();
    }

    public String addIncludes(String includes, int lineNo, ICodeType codeType) {
        StringBuilder sb = new StringBuilder();
        for (String s : includes.split("[\\s,;:]+")) {
            sb.append(addInclude(s, lineNo, codeType));
        }
        return sb.toString();
    }

    /**
     * add the given include template at the given linenumber
     * @param include
     * @param lineNo
     * @param codeType
     * @return
     */
    public String addInclude(String include, int lineNo, ICodeType codeType) {
        TemplateTestResult testResult = engine.testTemplate(include, templateClass, codeType);
        if (null == testResult) {
            throw new ParseException(engine, templateClass, lineNo, "include template not found: %s", include);
        }
        if (testResult.getError()!=null) {
          String errMsg=testResult.getErrorMessage();
          throw new ParseException(engine, templateClass, lineNo,"including "+include+" creates error\n"+ errMsg);
        }
        String tmplName=testResult.getFullName();
        TemplateBase includeTmpl = (TemplateBase) engine.getRegisteredTemplate(tmplName);
   
        if (includeTmpl instanceof JavaTagBase) {
            throw new ParseException(engine, templateClass, lineNo, "cannot include Java tag: %s", include);
        }
        if (includeTmpl==null) {
          throw new ParseException(engine, templateClass, lineNo, "include for template failed: %s ", include);
        }
        TemplateClass includeTc = includeTmpl.__getTemplateClass(false);
        includeTc.buildSourceCode(includingClassName());
        merge(includeTc.codeBuilder);
        templateClass.addIncludeTemplateClass(includeTc);
        return includeTc.codeBuilder.buildBody;
    }
    
    public String addInlineInclude(String inlineTemplate, int lineNo) {
        TemplateClass includeTc = new TemplateClass(new StringTemplateResource(inlineTemplate), engine, false);
        includeTc.buildSourceCode(includingClassName());
        merge(includeTc.codeBuilder);
        return includeTc.codeBuilder.buildBody;
    }

    public void setExtended(Class<? extends TemplateBase> c) {
        this.extended = c.getName();
    }

    public void setExtended(String extended, InvokeTemplateParser.ParameterDeclarationList args, int lineNo) {
        if (simpleTemplate()) {
            throw new ParseException(engine, templateClass, lineNo, "Simple template does not allow to extend layout template");
        }
        if (null != this.extended) {
            throw new ParseException(engine, templateClass, lineNo, "Extended template already declared");
        }
        TemplateTestResult testResult = engine.testTemplate(extended, templateClass, null);
        String fullName=testResult.getFullName();
        if (null == fullName) {
            // try legacy style
            setExtended_deprecated(extended, args, lineNo);
            logger.warn("Template[%s]: Extended template declaration \"%s\" is deprecated, please switch to the new style \"%s\"", templateClass.getKey(), extended, extendedTemplateClass.getTagName());
        } else {
            TemplateBase tb = (TemplateBase) engine.getRegisteredTemplate(fullName);
            TemplateClass tc = tb.__getTemplateClass(false);
            this.extended = tc.name();
            this.extendedTemplateClass = tc;
            this.templateClass.extendedTemplateClass = tc;
            this.engine.addExtendRelationship(tc, this.templateClass);
            this.extendArgs = args;
            this.extendDeclareLineNo = lineNo;
        }
    }

    public void setExtended_deprecated(String extended, InvokeTemplateParser.ParameterDeclarationList args, int lineNo) {
        if (null != this.extended) {
            throw new IllegalStateException("Extended template already declared");
        }
        TemplateClass tc = null;
        String origin = extended;
        if (!extended.startsWith("/")) {
            // relative path ?
            String me = templateClass.getKey().toString();
            int pos = me.lastIndexOf("/");
            if (-1 != pos) extended = me.substring(0, pos) + "/" + extended;
            tc = engine.classes().getByTemplate(extended);
            if (null == tc) {
                ITemplateResource resource = engine.resourceManager().getResource(extended);
                if (resource.isValid()) {
                    tc = new TemplateClass(resource, engine);
                }
            }
        }
        if (null == tc && !extended.startsWith("/")) {
            // it's in class name style ?
            //if (!extended.endsWith(TemplateClass.CN_SUFFIX)) extended = extended + TemplateClass.CN_SUFFIX;
            tc = engine.classes().getByClassName(extended);
        }
        if (null == tc) {
            tc = engine.classes().getByTemplate(origin);
            if (null == tc) {
                ITemplateResource resource = engine.resourceManager().getResource(origin);
                if (resource.isValid()) tc = new TemplateClass(resource, engine);
            }
        }
        if (null == tc) {
            throw new ParseException(engine, templateClass, lineNo, "Cannot find extended template by name \"%s\"", origin);
        }
        this.extended = tc.name();
        this.extendedTemplateClass = tc;
        this.templateClass.extendedTemplateClass = tc;
        this.engine.addExtendRelationship(tc, this.templateClass);
        this.extendArgs = args;
        this.extendDeclareLineNo = lineNo;
    }

    protected boolean logTime = false;

    public void setLogTime() {
        logTime = true;
    }

    public String getRenderArgType(String name) {
        addInferencedRenderArgs();
        RenderArgDeclaration rad = renderArgs.get(name);
        if (null != rad) return rad.type;
        else return null;
    }

    public void addRenderArgs(RenderArgDeclaration declaration) {
        renderArgs.put(declaration.name, declaration);
    }

    public void addRenderArgs(int lineNo, String type, String name, String defVal) {
        renderArgs.put(name, new RenderArgDeclaration(renderArgCounter++, lineNo, type, name, defVal));
    }

    public void addRenderArgs(int lineNo, String type, String name) {
        renderArgs.put(name, new RenderArgDeclaration(renderArgCounter++, lineNo, type, name));
    }

    public void addRenderArgsIfNotDeclared(int lineNo, String type, String name) {
        if (!renderArgs.containsKey(name)) {
            renderArgs.put(name, new RenderArgDeclaration(renderArgCounter++, lineNo, type, name));
        }
    }

    private Map<String, List<Token>> macros = new HashMap<String, List<Token>>();
    private Stack<String> macroStack = new Stack<String>();

    public void pushMacro(String macro) {
        if (macros.containsKey(macro)) {
            throw new ParseException(engine, templateClass, parser.currentLine(), "Macro already defined: %s", macro);
        }
        macroStack.push(macro);
        macros.put(macro, new ArrayList<Token>());
    }

    public void popMacro() {
        if (macroStack.empty()) {
            throw new ParseException(engine, templateClass, parser.currentLine(), "no macro found in stack");
        }
        macroStack.pop();
    }

    public boolean hasMacro(String macro) {
        return macros.containsKey(macro);
    }

    public List<Token> getMacro(String macro) {
        List<Token> list = this.macros.get(macro);
        if (null == list) throw new NullPointerException();
        return list;
    }
    
    public boolean lastIsBlockToken() {
        List<Token> bs = builders();
        for (int i = bs.size() - 1; i >= 0; --i) {
            Token tb = bs.get(i);
            if (tb instanceof BlockCodeToken) return true;
            else if (tb instanceof Token.StringToken) {
                String s = tb.toString();
                if (S.empty(s)) continue;
                else return false;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public boolean removeNextLF = false;
    public void addBuilder(Token builder) {
        if (builder == Token.EMPTY_TOKEN) {
            return;
        }
        Token token = builder;
        if (removeNextLF && token != Token.EMPTY_TOKEN2) {
            if (token.removeLeadingLineBreak()) {
                removeNextLF = false;
            }
        }
        if (token.removeNextLineBreak) {
            removeNextLF = true;
        }
        builders().add(builder);
    }
    
    /**
     * If from the current cursor to last linebreak are all space, then
     * remove all those spaces and the last line break
     */
    public void removeSpaceToLastLineBreak(IContext ctx) {
        boolean shouldRemoveSpace = true;
        List<Token> bl = builders();
        for (int i = bl.size() - 1; i >= 0; --i) {
            TextBuilder tb = bl.get(i);
            if (tb == Token.EMPTY_TOKEN || tb instanceof IDirective) {
                continue;
            }
            if (tb.getClass().equals(Token.StringToken.class)) {
                String s = tb.toString();
                if (s.matches("[ \\t\\x0B\\f]+")) {
                    continue;
                } else if (s.matches("(\\n\\r|\\r\\n|[\\r\\n])")) {
                } else {
                    shouldRemoveSpace = false;
                }
            }
            break;
        }
        if (shouldRemoveSpace) {
            for (int i = bl.size() - 1; i >= 0; --i) {
                TextBuilder tb = bl.get(i);
                if (tb == Token.EMPTY_TOKEN || tb instanceof IDirective) {
                    continue;
                }
                if (tb.getClass().equals(Token.StringToken.class)) {
                    String s = tb.toString();
                    if (s.matches("[ \\t\\x0B\\f]+")) {
                        bl.remove(i);
                        continue;
                    } else if (s.matches("(\\n\\r|\\r\\n|[\\r\\n])")) {
                        bl.remove(i);
                    }
                }
                break;
            }
        }
    }

    /**
     * If from the current cursor till last linebreak are all space, then
     * remove all those spaces and the last line break
     */
    public void removeSpaceTillLastLineBreak(IContext ctx) {
        boolean shouldRemoveSpace = true;
        List<Token> bl = builders();
        for (int i = bl.size() - 1; i >= 0; --i) {
            Token tb = bl.get(i);
            if (tb == Token.EMPTY_TOKEN || tb instanceof IDirective) {
                continue;
            }
            if (tb.getClass().equals(Token.StringToken.class)) {
                String s = tb.toString();
                if (s.matches("[ \\t\\x0B\\f]+")) {
                    continue;
                } else if (s.matches("(\\n\\r|\\r\\n|[\\r\\n])")) {
                } else {
                    shouldRemoveSpace = false;
                }
            }
            break;
        }
        if (shouldRemoveSpace) {
            for (int i = bl.size() - 1; i >= 0; --i) {
                TextBuilder tb = bl.get(i);
                if (tb == Token.EMPTY_TOKEN || tb instanceof IDirective) {
                    continue;
                }
                if (tb.getClass().equals(Token.StringToken.class)) {
                    String s = tb.toString();
                    if (s.matches("[ \\t\\x0B\\f]+")) {
                        bl.remove(i);
                        continue;
                    }
                }
                break;
            }
        }
    }

    String template() {
        return tmpl;
    }

    @Override
    public TextBuilder build() {
        long start = 0L;
        if (logger.isTraceEnabled()) {
            logger.trace("Begin to build %s", templateClass.getKey());
            start = System.currentTimeMillis();
        }
        try {
            RythmEngine engine = engine();
            parser.parse();
            String key = templateClass.getKey();
            if (!key.endsWith("/__global.rythm")) {
                boolean enableGlobalInclude = true;
                if (null != requiredDialect) {
                    if (requiredDialect instanceof BasicRythm || requiredDialect instanceof ToStringTemplateBase) {
                        enableGlobalInclude = false;
                    } 
                }
                if (enableGlobalInclude && conf.hasGlobalInclude()) {
                    String code = addInclude("__global.rythm", -1, null);
                    CodeToken ck = new CodeToken(code, parser);
                    addBuilder(ck);
                }
            }
            invokeDirectives();
            //if (!basicTemplate()) addDefaultRenderArgs();
            RythmEvents.ON_BUILD_JAVA_SOURCE.trigger(engine, this);
            pPackage();
            pImports();
            pClassOpen();
            pTagImpl();
            pInitCode();
            pSetup();
            if (!simpleTemplate()) pExtendInitArgCode();
            pRenderArgs();
            pStaticCodes();
            pInlineClasses();
            pInlineTags();
            pBuild();
            pFinalCode();
            RythmEvents.ON_CLOSING_JAVA_CLASS.trigger(engine, this);
            pClassClose();
            if (conf.debugJavaSourceEnabled()) {
                logger.info("java source code for %s: \n%s", templateClass, this);
            }
            return this;
        } catch (NotRythmTemplateException e) {
            isNotRythmTemplate = true;
            return this;
        } finally {
            parser.shutdown();
            if (logger.isTraceEnabled()) {
                logger.trace("%sms to build %s", System.currentTimeMillis() - start, templateClass.getKey());
            }
        }
    }

    private void invokeDirectives() {
        for (TextBuilder b : builders) {
            if (b instanceof IDirective) {
                ((IDirective) b).call();
            }
        }
    }

    protected void pPackage() {
        if (!S.isEmpty(pName)) p("package ").p(pName).pn(";");
    }

    private void pImport(String s, boolean sandbox) {
        if (S.isEmpty(s)) return;
        if (s.startsWith("import ")) {
            s = s.replaceFirst("import ", "");
        }
        if (sandbox) {
            String s0 = Sandbox.hasAccessToRestrictedClasses(engine, s);
            if (null != s0) return;
        }
        p("import ").p(s).p(';');
        Integer I = importLineMap.get(s);
        if (null != I) p(" //line: ").pn(I);
        else p("\n");
    }

    // print imports
    protected void pImports() {
        boolean sandbox = Rythm.insideSandbox();
        for (String s : imports) {
            pImport(s, sandbox);
        }
//        for (String s : globalImports) {
//            pImport(s, sandbox);
//        }
// moved to event handler
//        if (null != importProvider) {
//            for (String s : importProvider.imports()) {
//                pImport(s, sandbox);
//            }
//        }
// replaced by importProvider
//        IImplicitRenderArgProvider p = implicitRenderArgProvider;
//        if (null != p) {
//            for (String s : p.getImplicitImportStatements()) {
//                pImport(s, sandbox);
//            }
//        }
        // common imports
        pn("import java.util.*;");
        pn("import org.rythmengine.template.TemplateBase;");
        if (!sandbox) pn("import java.io.*;");
    }

    protected void pClassOpen() {
        np("public class ").p(cName).p(" extends ").p(extended()).p(" {").pn(extendedResourceMark());
    }

    protected void pClassClose() {
        np("}").pn();
    }

    private static String toNonGeneric(String type) {
        Regex regex = new Regex("(?@<>)", "");
        return regex.replaceAll(type);
    }
    
    private static boolean isGeneric(String type) {
        Regex regex = new Regex(".*(?@<>)");
        return regex.search(type);
    }
    
    private static boolean isArray(String type) {
        Regex regex = new Regex(".*(?@[])");
        return regex.search(type);
    }
    
    private void addInferencedRenderArgs() {
        if (renderArgs.isEmpty() && conf.typeInferenceEnabled()) {
            Map<String, String> tMap = ParamTypeInferencer.getTypeMap();
            List<String> ls = new ArrayList<String>(tMap.keySet());
            Collections.sort(ls);
            for (String name : ls) {
                String type = tMap.get(name);
                addRenderArgs(-1, type, name);
            }
        }
    }

    protected void pRenderArgs() {
        pn();
        addInferencedRenderArgs();
        // -- output private members
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            pt("protected ").p(arg.type).p(" ").p(argName);
            if (null != arg.defVal) {
                p("=").p(arg.defVal).p(";");
            } else {
                p(";");
            }
            if (arg.lineNo > -1) p(" //line: ").pn(arg.lineNo);
            else pn();
        }
 
        List<RenderArgDeclaration> renderArgList = new ArrayList<RenderArgDeclaration>(renderArgs.values());
        // comment to fix gh244: Collections.sort(renderArgList);
        
        // -- output __renderArgName method
        pn();
        boolean first = true; 
        ptn("protected java.lang.String __renderArgName(int __pos) {");
        p2tn("int __p = 0;");
        if (true) {
            first = true;
            for (RenderArgDeclaration arg : renderArgList) {
                if (first) {
                    first = false;
                    p2t("");
                } else {
                    p2t("else ");
                }
                p("if (__p++ == __pos) return \"").p(arg.name).p("\";").pn();
            }
            p2tn("throw new ArrayIndexOutOfBoundsException();");
        }
        ptn("}");

        // -- output __renderArgTypeMap method
        pn();
        ptn("protected java.util.Map<java.lang.String, java.lang.Class> __renderArgTypeMap() {");
        p2tn("java.util.Map<java.lang.String, java.lang.Class> __m = new java.util.HashMap<String, Class>();");
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            String argType = arg.type;
            boolean isGeneric = isGeneric(argType);
            if (isGeneric) {
                p2t("__m.put(\"").p(argName).p("\", ").p(toNonGeneric(argType)).pn(".class);");
                Regex regex = new Regex(".*((?@<>))");
                regex.search(argType);
                String s = regex.stringMatched(1);
                s = S.strip(s, "<", ">");
                if (s.contains("<")) {
                    // not support embedded <> yet
                } else {
                    String[] sa = s.split(",");
                    for (int i = 0; i < sa.length; ++i) {
                        String type = sa[i];
                        if ("?".equals(type)) {
                            type = "Object";
                        }
                        p2t("__m.put(\"").p(argName).p("__").p(i).p("\", ").p(type).pn(".class);");
                    }
                }
            } else {
                String type = argType;
                if ("?".equals(type)) {
                    type = "Object";
                }
                p2t("__m.put(\"").p(argName).p("\", ").p(type).pn(".class);");
//                int lvl = 0;
//                if (isArray(type)) {
//                    int pos = type.lastIndexOf("[");
//                    type = type.substring(0, pos);
//                    p2t("__m.put(\"").p(argName).p("__").p(lvl).p("\", ").p(type).pn(".class);");
//                    //lvl++;
//                }
            }
        }
        p2tn("return __m;");
        ptn("}");

        // -- output __setRenderArgs method
        pn();
        ptn("@SuppressWarnings(\"unchecked\")\n\tpublic TemplateBase __setRenderArgs(java.util.Map<java.lang.String, java.lang.Object> __args) {");
        p2tn("if (null == __args) throw new NullPointerException();\n\t\tif (__args.isEmpty()) return this;");
        p2tn("super.__setRenderArgs(__args);");
        first = true;
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (__args.containsKey(\"").p(argName).p("\")) this.").p(argName).p(" = __get(__args,\"").p(argName).p("\",").p(arg.objectType()).pn(".class);");
        }
        p2tn("return this;");
//        for (String argName : renderArgs.keySet()) {
//            p2t("System.err.println(\"").p(argName).p("=\" + this.").p(argName).pn(");");
//        }
        ptn("}");

        ISourceCodeEnhancer ce = engine.conf().get(RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER);
        int userDefinedArgNumber = basicTemplate() ? renderArgs.size() : (renderArgs.size() - ((null == ce) ? 0 : ce.getRenderArgDescriptions().size()));
        if (0 < userDefinedArgNumber) {
            // -- output __setRenderArgs method with args passed in positioned order
            pn();
            ptn("@SuppressWarnings(\"unchecked\") public TemplateBase __setRenderArgs(java.lang.Object... __args) {");
            {
                p2tn("int __p = 0, __l = __args.length;");
                int i = userDefinedArgNumber;
                for (RenderArgDeclaration arg : renderArgList) {
                    p2t("if (__p < __l) { \n\t\t\tObject v = __args[__p++]; \n\t\t\t").p(arg.name).p(" = __safeCast(v, ").p(arg.objectType()).p(".class); \n\t\t\t__renderArgs.put(\"").p(arg.name).p("\",").p(arg.name).p(");\n\t\t}\n"); 
                    if (--i == 0) break;
                }
            }
            p2tn("return this;");
            ptn("}");

            // -- output __renderArgTypeArray method with args passed in positioned order
            pn();
            ptn("protected java.lang.Class[] __renderArgTypeArray() {");
            {
                p2t("return new java.lang.Class[]{");
                int i = userDefinedArgNumber;
                for (RenderArgDeclaration arg : renderArgList) {
                    p(toNonGeneric(arg.type)).p(".class").p(", ");
                    if (--i == 0) break;
                }
                pn("};");
            }
            ptn("}");
        }

        // -- output __setRenderArg by name
        pn();
        ptn("@SuppressWarnings(\"unchecked\") @Override public TemplateBase __setRenderArg(java.lang.String __name, java.lang.Object __arg) {");
        if (true) {
            first = true;
            for (RenderArgDeclaration arg : renderArgList) {
                if (first) {
                    first = false;
                    p2t("");
                } else {
                    p2t("else ");
                }
                String argName = arg.name;
                p("if (\"").p(argName).p("\".equals(__name)) this.").p(argName).p(" = __safeCast(__arg, ").p(arg.objectType()).pn(".class);");
            }
        }
        p2t("super.__setRenderArg(__name, __arg);\n\t\treturn this;\n\t}\n");

        // -- output __setRenderArg by position
        pn();
        ptn("@SuppressWarnings(\"unchecked\") public TemplateBase __setRenderArg(int __pos, java.lang.Object __arg) {");
        p2tn("int __p = 0;");
        if (true) {
            first = true;
            for (RenderArgDeclaration arg : renderArgList) {
                if (first) {
                    first = false;
                    p2t("");
                } else {
                    p2t("else ");
                }
                p2t("if (__p++ == __pos) { \n\t\t\tObject v = __arg; \n\t\t\t").p(arg.name).p(" = __safeCast(v, ").p(arg.objectType()).p(".class); \n\t\t\t__renderArgs.put(\"").p(arg.name).p("\", ").p(arg.name).p(");\n\t\t}\n"); 
            }
        }
        // the first argument has a default name "arg"
        p2tn("if(0 == __pos) __setRenderArg(\"arg\", __arg);");
        p2tn("return this;");
        ptn("}");
    }

    protected void pExtendInitArgCode() {
        if (null == extendArgs || extendArgs.pl.size() < 1) return;
        pn();
        ptn("@Override protected void __loadExtendingArgs() {");
        for (int i = 0; i < extendArgs.pl.size(); ++i) {
            InvokeTemplateParser.ParameterDeclaration pd = extendArgs.pl.get(i);
            if (S.isEmpty(pd.nameDef)) {
                p2t("__parent.__setRenderArg(").p(i).p(", ").p(pd.valDef).p(");");
            } else {
                p2t("__parent.__setRenderArg(\"").p(pd.nameDef).p("\", ").p(pd.valDef).p(");");
            }
            if (extendDeclareLineNo != -1) {
                p(" //line: ").pn(extendDeclareLineNo);
            } else {
                pn();
            }
        }
        ptn("}");
    }

    protected void pSetup() {
        if (!logTime && renderArgs.isEmpty()) return;
        pn();
        ptn("@Override protected void __setup() {");
        if (logTime) {
            p2tn("__logTime = true;");
        }
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (__isDefVal(").p(argName).p(")) {");
            p(argName).p(" = __get(\"").p(argName).p("\",").p(arg.objectType()).p(".class) ;}\n");
        }
        ptn("}");
    }

    protected void pInitCode() {
        if (S.isEmpty(initCode)) return;
        pn();
        pt("@Override public void __init() {").p(initCode).p(";").pn("\n\t}");
    }

    protected void pFinalCode() {
        if (S.isEmpty(finalCode)) return;
        pn();
        pt("@Override public void __finally() {").p(finalCode).p(";").pn("\n\t}");
    }

    protected void pTagImpl() {
        pn();
        pt("@Override public java.lang.String __getName() {\n\t\treturn \"").p(tagName).p("\";\n\t}\n");
    }

    public String buildBody = null;

    transient Map<Token.StringToken, String> consts = new HashMap<Token.StringToken, String>();

    private RythmEngine.OutputMode outputMode = RythmEngine.outputMode();

    private Token.StringToken addConst(Token.StringToken st) {
        if (!outputMode.writeOutput()) return st;
        if (consts.containsKey(st)) {
            st.constId = consts.get(st);
            return st;
        } else {
            String id = this.newVarName();
            st.constId = id;
            consts.put(st, id);
            return st;
        }
    }

    private List<Token> mergeStringTokens(List<Token> builders) {
        List<Token> merged = new ArrayList<Token>();
        Token.StringToken curTk = new Token.StringToken("", parser);
        for (int i = 0; i < builders.size(); ++i) {
            Token tb = builders.get(i);
            if (tb == Token.EMPTY_TOKEN) {
                continue;
            }
            if (tb instanceof Token.StringToken) {
                Token.StringToken tk = (Token.StringToken) tb;
                curTk = curTk.mergeWith(tk);
            } else if (tb instanceof IDirective) {
                // do nothing
            } else if (tb instanceof BlockToken.LiteralBlock) {
                BlockToken.LiteralBlock bk = (BlockToken.LiteralBlock) tb;
                curTk = curTk.mergeWith(bk);
            } else if (tb instanceof CompactStateToken) {
                if (null != curTk && curTk.s().length() > 0) {
                    curTk = addConst(curTk);
                    curTk.compact();
                    merged.add(curTk);
                }
                curTk = new Token.StringToken("", parser);
                merged.add(tb);
                tb.build();
            } else {
                if (null != curTk && curTk.s().length() > 0) {
                    curTk = addConst(curTk);
                    curTk.compact();
                    merged.add(curTk);
                }
                curTk = new Token.StringToken("", parser);
                merged.add(tb);
            }
        }
        if (null != curTk && curTk.s().length() > 0) {
            curTk = addConst(curTk);
            curTk.compact();
            merged.add(curTk);
        }
        return merged;
    }

    protected void pInlineTags() {
        pn();
        for (InlineTag tag : inlineTags) {
            p("\npublic ").p(tag.retType).p(" ").p(tag.tagName).p(tag.signature);
            p("{\norg.rythmengine.template.TemplateBase oldParent = this.__parent;\ntry{\nthis.__parent = this;\n");
            boolean isVoid = tag.autoRet;
            if (!isVoid) {
                p(tag.body);
            } else {
                List<Token> merged = mergeStringTokens(tag.builders);
                for (Token b : merged) {
                    b.build(parser);
                }
            }
            p("\n}catch(RuntimeException __e){\n throw __e;\n}catch(Exception __e){\nthrow new java.lang.RuntimeException(__e);\n} finally {this.__parent = oldParent;}\n}");
        }
    }

    protected void pStaticCodes() {
        pn();
        for (String codeSnippet : staticCodes) {
            p("\n").p(codeSnippet).p(";\n");
        }
    }

    protected void pInlineClasses() {
        pn();
        for (InlineClass clz : inlineClasses) {
            p("\nprivate class ").p(clz.className).p(" {\n").p(clz.body).p("\n}\n");
        }
    }

    protected void pBuild() {
        pn();
        pn();
        ptn("@Override public org.rythmengine.utils.TextBuilder build(){");
        if (tmpl!=null)
          p2t("buffer().ensureCapacity(").p(tmpl.length()).p(");").pn();
        StringBuilder sb = new StringBuilder();
        StringBuilder old = buffer();
        __setBuffer(sb);
        // try merge strings
        List<Token> merged = mergeStringTokens(this.builders);
        for (Token b : merged) {
            b.build();
        }
        buildBody = sb.toString();
        __setBuffer(old);
        p(buildBody);
        p("\n\t\treturn this;\n\t}\n");

        // print out consts
        for (Token.StringToken st : consts.keySet()) {
            pConst(st);
        }
    }

    private void pConst(Token.StringToken st) {
        String constId = st.constId;
        String s = st.s(), s0 = s;
        if (st.compactMode()) {
            s0 = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        } else {
            s0 = s.replaceAll("(\\r?\\n)", "\\\\n").replaceAll("\"", "\\\\\"");
        }
        np("private static final org.rythmengine.utils.TextBuilder.StrBuf ").p(constId).p(" = new org.rythmengine.utils.TextBuilder.StrBuf(\"").p(s0);
        StrBuf sw = new StrBuf(s);
        if (outputMode == RythmEngine.OutputMode.os) {
            p("\", new byte[]{");
            byte[] ba = sw.toBinary();
            for (int i = 0; i < ba.length; ++i) {
                p(String.valueOf(ba[i]));
                if (i < ba.length - 1) p(",");
            }
            p("});");
        } else if (outputMode == RythmEngine.OutputMode.writer) {
            p("\", null);");
        } else {
            throw new AssertionError("should not go here");
        }
        p("// line:").pn(st.getLineNo());
    }

    private Set<String> varNames = new HashSet<String>();

    public String newVarName() {
        int i = 0;
        while (true) {
            String name = "__v" + i;
            if (!varNames.contains(name)) {
                varNames.add(name);
                return name;
            } else {
                i += new Random().nextInt(100000);
            }
        }
    }

    public static final String INTERRUPT_CODE = "\n{if (java.lang.Thread.interrupted()) throw new RuntimeException(\"interrupted\");}";

    private static final Regex R_FOR_0 = new Regex("([\\s;]for\\s*(?@())\\s*\\{(\\s*//\\s*line:\\s*[0-9]+)?)", "${1}" + INTERRUPT_CODE + "${2}" + "\n");
    private static final Regex R_FOR_1 = new Regex("([\\s;]for\\s*(?@()))\\s*([^\\{]+;)", "${1} \\{" + INTERRUPT_CODE + "${2} \n\\}");

    private static final Regex R_WHILE_0 = new Regex("([\\s;]while\\s*(?@())\\s*\\{)", "${1}" + INTERRUPT_CODE);
    private static final Regex R_WHILE_1 = new Regex("([\\s;]while\\s*(?@()))\\s*([^\\{]+;)", "${1} \\{" + INTERRUPT_CODE + "${2} \\}");

    private static final Regex R_DO_0 = new Regex("([\\s;]do\\s*\\{)", "${1}" + INTERRUPT_CODE);
    private static final Regex R_DO_1 = new Regex("([\\s;]do\\s*)([^\\{\\}]+[\\s;]while[\\s\\(])", "${1} \\{" + INTERRUPT_CODE + "${2}");

    public static String preventInfiniteLoop(String code) {
        code = R_FOR_0.replaceAll(code);
        code = R_FOR_1.replaceAll(code);
        code = R_WHILE_0.replaceAll(code);
        code = R_WHILE_1.replaceAll(code);
        code = R_DO_0.replaceAll(code);
        code = R_DO_1.replaceAll(code);
        return code;
    }

}
