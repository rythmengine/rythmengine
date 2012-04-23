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
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.IImplicitRenderArgProvider;
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
    private String tmpl;
    private String cName;
    private String pName;
    private String tagName;
    private boolean isTag() {
        return null != tagName;
    }
    private String initCode = null;
    public void setInitCode(String code) {
        if (null != initCode) throw new ParseException(templateClass, parser.currentLine(), "@init section already declared.");
        initCode = code;
    }
    private String extended; // the cName of the extended template
    private String extended() {
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

    public CodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine) {
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
        this.parser = new TemplateParser(this);
        this.templateClass = templateClass;
    }

    public void clear() {
        out().ensureCapacity(0);
        this.builders.clear();
        this.engine = null;
        if (null != this.extendArgs) this.extendArgs.pl.clear();
        this.extendedTemplateClass = null;
        this.imports.clear();
        this.inlineTagBodies.clear();
        this.inlineTags.clear();
        this.parser = null;
        this.templateClass = null;
        this.renderArgs.clear();
    }

    public void merge(CodeBuilder codeBuilder) {
        if (null == codeBuilder) return;
        this.imports.addAll(codeBuilder.imports);
        for (String key: codeBuilder.inlineTags.keySet()) {
            InlineTag tag = codeBuilder.inlineTags.get(key).clone(this);
            inlineTags.put(key, tag);
        }
        this.initCode = new StringBuilder(S.toString(this.initCode)).append(S.toString(codeBuilder.initCode)).toString();
        this.renderArgs.putAll(codeBuilder.renderArgs);
    }

    public String className() {
        return cName;
    }

    private static Set<String > globalImports = new HashSet<String>();

    public static void registerImports(String imports) {
        globalImports.addAll(Arrays.asList(imports.split(",")));
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
        List<TextBuilder> builders = new ArrayList<TextBuilder>();
        InlineTag(String name, String sig) {
            tagName = name;
            signature = sig;
        }
        InlineTag clone(CodeBuilder newCaller) {
            InlineTag tag = new InlineTag(tagName, signature);
            tag.builders.clear();
            for (TextBuilder tb: builders) {
                TextBuilder newTb = tb.clone(newCaller);
                tag.builders.add(newTb);
            }
            return tag;
        }
    }
    private Map<String, InlineTag> inlineTags = new HashMap<String, InlineTag>();
    private Stack<List<TextBuilder>> inlineTagBodies = new Stack<List<TextBuilder>>();
    public void defTag(String tagName, String signature) {
        if (inlineTags.containsKey(tagName)) {
            throw new ParseException(templateClass, parser.currentLine(), "inline tag already defined: %s", tagName);
        }
        InlineTag tag = new InlineTag(tagName, signature);
        inlineTags.put(tagName, tag);
        inlineTagBodies.push(builders);
        builders = tag.builders;
    }
    public void endTag() {
        if (inlineTagBodies.empty()) throw new ParseException(templateClass, parser.currentLine(), "Unexpected tag definition close");
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
        TemplateBase includeTag = (TemplateBase)engine.tags.get(tagName);
        if (includeTag instanceof JavaTagBase) {
            throw new ParseException(templateClass, lineNo, "cannot include Java tag: %s", include);
        }
        TemplateClass includeTc = includeTag.getTemplateClass(false);
        if (null == includeTc.codeBuilder) {
            // loaded from persistent class cache?
            includeTc.buildSourceCode();
        }
        merge(includeTc.codeBuilder);
        templateClass.addIncludeTemplateClass(includeTc);
        return includeTc.codeBuilder.buildBody;
    }

    public void setExtended(String extended, InvokeTagParser.ParameterDeclarationList args, int lineNo) {
        if (null != this.extended) {
            throw new ParseException(templateClass, lineNo, "Extended template already declared");
        }
        String fullName = engine.testTag(extended, templateClass);
        if (null == fullName) {
            // try legacy style
            setExtended_deprecated(extended, args, lineNo);
            logger.warn("Extended template declaration \"%s\" is deprecated, please switch to the new style \"%s\"", extended, engine.resourceManager.getFullTagName(extendedTemplateClass));
        } else {
            TemplateBase tb = (TemplateBase) engine.tags.get(fullName);
            TemplateClass tc = tb.getTemplateClass(false);
            this.extended = tc.name();
            this.extendedTemplateClass = tc;
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
            String me = templateClass.getKey();
            int pos = me.lastIndexOf("/");
            extended = me.substring(0, pos) + "/" + extended;
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
        this.extendArgs = args;
    }

    private boolean logTime = false;
    public void setLogTime() {
        logTime = true;
    }

    public void addRenderArgs(RenderArgDeclaration declaration) {
        renderArgs.put(declaration.name, declaration);
    }

    public void addRenderArgs(String type, String name) {
        renderArgs.put(name, new RenderArgDeclaration(name, type));
    }

    public void addBuilder(TextBuilder builder) {
        builders.add(builder);
    }

    String template() {
        return tmpl;
    }

    @Override
    public TextBuilder build() {
        try {
            parser.parse();
            invokeDirectives();
            addDefaultRenderArgs();
            pPackage();
            pImports();
            pClassOpen();
            pTagImpl();
            pInitCode();
            pSetup();
            pExtendInitArgCode();
            pRenderArgs();
            pInlineTags();
            pBuild();
            pClassClose();
            return this;
        } catch (NotRythmTemplateException e) {
            isNotRythmTemplate = true;
            return this;
        }
    }

    private void invokeDirectives() {
        for (TextBuilder b: builders) {
            if (b instanceof IDirective) {
                ((IDirective)b).call();
            }
        }
    }

    private void addDefaultRenderArgs() {
        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        if (null == p) return;
        Map<String, ?> defArgs = p.getRenderArgDescriptions();
        for (String name: defArgs.keySet()) {
            Object o = defArgs.get(name);
            String type = (o instanceof Class<?>) ? ((Class<?>)o).getName() : o.toString();
            addRenderArgs(type, name);
        }
    }

    private void pPackage() {
        if (!S.isEmpty(pName)) p("\npackage ").p(pName).p(";");
    }

    // print imports
    private void pImports() {
        for (String s: imports) {
            p("\nimport ").p(s).p(';');
        }
        for (String s: globalImports) {
            p("\nimport ").p(s).p(';');
        }
        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        if (null != p) {
            for (String s: p.getImplicitImportStatements()) {
                p("\nimport ").p(s).p(';');
            }
        }
        // common imports
        p("\nimport java.util.*;");
        p("\nimport java.io.*;");
    }

    private void pClassOpen() {
        p("\npublic class ").p(cName).p(" extends ").p(extended()).p(" {").p(extendedResourceMark());
    }

    private void pClassClose() {
        p("\n}");
    }

    private void pRenderArgs() {
        // -- output private members
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\t\tprotected ").p(arg.type).p(" ").p(argName);
            if (null != arg.defVal) {
                p("=").p(arg.defVal).p(";");
            } else {
                p(";");
            }
        }

        // -- output setRenderArgs method
        p("\n\t@SuppressWarnings(\"unchecked\") public void setRenderArgs(java.util.Map<String, Object> args) {");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\tif (null != args && args.containsKey(\"").p(argName).p("\")) this.").p(argName).p("=(").p(arg.type).p(")args.get(\"").p(argName).p("\");");
        }
        p("\n\tsuper.setRenderArgs(args);\n}");

        // -- output setRenderArgs method with args passed in positioned order
        IImplicitRenderArgProvider p = engine.implicitRenderArgProvider;
        int userDefinedArgNumber = renderArgs.size() - ((null == p) ? 0 : p.getRenderArgDescriptions().size());
        if (0 < userDefinedArgNumber) {
            p("\n@SuppressWarnings(\"unchecked\") public void setRenderArgs(Object... args) {");
            {
                p("\n\tint p = 0, l = args.length;");
                int i = userDefinedArgNumber;
                for (String argName: renderArgs.keySet()) {
                    RenderArgDeclaration arg = renderArgs.get(argName);
                    p("\n\tif (p < l) { Object v = args[p++]; boolean isString = (\"java.lang.String\".equals(\"")
                            .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                            .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
                    if (--i == 0) break;
                }
                p("\n}");
            }
        }

        // -- output setRenderArg by name
        p("\n@SuppressWarnings(\"unchecked\") @Override public void setRenderArg(String name, Object arg) {");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\tif (\"").p(argName).p("\".equals(name)) this.").p(argName).p("=(").p(arg.type).p(")arg;");
        }
        p("\n\tsuper.setRenderArg(name, arg);\n}");

        // -- output setRenderArg by position
        p("\n@SuppressWarnings(\"unchecked\") public void setRenderArg(int pos, Object arg) {");
        p("\nint p = 0;");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\nif (p++ == pos) { Object v = arg; boolean isString = (\"java.lang.String\".equals(\"")
                    .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                    .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
        }
        // the first argument has a default name "arg"
        p("\n\tif(0 == pos) setRenderArg(\"arg\", arg);");
        p("\n}");
    }

    private void pExtendInitArgCode() {
        if (null == extendArgs || extendArgs.pl.size() < 1) return;
        p("\n@Override protected void loadExtendingArgs() {");
        for (int i = 0; i < extendArgs.pl.size(); ++i) {
            InvokeTagParser.ParameterDeclaration pd = extendArgs.pl.get(i);
            if (S.isEmpty(pd.nameDef)) {
                p("\n\t__parent.setRenderArg(").p(i).p(", ").p(pd.valDef).p(");");
            } else {
                p("\n\t__parent.setRenderArg(\"").p(pd.nameDef).p("\", ").p(pd.valDef).p(");");
            }
            if (extendDeclareLineNo != -1) {
                p(" //line: ").p(extendDeclareLineNo);
            }
        }
        p("\n}\n");
    }

    private void pSetup() {
        p("\n@Override protected void setup() {");
            if (logTime) {
                p("\n\t_logTime = true;");
            }
            for (String argName: renderArgs.keySet()) {
                RenderArgDeclaration arg = renderArgs.get(argName);
                p("\n\tif (").p(argName).p(" == null) {");
                //p("\n\tif (").p(argName).p(" == ").p(RenderArgDeclaration.defVal(arg.type)).p(") {");
                p("\n\t\t").p(argName).p("=(").p(arg.type).p(")_get(\"").p(argName).p("\");\n\t}");
            }
        p("\n}\n");
    }

    private void pInitCode() {
        if (null == initCode) return;
        p("\n@Override public void init() {").p(initCode).p(";").p("}\n");
    }

    private void pTagImpl() {
        if (!isTag()) return;
        p("\n@Override public java.lang.String getName() {\n\treturn \"").p(tagName).p("\";\n}\n");
    }

    private void pInlineTags() {
        for (InlineTag tag: inlineTags.values()) {
            p("\nprotected String ").p(tag.tagName).p(tag.signature).p("{\n");
            for (TextBuilder b: tag.builders) {
                b.build();
            }
            p("\nreturn \"\";\n}\n");
        }
    }

    public String buildBody = null;

    private void pBuild() {
        p("\n@Override public com.greenlaw110.rythm.utils.TextBuilder build(){");
        p("\n\tout().ensureCapacity(").p(tmpl.length()).p(");");
        StringBuilder sb = new StringBuilder();
        StringBuilder old = out();
        setOut(sb);
        for (TextBuilder b: builders) {
            b.build();
        }
        buildBody = sb.toString();
        setOut(old);
        p(buildBody);
        p("\nreturn this;\n}");
    }

}
