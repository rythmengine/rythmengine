package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.Sandbox;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.extension.ISourceCodeEnhancer;
import com.greenlaw110.rythm.internal.compiler.ParamTypeInferencer;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
import com.greenlaw110.rythm.internal.dialect.SimpleRythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.NotRythmTemplateException;
import com.greenlaw110.rythm.internal.parser.build_in.BlockToken;
import com.greenlaw110.rythm.internal.parser.build_in.InvokeTagParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CodeBuilder extends TextBuilder {

    protected ILogger logger = Logger.get(CodeBuilder.class);

    private int renderArgCounter = 0;

    public static class RenderArgDeclaration implements Comparable<RenderArgDeclaration> {
        public int no;
        public String name;
        public String type;
        public String defVal;
        public int lineNo;

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
            if ("boolean".equalsIgnoreCase(type)) defVal = defVal.toLowerCase();
            if ("long".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "L";
            if ("float".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "f";
            if ("double".equalsIgnoreCase(type) && defVal.matches("[0-9]+")) return defVal + "d";
            return defVal;
        }

        private static String defVal(String type) {
            if (type.equalsIgnoreCase("boolean"))
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

    private boolean isNotRythmTemplate = false;
    public ILang templateDefLang;

    public boolean isRythmTemplate() {
        return !isNotRythmTemplate;
    }

    protected String tmpl;
    private String cName;
    public String includingCName;
    private String pName;
    private String tagName;

    private boolean isTag() {
        return null != tagName;
    }

    private String initCode = null;

    public void setInitCode(String code) {
        if (null != initCode)
            throw new ParseException(engine, templateClass, parser.currentLine(), "@init section already declared.");
        initCode = code;
    }

    private String extended; // the cName of the extended template

    protected String extended() {
        String defClass = isTag() ? TagBase.class.getName() : TemplateBase.class.getName();
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

    private InvokeTagParser.ParameterDeclarationList extendArgs = null;
    public Set<String> imports = new HashSet<String>();
    private int extendDeclareLineNo = -1;
    // <argName, argClass>
    public Map<String, RenderArgDeclaration> renderArgs = new LinkedHashMap<String, RenderArgDeclaration>();
    private List<TextBuilder> builders = new ArrayList<TextBuilder>();
    private TemplateParser parser;
    private TemplateClass templateClass;

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
        this.tagName = (null == tagName) ? className : tagName;
        className = className.replace('/', '.');
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
        ILang lang = templateClass.templateLang;
        if (null == lang) {
            lang = ILang.DefImpl.probeFileName(templateClass.name(), this.engine.conf().defaultLang());
        }
        this.templateDefLang = lang;
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
        this.extended = null;
        this.extendedTemplateClass = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.imports.clear();
        this.extendDeclareLineNo = 0;
        this.renderArgs.clear();
        this.builders.clear();
        this.parser = null;
        this.templateClass = null;
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
        this.importLineMap.clear();
        this.logTime = false;
        this.macros.clear();
        this.macroStack.clear();
        this.buildBody = null;
        this.templateDefLang = null;
    }

    /**
     * Rewind to the state when construction finished
     */
    public void rewind() {
        renderArgCounter = 0;
        this.initCode = null;
        this.extended = null;
        this.extendedTemplateClass = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.imports.clear();
        this.extendDeclareLineNo = 0;
        this.renderArgs.clear();
        this.builders.clear();
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
        this.importLineMap.clear();
        this.logTime = false;
        this.macros.clear();
        this.macroStack.clear();
        this.buildBody = null;
    }

    public void merge(CodeBuilder codeBuilder) {
        if (null == codeBuilder) return;
        this.imports.addAll(codeBuilder.imports);
        for (InlineTag tag : codeBuilder.inlineTags) {
            inlineTags.add(tag.clone(this));
        }
        this.initCode = new StringBuilder(S.toString(this.initCode)).append(S.toString(codeBuilder.initCode)).toString();
        this.renderArgs.putAll(codeBuilder.renderArgs);
        this.importLineMap.putAll(codeBuilder.importLineMap);
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

    public static class InlineTag {
        String tagName;
        String signature;
        String retType = "void";
        String body;
        boolean autoRet = false;
        List<TextBuilder> builders = new ArrayList<TextBuilder>();

        InlineTag(String name, String ret, String sig, String body) {
            tagName = name;
            signature = sig;
            retType = null == ret ? "void" : ret;
            this.body = body;
        }

        InlineTag clone(CodeBuilder newCaller) {
            InlineTag tag = new InlineTag(tagName, retType, signature, body);
            tag.builders.clear();
            for (TextBuilder tb : builders) {
                TextBuilder newTb = tb.clone(newCaller);
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

    public boolean needsPrint(String tagName) {
        return templateClass.returnObject(tagName);
    }

    private Stack<List<TextBuilder>> inlineTagBodies = new Stack<List<TextBuilder>>();

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
            tag.retType = "com.greenlaw110.rythm.template.ITemplate.RawData";
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

    public String addIncludes(String includes, int lineNo) {
        StringBuilder sb = new StringBuilder();
        for (String s : includes.split("[\\s,;:]+")) {
            sb.append(addInclude(s, lineNo));
        }
        return sb.toString();
    }

    public String addInclude(String include, int lineNo) {
        String tagName = engine.testTag(include, templateClass);
        if (null == tagName) {
            throw new ParseException(engine, templateClass, lineNo, "include template not found: %s", include);
        }
        TemplateBase includeTag = (TemplateBase) engine.getTag(tagName);
        if (includeTag instanceof JavaTagBase) {
            throw new ParseException(engine, templateClass, lineNo, "cannot include Java tag: %s", include);
        }
        TemplateClass includeTc = includeTag.getTemplateClass(false);
        includeTc.buildSourceCode(includingClassName());
        merge(includeTc.codeBuilder);
        templateClass.addIncludeTemplateClass(includeTc);
        return includeTc.codeBuilder.buildBody;
    }

    public void setExtended(Class<? extends TemplateBase> c) {
        this.extended = c.getName();
    }

    public void setExtended(String extended, InvokeTagParser.ParameterDeclarationList args, int lineNo) {
        if (simpleTemplate()) {
            throw new ParseException(engine, templateClass, lineNo, "Simple template does not allow to extend layout template");
        }
        if (null != this.extended) {
            throw new ParseException(engine, templateClass, lineNo, "Extended template already declared");
        }
        String fullName = engine.testTag(extended, templateClass);
        if (null == fullName) {
            // try legacy style
            setExtended_deprecated(extended, args, lineNo);
            logger.warn("Template[%s]: Extended template declaration \"%s\" is deprecated, please switch to the new style \"%s\"", templateClass.getKey(), extended, engine.resourceManager().getFullTagName(extendedTemplateClass));
        } else {
            TemplateBase tb = (TemplateBase) engine.getTag(fullName);
            TemplateClass tc = tb.getTemplateClass(false);
            this.extended = tc.name();
            this.extendedTemplateClass = tc;
            this.templateClass.extendedTemplateClass = tc;
            this.engine.addExtendRelationship(tc, this.templateClass);
            this.extendArgs = args;
            this.extendDeclareLineNo = lineNo;
        }
    }

    public void setExtended_deprecated(String extended, InvokeTagParser.ParameterDeclarationList args, int lineNo) {
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
                ITemplateResource resource = engine.resourceManager().getFileResource(extended);
                if (resource.isValid()) tc = new TemplateClass(resource, engine);
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
                ITemplateResource resource = engine.resourceManager().getFileResource(origin);
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

    private Map<String, List<TextBuilder>> macros = new HashMap<String, List<TextBuilder>>();
    private Stack<String> macroStack = new Stack<String>();

    public void pushMacro(String macro) {
        if (macros.containsKey(macro)) {
            throw new ParseException(engine, templateClass, parser.currentLine(), "Macro already defined: %s", macro);
        }
        macroStack.push(macro);
        macros.put(macro, new ArrayList<TextBuilder>());
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

    public List<TextBuilder> getMacro(String macro) {
        List<TextBuilder> list = this.macros.get(macro);
        if (null == list) throw new NullPointerException();
        return list;
    }

    public void addBuilder(TextBuilder builder) {
        if (macroStack.empty()) builders.add(builder);
        else {
            String macro = macroStack.peek();
            List<TextBuilder> list = macros.get(macro);
            if (null == list) {
                list = new ArrayList<TextBuilder>();
                macros.put(macro, list);
            }
            list.add(builder);
        }
    }
    
    public void removeImmediateLastLineBreak() {
        for (int i = builders.size() - 1; i >= 0; --i) {
            TextBuilder tb = builders.get(i);
            if (tb == Token.EMPTY_TOKEN || tb instanceof IDirective) {
                continue;
            }
            if (tb.getClass().equals(Token.StringToken.class)) {
                if (tb.toString().matches("(\\n\\r|\\r\\n|[\\r\\n])")) {
                    builders.remove(i);
                }
            }
            break;
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
            pInlineTags();
//            for (IByteCodeEnhancer enhancer : engine.templateClassEnhancers) {
//                np(enhancer.sourceCode());
//            }
            pBuild();
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

        // -- output renderArgTypeMap method
        pn();
        ptn("protected Map<String, Class> renderArgTypeMap() {");
        p2tn("Map<String, Class> m = new HashMap<String, Class>();");
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("m.put(\"").p(argName).p("\", ").p(toNonGeneric(arg.type)).pn(".class);");
        }
        p2tn("return m;");
        ptn("}");

        // -- output setRenderArgs method
        pn();
        ptn("@SuppressWarnings(\"unchecked\")\n\tpublic void setRenderArgs(java.util.Map<String, Object> args) {");
        p2tn("if (null == args) throw new NullPointerException();\n\t\tif (args.isEmpty()) return;");
        p2tn("super.setRenderArgs(args);");
        boolean first = true;
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (args.containsKey(\"").p(argName).p("\")) this.").p(argName).p("=(").p(arg.type).p(")args.get(\"").p(argName).pn("\");");
        }
//        for (String argName : renderArgs.keySet()) {
//            p2t("System.err.println(\"").p(argName).p("=\" + this.").p(argName).pn(");");
//        }
        ptn("}");

        List<RenderArgDeclaration> renderArgList = new ArrayList<RenderArgDeclaration>(renderArgs.values());
        Collections.sort(renderArgList);

        ISourceCodeEnhancer ce = engine.conf().get(RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER);
        int userDefinedArgNumber = basicTemplate() ? renderArgs.size() : (renderArgs.size() - ((null == ce) ? 0 : ce.getRenderArgDescriptions().size()));
        if (0 < userDefinedArgNumber) {
            // -- output setRenderArgs method with args passed in positioned order
            pn();
            ptn("@SuppressWarnings(\"unchecked\") public void setRenderArgs(Object... args) {");
            {
                p2tn("int _p = 0, l = args.length;");
                int i = userDefinedArgNumber;
                for (RenderArgDeclaration arg : renderArgList) {
                    p2t("if (_p < l) { Object v = args[_p++]; boolean isString = (\"java.lang.String\".equals(\"")
                            .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                            .p(arg.name).p(" = (").p(arg.type).pn(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
                    if (--i == 0) break;
                }
            }
            ptn("}");

            // -- output renderArgTypeArray method with args passed in positioned order
            pn();
            ptn("protected Class[] renderArgTypeArray() {");
            {
                p2t("return new Class[]{");
                int i = userDefinedArgNumber;
                for (RenderArgDeclaration arg : renderArgList) {
                    p(toNonGeneric(arg.type)).p(".class").p(", ");
                    if (--i == 0) break;
                }
                pn("};");
            }
            ptn("}");
        }

        // -- output setRenderArg by name
        pn();
        ptn("@SuppressWarnings(\"unchecked\") @Override public void setRenderArg(String name, Object arg) {");
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
                p("if (\"").p(argName).p("\".equals(name)) this.").p(argName).p("=(").p(arg.type).pn(")arg;");
            }
        }
        p2t("super.setRenderArg(name, arg);\n\t}\n");

        // -- output setRenderArg by position
        pn();
        ptn("@SuppressWarnings(\"unchecked\") public void setRenderArg(int pos, Object arg) {");
        p2tn("int _p = 0;");
        if (true) {
            first = true;
            for (RenderArgDeclaration arg : renderArgList) {
                if (first) {
                    first = false;
                    p2t("");
                } else {
                    p2t("else ");
                }
                p("if (_p++ == pos) { Object v = arg; boolean isString = (\"java.lang.String\".equals(\"")
                        .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                        .p(arg.name).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }").pn();
            }
        }
        // the first argument has a default name "arg"
        p2tn("if(0 == pos) setRenderArg(\"arg\", arg);");
        ptn("}");
    }

    protected void pExtendInitArgCode() {
        if (null == extendArgs || extendArgs.pl.size() < 1) return;
        pn();
        ptn("@Override protected void loadExtendingArgs() {");
        for (int i = 0; i < extendArgs.pl.size(); ++i) {
            InvokeTagParser.ParameterDeclaration pd = extendArgs.pl.get(i);
            if (S.isEmpty(pd.nameDef)) {
                p2t("__parent.setRenderArg(").p(i).p(", ").p(pd.valDef).p(");");
            } else {
                p2t("__parent.setRenderArg(\"").p(pd.nameDef).p("\", ").p(pd.valDef).p(");");
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
        ptn("@Override protected void setup() {");
        if (logTime) {
            p2tn("__logTime = true;");
        }
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (").p(argName).p(" == null) {");
            //p("\n\tif (").p(argName).p(" == ").p(RenderArgDeclaration.defVal(arg.type)).p(") {");
            p(argName).p("=(").p(arg.type).p(")_get(\"").p(argName).p("\");}\n");
        }
        ptn("}");
    }

    protected void pInitCode() {
        if (S.isEmpty(initCode)) return;
        pn();
        pt("@Override public void init() {").p(initCode).p(";").pn("\n\t}");
    }

    protected void pTagImpl() {
        if (!isTag()) return;
        pn();
        pt("@Override public java.lang.String getName() {\n\t\treturn \"").p(tagName).p("\";\n\t}\n");
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

    private List<TextBuilder> mergeStringTokens(List<TextBuilder> builders) {
        List<TextBuilder> merged = new ArrayList<TextBuilder>();
        Token.StringToken curTk = new Token.StringToken("", parser);
        for (int i = 0; i < builders.size(); ++i) {
            TextBuilder tb = builders.get(i);
            if (tb == Token.EMPTY_TOKEN) {
                continue;
            }
            if (tb instanceof Token.StringToken || tb instanceof BlockToken.LiteralBlock || tb instanceof IDirective) {
                if (tb instanceof Token.StringToken) {
                    Token.StringToken tk = (Token.StringToken) tb;
                    curTk = curTk.mergeWith(tk);
                } else if (tb instanceof IDirective) {
                    // do nothing
                } else {
                    BlockToken.LiteralBlock bk = (BlockToken.LiteralBlock) tb;
                    curTk = curTk.mergeWith(bk);
                }
            } else {
                if (null != curTk && curTk.s().length() > 0) {
                    curTk = addConst(curTk);
                    merged.add(curTk);
                }
                curTk = new Token.StringToken("", parser);
                merged.add(tb);
            }
        }
        if (null != curTk && curTk.s().length() > 0) {
            curTk = addConst(curTk);
            merged.add(curTk);
        }
        return merged;
    }

    protected void pInlineTags() {
        pn();
        for (InlineTag tag : inlineTags) {
            p("\nprotected ").p(tag.retType).p(" ").p(tag.tagName).p(tag.signature);
            p("{\ncom.greenlaw110.rythm.template.TemplateBase oldParent = this.__parent;\ntry{\nthis.__parent = this;\n");
            boolean isVoid = tag.autoRet;
            StringBuilder sb = buffer();
            if (!isVoid) {
                p(tag.body);
            } else {
                List<TextBuilder> merged = mergeStringTokens(tag.builders);
                for (TextBuilder b : merged) {
                    b.build();
                }
            }
            p("\n}catch(Exception __e){\nthrow new RuntimeException(__e);\n} finally {this.__parent = oldParent;}\n}");
        }
    }

    protected void pBuild() {
        pn();
        pn();
        ptn("@Override public com.greenlaw110.rythm.utils.TextBuilder build(){");
        p2t("buffer().ensureCapacity(").p(tmpl.length()).p(");").pn();
        StringBuilder sb = new StringBuilder();
        StringBuilder old = buffer();
        setBuffer(sb);
        // try merge strings
        List<TextBuilder> merged = mergeStringTokens(this.builders);
        for (TextBuilder b : merged) {
            b.build();
        }
        buildBody = sb.toString();
        setBuffer(old);
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
        np("private static final StrBuf ").p(constId).p(" = new StrBuf(\"").p(s0);
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

    public static final String INTERRUPT_CODE = "\n{if (Thread.interrupted()) throw new RuntimeException(\"interrupted\");}";

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

    public static void main(String[] args) {
        String s = "<!-- @dsfs --> dfds -->dfs <!-- xx -->";
        Pattern p = Pattern.compile("\\<\\!\\-\\-\\s*(@.*?)\\-\\-\\>");
        Matcher m = p.matcher(s);
        s = m.replaceAll("$1");
        System.out.println(s);
    }

}
