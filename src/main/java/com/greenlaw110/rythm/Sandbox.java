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
package com.greenlaw110.rythm;

import com.greenlaw110.rythm.sandbox.SandboxExecutingService;

import java.io.File;
import java.util.Map;

/**
 * A wrapper of Rythm engine and make sure the rendering is happen in Sandbox mode
 */
public class Sandbox {

    private static final InheritableThreadLocal<Boolean> sandboxMode = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    static boolean sandboxMode() {
        return sandboxMode.get();
    }


    RythmEngine engine;
    SandboxExecutingService secureExecutor = null;

    public Sandbox(RythmEngine engine, SandboxExecutingService executor) {
        this.engine = engine;
        this.secureExecutor = executor;
    }

    private RythmEngine engine() {
        if (null != engine) return engine;
        return Rythm.engine();
    }

    private Map<String, Object> userContext;

    public Sandbox setUserContext(Map<String, Object> context) {
        this.userContext = context;
        return this;
    }

    public String render(String template, Object... args) {
        sandboxMode.set(true);
        try {
            return secureExecutor.execute(userContext, template, args);
        } finally {
            sandboxMode.set(false);
        }
    }

    public String render(File file, Object... args) {
        sandboxMode.set(true);
        try {
            return secureExecutor.execute(userContext,file, args);
        } finally {
            sandboxMode.set(false);
        }
    }

    public static String hasAccessToRestrictedClasses(RythmEngine engine, String code) {
        for (String s : engine.conf().restrictedClasses()) {
            if (code.contains(s)) return s;
        }
        return null;
    }

}
