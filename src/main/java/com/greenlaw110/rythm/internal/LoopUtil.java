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
package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.utils.S;

/**
 * Used to help track loop state
 */
public class LoopUtil {
    private final Object obj;
    private final boolean isFirst;
    private final boolean isLast;

    public LoopUtil(boolean isFirst, boolean isLast) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = null;
    }
    
    public LoopUtil(boolean isFirst, boolean isLast, Object obj) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = obj;
    }

    public String sep(String sep) {
        return postSep(sep);
    }

    public String preSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.str(obj);
        }
        return result + (isFirst ? "" : sep);
    }

    public String postSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.str(obj);
        }
        return result + (isLast ? "" : sep);
    }
}
