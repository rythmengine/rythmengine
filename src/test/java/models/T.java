package models;

import java.util.*;
import org.rythmengine.template.TemplateBase;
import java.io.*;

public class T extends org.rythmengine.template.TagBase {

    @Override public java.lang.String __getName() {
        return "Cae39fa57_a9df_3204_abdf_75d06d63cece__R_T_C__";
    }


    protected java.lang.String __renderArgName(int __pos) {
        int __p = 0;
        throw new ArrayIndexOutOfBoundsException();
    }

    protected java.util.Map<java.lang.String, java.lang.Class> __renderArgTypeMap() {
        java.util.Map<java.lang.String, java.lang.Class> __m = new java.util.HashMap<String, Class>();
        return __m;
    }

    @SuppressWarnings("unchecked")
    public TemplateBase __setRenderArgs(java.util.Map<java.lang.String, java.lang.Object> __args) {
        if (null == __args) throw new NullPointerException();
        if (__args.isEmpty()) return this;
        super.__setRenderArgs(__args);
        return this;
    }

    @SuppressWarnings("unchecked") @Override public TemplateBase __setRenderArg(java.lang.String __name, java.lang.Object __arg) {
        super.__setRenderArg(__name, __arg);
        return this;
    }

    @SuppressWarnings("unchecked") public TemplateBase __setRenderArg(int __pos, java.lang.Object __arg) {
        int __p = 0;
        if(0 == __pos) __setRenderArg("arg", __arg);
        return this;
    }


    class Foo {public String foo() {return "hello foo";}} //line: 1
    ;




    @Override public org.rythmengine.utils.TextBuilder build(){
        buffer().ensureCapacity(101);
        Foo foo = new Foo() //line: 2
                ; //line: 1
        p(" "); //line: 1

        try{pe(foo.foo());} catch (RuntimeException e) {__handleTemplateExecutionException(e);}  //line: 1

        return this;
    }

}
