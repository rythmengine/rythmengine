package com.greenlaw110.rythm.internal.compile;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import com.greenlaw110.rythm.Rythm;

public class ClassFileManager extends
        ForwardingJavaFileManager {
    /**
    * Instance of JavaClassObject that will store the
    * compiled bytecode of our class
    */
    private Map<String, JavaClassObject> jclassObjects = new HashMap<String, JavaClassObject>();

    /**
    * Will initialize the manager with the specified
    * standard java file manager
    *
    * @param standardManger
    */
    public ClassFileManager(StandardJavaFileManager
        standardManager) {
        super(standardManager);
    }

    /**
    * Will be used by us to get the class loader for our
    * compiled class. It creates an anonymous class
    * extending the SecureClassLoader which uses the
    * byte code created by the compiler and stored in
    * the JavaClassObject, and returns the Class for it
    */
    @Override
    public ClassLoader getClassLoader(Location location) {
        return new SecureClassLoader() {
            @Override
            protected Class<?> findClass(String name)
                throws ClassNotFoundException {
                JavaClassObject jclassObject = jclassObjects.get(name);
                if (null != jclassObject) {
                    byte[] b = jclassObject.getBytes();
                    return super.defineClass(name, b, 0, b.length);
                } else {
                    ClassLoader cl = Rythm.classLoader;
                    if (null == cl) return super.findClass(name);
                    return cl.loadClass(name);
                }
            }
        };
    }

    /**
    * Gives the compiler an instance of the JavaClassObject
    * so that the compiler can write the byte code into it.
    */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
        String className, Kind kind, FileObject sibling)
            throws IOException {
        JavaClassObject jclassObject = new JavaClassObject(className, kind);
        jclassObjects.put(className, jclassObject);
        return jclassObject;
    }
}