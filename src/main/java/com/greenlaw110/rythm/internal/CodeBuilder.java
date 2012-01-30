package com.greenlaw110.rythm.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.parser.NotRythmTemplateException;
import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;


public class CodeBuilder extends TextBuilder {
    
    public static class RenderArgDeclaration {
        String name;
        String type;
        String defVal;
        
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
    private String extended; // the cName of the extended template
    private String extended() {
        String defClass = isTag() ? TagBase.class.getName() : TemplateBase.class.getName();
        return null == extended ? defClass : extended;
    }
    private TemplateClass extendedTemplateClass;
    public TemplateClass getExtendedTemplateClass() {
        return extendedTemplateClass;
    }
    public RythmEngine engine;
    Set<String> imports = new HashSet<String>();
    // <argName, argClass>
    Map<String, RenderArgDeclaration> renderArgs = new LinkedHashMap<String, RenderArgDeclaration>();
    private List<TextBuilder> builders = new ArrayList<TextBuilder>();
    
    public CodeBuilder(String template, String className, String tagName, RythmEngine engine) {
        tmpl = template;
        this.tagName = tagName;
        className = className.replace('/', '.');
        cName = className;
        int i = className.lastIndexOf('.');
        if (-1 < i) {
            cName = className.substring(i + 1);
            pName = className.substring(0, i);
        }
        this.engine = null == engine ? Rythm.engine : engine;
    }
    
    public String className() {
        return cName;
    }
    
    public void addImport(String imprt) {
        imports.add(imprt);
    }
    
    public void defTag(String tagName) {
        this.tagName = tagName;
    }
    
    public void setExtended(String extended) {
        if (null != this.extended) throw new ParseException("extended already set for this page");
        TemplateClass tc = engine.classes.getByTemplate(extended);
        String origin = extended;
        if (null == tc) {
            if (!extended.endsWith(TemplateClass.CN_SUFFIX)) extended = extended + TemplateClass.CN_SUFFIX;
            tc = engine.classes.getByClassName(extended);
        }
        if (null == tc) {
            tc = new TemplateClass(origin, engine);
        }
        this.extended = tc.name();
        this.extendedTemplateClass = tc;
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
            new TemplateParser(this).parse();
        } catch (NotRythmTemplateException e) {
            isNotRythmTemplate = true;
            return this;
        }
        invokeDirectives();
        addDefaultRenderArgs();
        pPackage();
        pImports();
        pClassOpen();
        pTagImpl();
        pRenderArgs();
        pBuild();
        pClassClose();
        return this;
    }
    
    private void invokeDirectives() {
        for (TextBuilder b: builders) {
            if (b instanceof IDirective) {
                ((IDirective)b).call();
            }
        }
    }
    
    private void addDefaultRenderArgs() {
        Map<String, ?> defArgs = engine.defaultRenderArgs;
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
        // common imports
        p("\nimport java.util.*;");
        p("\nimport java.io.*;");
        p("\nimport java.util.regex.*;");
    }
    
    private void pClassOpen() {
        p("\npublic class ").p(cName).p(" extends ").p(extended()).p(" {");
    }
    
    private void pClassClose() {
        p("\n}");
    }
    
    private void pRenderArgs() {
        // -- output private members
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\t\tprivate ").p(arg.type).p(" ").p(argName);
            if (null != arg.defVal) {
                p("=").p(arg.defVal).p(";");
            } else {
                p(";");
            }
        }
        // - this moved to TemplateBase: p("\n\tprotected java.util.Map<String, Object> _properties = new java.util.HashMap();");
        // - this moved to TagBase: if (isTag()) p("\n\tprivate com.greenlaw110.rythm.runtime.ITag.Body _body = null;");

        // -- output setRenderArgs method
        p("\n\t@SuppressWarnings(\"unchecked\") public void setRenderArgs(java.util.Map<String, Object> args) {");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\tif (null != args && args.containsKey(\"").p(argName).p("\")) this.").p(argName).p("=(").p(arg.type).p(")args.get(\"").p(argName).p("\");");
        }
        p("\n\t_properties.putAll(args);\n}");
        // this moved to TagBase: if (isTag()) p("\n\tif (null == _body) _body = args.get(\"body\");\n}");

        // -- output setRenderArgs method with args passed in positioned order
        p("\n@SuppressWarnings(\"unchecked\") public void setRenderArgs(Object... args) {");
        p("\n\tint p = 0, l = args.length;");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\tif (p < l) { Object v = args[p++]; boolean isString = (\"java.lang.String\".equals(\"")
                .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
        }
        p("\n}");
        
        // -- output setRenderArg by name
        p("\n@SuppressWarnings(\"unchecked\") @Override public void setRenderArg(String name, Object arg) {");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\n\tif (\"").p(argName).p("\".equals(name)) this.").p(argName).p("=(").p(arg.type).p(")arg;");
        }
        //moved to TagBase: if (isTag()) p("\n\tif (\"_body\".equals(name)) this._body = (com.greenlaw110.rythm.runtime.ITag.Body)arg;");
        p("\n\t_properties.put(name, arg);\n}");

        // -- output getRenderArgs
        // this is moved to TemplateBase
        // p("\n@SuppressWarnings(\"unchecked\") @Override public java.util.Map<String,Object> getRenderArgs() { \n\treturn new HashMap<String, Object>(_properties);\n}");

        // -- output getRenderArg by name - moved to TemplateBase
//        p("\n@SuppressWarnings(\"unchecked\") protected Object internalGetRenderArg(String name) {");
//        // moved to tag base if (isTag()) p("\n\tif (\"_body\".equals(name)) return this._body;");
//        p("\n\treturn _properties.get(name);");
//        p("\n}");

        // -- output setRenderArg by position
        p("\n@SuppressWarnings(\"unchecked\") public void setRenderArg(int pos, Object arg) {");
        p("\nint p = 0;");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\nif (p++ == pos) { Object v = arg; boolean isString = (\"java.lang.String\".equals(\"")
                    .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                    .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
        }
        p("\n}");
    }

    private void pTagImpl() {
        if (!isTag()) return;
        p("\n@Override public java.lang.String getName() {\n\treturn \"").p(tagName).p("\";\n}\n");
    }

    private void pBuild() {
        p("\n@Override public com.greenlaw110.rythm.utils.TextBuilder build(){");
        for (TextBuilder b: builders) {
            b.build();
        }
        p("\nreturn this;\n}");
    }
    
}
