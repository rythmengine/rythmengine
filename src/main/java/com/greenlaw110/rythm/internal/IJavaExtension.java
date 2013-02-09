package com.greenlaw110.rythm.internal;

import java.util.regex.Pattern;

/**
 * <code>IJavaExtension</code> defines meta structure used by rythm to "extend" an expression.
 * <p/>
 * This enable rythm to provide the functionality of Java extension concept used in playframework
 * http://www.playframework.org/documentation/1.2.4/templates#extensions
 * <p/>
 * User: luog
 * Date: 21/02/12
 * Time: 6:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IJavaExtension {
    String extend(String s, String signature);

    Pattern pattern1();

    Pattern pattern2();

    String methodName();

    static class VoidParameterExtension implements IJavaExtension {
        private String methodName = null;
        private String fullMethodName = null;
        private Pattern pattern1 = null;
        private Pattern pattern2 = null;

        public VoidParameterExtension(String waiveName, String name) {
            this(waiveName, name, String.format("com.greenlaw110.rythm.utils.S.%s", name));
        }

        public VoidParameterExtension(String waiveName, String name, String fullName) {
            methodName = name;
            fullMethodName = fullName;
            pattern1 = Pattern.compile(String.format(".*(?<!%s)\\.(?i)%s\\s*\\(\\s*\\)\\s*$", waiveName, methodName));
            pattern2 = Pattern.compile(String.format("\\.(?i)%s\\s*\\(\\s*\\)\\s*$", methodName));
        }

        @Override
        public Pattern pattern1() {
            return pattern1;
        }

        public Pattern pattern2() {
            return pattern2;
        }

        @Override
        public String extend(String s, String signature) {
            return String.format("%s(%s)", fullMethodName, s);
        }

        @Override
        public String methodName() {
            return methodName;
        }
    }

    static class ParameterExtension implements IJavaExtension {
        private String methodName = null;
        private String fullMethodName = null;
        private Pattern pattern1 = null;
        private Pattern pattern2 = null;

        public ParameterExtension(String waiveName, String name, String signature) {
            this(waiveName, name, signature, String.format("com.greenlaw110.rythm.utils.S.%s", name));
        }

        public ParameterExtension(String waiveName, String name, String signature, String fullName) {
            methodName = name;
            fullMethodName = fullName;
            pattern1 = Pattern.compile(String.format(".*(?<!%s)\\.%s\\s*\\((\\s*%s?\\s*)\\)\\s*$", waiveName, methodName, signature));
            pattern2 = Pattern.compile(String.format("\\.%s\\s*\\((\\s*%s?\\s*)\\)\\s*$", methodName, signature));
        }

        @Override
        public Pattern pattern1() {
            return pattern1;
        }

        @Override
        public Pattern pattern2() {
            return pattern2;
        }

        @Override
        public String extend(String s, String signature) {
            return String.format("%s(%s, %s)", fullMethodName, s, signature);
        }

        @Override
        public String methodName() {
            return methodName;
        }
    }

}
