package com.greenlaw110.rythm.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.greenlaw110.rythm.util.S;
import com.greenlaw110.rythm.util.TextBuilder;


public class CodeBuilder extends TextBuilder {
    
    class RenderArgDeclaration {
        String name;
        String type;
        String defVal;
        
        RenderArgDeclaration(String name, String type) {
            this.name = name;
            this.type = type;
            this.defVal = defVal(type);
        }

        private String defVal(String type) {
            if (type.equals("String"))
                return "\"\"";
            else if (type.equals("boolean"))
                return "false";
            else if (type.equals("char"))
                return "(char)0";
            else if (type.equals("byte"))
                return "(byte)0";
            else if (type.equals("short"))
                return "(short)0";
            else if (type.equals("int"))
                return "0";
            else if (type.equals("float"))
                return "0f";
            else if (type.equals("long"))
                return "0L";
            else if (type.equals("double"))
                return "0d";

            return "null";
        }
    }
    
    private String tmpl;
    private String cName;
    private String pName;
    Set<String> imports = new HashSet<String>();
    // <argName, argClass>
    Map<String, RenderArgDeclaration> renderArgs = new LinkedHashMap<String, RenderArgDeclaration>();
    private List<TextBuilder> builders = new ArrayList<TextBuilder>();
    
    public CodeBuilder(String template, String className) {
        tmpl = template;
        className = className.replace('/', '.');
        cName = className;
        int i = className.lastIndexOf('.');
        if (-1 < i) {
            cName = className.substring(i + 1);
            pName = className.substring(0, i);
        }
    }

    public void addImport(String imprt) {
        imports.add(imprt);
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
        new TemplateParser(this).parse();
        invokeDirectives();
        pPackage();
        pImports();
        pClassOpen();
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
    
    private void pPackage() {
        if (!S.isEmpty(pName)) p("\npackage ").p(pName).p(";");
    }
    
    // print imports
    private void pImports() {
        for (String s: imports) {
            p("\nimport ").p(s).p(';');
        }
    }
    
    private void pClassOpen() {
        p("\npublic class ").p(cName).p(" extends com.greenlaw110.rythm.template.TemplateBase {");
    }
    
    private void pClassClose() {
        p("\n}");
    }
    
    private void pRenderArgs() {
        // -- output private members
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\nprivate ").p(arg.type).p(" ").p(argName).p("=").p(arg.defVal).p(";");
        }
        
        // -- output setRenderArgs method
        p("\n@SuppressWarnings(\"unchecked\") public void setRenderArgs(java.util.Map<String, Object> args) {");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\nif (null != args && args.containsKey(\"").p(argName).p("\")) this.").p(argName).p("=(").p(arg.type).p(")args.get(\"").p(argName).p("\");");
        }
        p("\n}");

        // -- output setRenderArgs method with args passed in positioned order
        p("\n@SuppressWarnings(\"unchecked\") public void setRenderArgs(Object... args) {");
        p("\nint p = 0, l = args.length;");
        for (String argName: renderArgs.keySet()) {
            RenderArgDeclaration arg = renderArgs.get(argName);
            p("\nif (p < l) { Object v = args[p++]; boolean isString = (\"java.lang.String\".equals(\"")
                .p(arg.type).p("\") || \"String\".equals(\"").p(arg.type).p("\")); ")
                .p(argName).p(" = (").p(arg.type).p(")(isString ? (null == v ? \"\" : v.toString()) : v); }");
        }
        p("\n}");
    }
    
    private void pBuild() {
        p("\n@Override public com.greenlaw110.rythm.util.TextBuilder build(){");
        for (TextBuilder b: builders) {
            b.build();
        }
        p("\nreturn this;\n}");
    }
    
}
