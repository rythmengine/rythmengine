package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.parser.NotRythmTemplateException;
import com.greenlaw110.rythm.internal.parser.build_in.InvokeTagParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.IImplicitRenderArgProvider;
import com.greenlaw110.rythm.utils.IImportProvider;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.*;


public class CodeBuilder extends TextBuilder {

    protected ILogger logger = Logger.get(CodeBuilder.class);

    public static class RenderArgDeclaration {
        public String name;
        public String type;
        public String defVal;

        public RenderArgDeclaration(String name, String type) {
            this(name, type, null);
        }

        public RenderArgDeclaration(String name, String type, String defVal) {
            this.name = name;
            this.type = typeTransform(type);
            this.defVal = null == defVal ? defVal(type) : defVal;
        }

        private static String typeTransform(String type) {
            if ("boolean".equals(type)) return "Boolean";
            else if ("int".equals(type)) return "Integer";
            else if ("float".equals(type)) return "Float";
            else if ("double".equals(type)) return "Double";
            else if ("char".equals(type)) return "Character";
            else if ("long".equals(type)) return "Long";
            else return type;
        }

        private static String defVal(String type) {
            if (type.equals("boolean"))
                return "false";
            else if (type.equals("int"))
                return "0";
            else if (type.equals("long"))
                return "0L";
            else if (type.equals("char"))
                return "(char)0";
            else if (type.equals("byte"))
                return "(byte)0";
            else if (type.equals("short"))
                return "(short)0";
            else if (type.equals("float"))
                return "0f";
            else if (type.equals("double"))
                return "0d";

            return "null";
        }
    }

    public RythmEngine engine;
    private boolean isNotRythmTemplate = false;

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
            throw new ParseException(templateClass, parser.currentLine(), "@init section already declared.");
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

    /*
    * Simple template
    * 1. does not have global render args,
    * 2. does not extends layout template
    */
    private boolean simpleTemplate = false;

    public void setSimpleTemplate(int lineNo) {
        if (null != this.extended) {
            throw new ParseException(templateClass, lineNo, "Simple template does not allow to extend layout template");
        }
        //if (true) throw new RuntimeException(this.template());
        simpleTemplate = true;
    }

    transient public IDialect dialect = null;

    public CodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine, IDialect dialect) {
        tmpl = template;
        this.tagName = (null == tagName) ? className : tagName;
        className = className.replace('/', '.');
        cName = className;
        int i = className.lastIndexOf('.');
        if (-1 < i) {
            cName = className.substring(i + 1);
            pName = className.substring(0, i);
        }
        this.engine = null == engine ? Rythm.engine : engine;
        this.dialect = dialect;
        this.parser = new TemplateParser(this);
        this.templateClass = templateClass;
        this.simpleTemplate = templateClass.simpleTemplate;
    }

    /**
     * Reset to the state before construction
     */
    public void clear() {
        out().ensureCapacity(0);
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
        this.simpleTemplate = false;
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
        this.logTime = false;
        this.macros.clear();
        this.macroStack.clear();
        this.buildBody = null;
    }

    /**
     * Rewind to the state when construction finished
     */
    public void rewind() {
        out().ensureCapacity(0);
        this.initCode = null;
        this.extended = null;
        this.extendedTemplateClass = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.imports.clear();
        this.extendDeclareLineNo = 0;
        this.renderArgs.clear();
        this.builders.clear();
        this.simpleTemplate = false;
        this.inlineTags.clear();
        this.inlineTagBodies.clear();
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
    }

    public String className() {
        return cName;
    }

    public String includingClassName() {
        return null == includingCName ? cName : includingCName;
    }

    private static Set<String> globalImports = new HashSet<String>();

    public static void registerImports(String imports) {
        globalImports.addAll(Arrays.asList(imports.split(",")));
    }

    private static IImportProvider importProvider = null;

    public static void registerImportProvider(IImportProvider provider) {
        importProvider = provider;
    }

    public void addImport(String imprt) {
        if (!globalImports.contains(imprt)) imports.add(imprt);
        if (imprt.endsWith(".*")) {
            imprt = imprt.substring(0, imprt.lastIndexOf(".*"));
            templateClass.importPaths.add(imprt);
        }
    }

    private static class InlineTag {
        String tagName;
        String signature;
        String retType = "void";
        String body;
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

    public void defTag(String tagName, String retType, String signature, String body) {
        tagName = tagName.trim();
        InlineTag tag = new InlineTag(tagName, retType, signature, body);
        if (inlineTags.contains(tag)) {
            throw new ParseException(templateClass, parser.currentLine(), "inline tag already defined: %s", tagName);
        }
        inlineTags.add(tag);
        inlineTagBodies.push(builders);
        builders = tag.builders;
        templateClass.setTagType(tagName, tag.retType);
    }

    public void endTag() {
        if (inlineTagBodies.empty())
            throw new ParseException(templateClass, parser.currentLine(), "Unexpected tag definition close");
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
            throw new ParseException(templateClass, lineNo, "include template not found: %s", include);
        }
        TemplateBase includeTag = (TemplateBase) engine.tags.get(tagName);
        if (includeTag instanceof JavaTagBase) {
            throw new ParseException(templateClass, lineNo, "cannot include Java tag: %s", include);
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
        if (simpleTemplate) {
            throw new ParseException(templateClass, lineNo, "Simple template does not allow to extend layout template");
        }
        if (null != this.extended) {
            throw new ParseException(templateClass, lineNo, "Extended template already declared");
        }
        String fullName = engine.testTag(extended, templateClass);
        if (null == fullName) {
            // try legacy style
            setExtended_deprecated(extended, args, lineNo);
            logger.warn("Template[%s]: Extended template declaration \"%s\" is deprecated, please switch to the new style \"%s\"", templateClass.getKey(), extended, engine.resourceManager.getFullTagName(extendedTemplateClass));
        } else {
            TemplateBase tb = (TemplateBase) engine.tags.get(fullName);
            TemplateClass tc = tb.getTemplateClass(false);
            this.extended = tc.name();
            this.extendedTemplateClass = tc;
            this.templateClass.extendedTemplateClass = tc;
            this.engine.addExtendRelationship(tc, this.templateClass);
            this.extendArgs = args;
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
            tc = engine.classes.getByTemplate(extended);
            if (null == tc) {
                ITemplateResource resource = engine.resourceManager.getFileResource(extended);
                if (resource.isValid()) tc = new TemplateClass(resource, engine);
            }
        }
        if (null == tc && !extended.startsWith("/")) {
            // it's in class name style ?
            //if (!extended.endsWith(TemplateClass.CN_SUFFIX)) extended = extended + TemplateClass.CN_SUFFIX;
            tc = engine.classes.getByClassName(extended);
        }
        if (null == tc) {
            tc = engine.classes.getByTemplate(origin);
            if (null == tc) {
                ITemplateResource resource = engine.resourceManager.getFileResource(origin);
                if (resource.isValid()) tc = new TemplateClass(resource, engine);
            }
        }
        if (null == tc) {
            throw new ParseException(templateClass, lineNo, "Cannot find extended template by name \"%s\"", origin);
        }
        this.extended = tc.name();
        this.extendedTemplateClass = tc;
        this.templateClass.extendedTemplateClass = tc;
        this.engine.addExtendRelationship(tc, this.templateClass);
        this.extendArgs = args;
    }

    protected boolean logTime = false;

    public void setLogTime() {
        logTime = true;
    }

    public void addRenderArgs(RenderArgDeclaration declaration) {
        renderArgs.put(declaration.name, declaration);
    }

    public void addRenderArgs(String type, String name) {
        renderArgs.put(name, new RenderArgDeclaration(name, type));
    }

    private Map<String, List<TextBuilder>> macros = new HashMap<String, List<TextBuilder>>();
    private Stack<String> macroStack = new Stack<String>();

    public void pushMacro(String macro) {
        if (macros.containsKey(macro)) {
            throw new ParseException(templateClass, parser.currentLine(), "Macro already defined: %s", macro);
        }
        macroStack.push(macro);
        macros.put(macro, new ArrayList<TextBuilder>());
    }

    public void popMacro() {
        if (macroStack.empty()) {
            throw new ParseException(templateClass, parser.currentLine(), "no macro found in stack");
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

    String template() {
        return tmpl;
    }

    @Override
    public TextBuilder build() {
        try {
            parser.parse();
            invokeDirectives();
            if (!simpleTemplate) addDefaultRenderArgs();
            pPackage();
            pImports();
            pClassOpen();
            pTagImpl();
            pInitCode();
            pSetup();
            if (!simpleTemplate) pExtendInitArgCode();
            pRenderArgs();
            pInlineTags();
            for (ITemplateClassEnhancer enhancer : engine.templateClassEnhancers) {
                np(enhancer.sourceCode());
            }
            pBuild();
            pClassClose();
            return this;
        } catch (NotRythmTemplateException e) {
            isNotRythmTemplate = true;
            return this;
        }
    }

    private void invokeDirectives() {
        for (TextBuilder b : builders) {
            if (b instanceof IDirective) {
                ((IDirective) b).call();
            }
        }
    }

    private void addDefaultRenderArgs() {
        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        if (null == p) return;
        Map<String, ?> defArgs = p.getRenderArgDescriptions();
        for (String name : defArgs.keySet()) {
            Object o = defArgs.get(name);
            String type = (o instanceof Class<?>) ? ((Class<?>) o).getName() : o.toString();
            addRenderArgs(type, name);
        }
    }

    protected void pPackage() {
        if (!S.isEmpty(pName)) p("package ").p(pName).pn(";");
    }

    // print imports
    protected void pImports() {
        for (String s : imports) {
            if (!S.isEmpty(s)) p("import ").p(s).pn(';');
        }
        for (String s : globalImports) {
            if (!S.isEmpty(s)) p("import ").p(s).pn(';');
        }
        if (null != importProvider) {
            for (String s : importProvider.imports()) {
                if (!S.isEmpty(s)) p("import ").p(s).pn(';');
            }
        }

        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        if (null != p) {
            for (String s : p.getImplicitImportStatements()) {
                p("import ").p(s).pn(';');
            }
        }
        // common imports
        pn("import java.util.*;");
        pn("import java.io.*;");
    }

    protected void pClassOpen() {
        np("public class ").p(cName).p(" extends ").p(extended()).p(" {").pn(extendedResourceMark());
    }

    protected void pClassClose() {
        np("}").pn();
    }

    protected void pRenderArgs() {
        pn();
        // -- output private members
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            pt("protected ").p(arg.type).p(" ").p(argName);
            if (null != arg.defVal) {
                p("=").p(arg.defVal).p(";");
            } else {
                p(";");
            }
            pn();
        }

        // -- output setRenderArgs method
        pn();
        ptn("@SuppressWarnings(\"unchecked\") public void setRenderArgs(java.util.Map<String, Object> args) {");
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (null != args && args.containsKey(\"").p(argName).p("\")) this.").p(argName).p("=(").p(arg.type).p(")args.get(\"").p(argName).pn("\");");
        }
        p2t("super.setRenderArgs(args);\n\t}\n");

        // -- output setRenderArgs method with args passed in positioned order
        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        int userDefinedArgNumber = simpleTemplate ? renderArgs.size() : (renderArgs.size() - ((null == p) ? 0 : p.getRenderArgDescriptions().size()));
        if (0 < userDefinedArgNumber) {
            pn();
            ptn("@SuppressWarnings(\"unchecked\") public void setRenderArgs(Object... args) {");
            {
                p2tn("int _p = 0, l = args.length;");
                int i = userDefinedArgNumber;
                for (String argName : renderArgs.keySet()) {
                    RenderArgDeclaration arg = renderArgs.get(argName);
                    p2t("if (_p < l) { Object v = args[_p++]; boolean isString = (\"java.lang.String\".equals(\"")
                            .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                            .p(argName).p(" = (").p(arg.type).pn(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
                    if (--i == 0) break;
                }
            }
            ptn("}");
        }

        // -- output setRenderArg by name
        pn();
        ptn("@SuppressWarnings(\"unchecked\") @Override public void setRenderArg(String name, Object arg) {");
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (\"").p(argName).p("\".equals(name)) this.").p(argName).p("=(").p(arg.type).pn(")arg;");
        }
        p2t("super.setRenderArg(name, arg);\n\t}\n");

        // -- output setRenderArg by position
        pn();
        ptn("@SuppressWarnings(\"unchecked\") public void setRenderArg(int pos, Object arg) {");
        p2tn("int _p = 0;");
        for (String argName : renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p2t("if (_p++ == pos) { Object v = arg; boolean isString = (\"java.lang.String\".equals(\"")
                    .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                    .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }").pn();
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
                p2t("__parent.setRenderArg(").p(i).p(", ").p(pd.valDef).pn(");");
            } else {
                p2t("__parent.setRenderArg(\"").p(pd.nameDef).p("\", ").p(pd.valDef).pn(");");
            }
            if (extendDeclareLineNo != -1) {
                pn(" //line: ").p(extendDeclareLineNo);
            }
        }
        ptn("}");
    }

    protected void pSetup() {
        if (!logTime && renderArgs.isEmpty()) return;
        pn();
        ptn("@Override protected void setup() {");
        if (logTime) {
            p2tn("_logTime = true;");
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

    protected void pInlineTags() {
        pn();
        for (InlineTag tag : inlineTags) {
            p("\nprotected ").p(tag.retType).p(" ").p(tag.tagName).p(tag.signature).p("{\n");
            boolean isVoid = "void".equals(tag.retType);StringBuilder sb = out();
            if (!isVoid) {
                p(tag.body);
            } else {
                for (TextBuilder b : tag.builders) {
                    b.build();
                }
            }
            p("\n}");
        }
    }

    public String buildBody = null;

    protected void pBuild() {
        pn();
        pn();
        ptn("@Override public com.greenlaw110.rythm.utils.TextBuilder build(){");
        p2t("out().ensureCapacity(").p(tmpl.length()).p(");").pn();
        StringBuilder sb = new StringBuilder();
        StringBuilder old = out();
        setOut(sb);
        for (TextBuilder b : builders) {
            b.build();
        }
        buildBody = sb.toString();
        setOut(old);
        p(buildBody);
        p("\n\t\treturn this;\n\t}\n");
    }

}
