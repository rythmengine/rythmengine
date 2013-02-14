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
package com.greenlaw110.rythm.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Patterns {
    /**
     * Recursive regexp, used only with com.stevesoft.pat.Regex
     */
    Expression("(?@())*"),
    Expression2("(?@())?[a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.\\=]*)*(?@[])*(?@())*)*"),
    VarName("[_a-zA-Z][a-zA-Z0-9_]*"),
    Blank("([\\s\\r\\n]+)"),
    NewLine("([\r\n]+)"),
    RESERVED("(if|else|for|null|class|return|break|continue|go|interface|extend|throw|final|finally|private|public|protected|static|void|enum|package|switch|case|do|until|while)"),
    Type("[a-zA-Z0-9_\\.\\[\\]\\<\\>,]+");

    private String s_;
    private Pattern p_;

    private Patterns(String pattern) {
        s_ = pattern;
    }

    @Override
    public String toString() {
        return s_;
    }

    public Pattern pattern() {
        if (null == p_) {
            p_ = Pattern.compile(s_);
        }
        return p_;
    }

    public Matcher matcher(String s) {
        return pattern().matcher(s);
    }

    public boolean matches(String s) {
        return matcher(s).matches();
    }
}
