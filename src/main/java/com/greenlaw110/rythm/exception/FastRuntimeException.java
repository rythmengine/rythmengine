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
package com.greenlaw110.rythm.exception;

/**
 * Fast Exception - skips creating stackTrace.
 * <p/>
 * More info here: http://www.javaspecialists.eu/archive/Issue129.html
 */
public class FastRuntimeException extends RuntimeException {
    public FastRuntimeException() {
        super();
    }

    public FastRuntimeException(String desc) {
        super(desc);
    }

    public FastRuntimeException(String desc, Throwable cause) {
        super(desc, cause);
    }

    public FastRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Since we override this method, no stacktrace is generated - much faster
     *
     * @return always null
     */
    public Throwable fillInStackTrace() {
        return null;
    }
}
