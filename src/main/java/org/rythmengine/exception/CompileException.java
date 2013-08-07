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
package org.rythmengine.exception;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.RythmEvents;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.internal.parser.build_in.ExpressionParser;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 14/02/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompileException extends RythmException {

    public static class CompilerException extends RuntimeException {
        public String className;
        public int javaLineNumber;
        public String message;

        private CompilerException() {
        }
    }

    public static CompilerException compilerException(String className, int line, String message) {
        CompilerException e = new CompilerException();
        e.javaLineNumber = line;
        e.message = ExpressionParser.reversePositionPlaceHolder(message);
        e.className = className;
        return e;
    }

    public CompileException(RythmEngine engine, TemplateClass tc, int javaLineNumber, String message) {
        super(engine, tc, javaLineNumber, -1, message);
        RythmEvents.COMPILE_FAILED.trigger(engine, tc);
    }

}
