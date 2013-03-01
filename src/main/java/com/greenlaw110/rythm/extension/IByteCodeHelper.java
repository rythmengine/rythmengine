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
package com.greenlaw110.rythm.extension;

/**
 * A <code>IByteCodeHelper</code> could be plugged into {@link com.greenlaw110.rythm.RythmEngine}
 * in memory compilation system to provide extra way to locate class byte
 * code.
 * <p/>
 * <p>A usage example of <code>IByteCodeHelper</code> could be find in
 * Play!framework's Rythm plugin, which locates Play!Framework's
 * application classes when compiling template classes</p>
 * <p/>
 * <p>One {@link com.greenlaw110.rythm.RythmEngine engine instance} can have zero
 * or one <code>IByteCodeHelper</code></p>
 */
public interface IByteCodeHelper {
    /**
     * Return the byte code of a class specified by the
     * parameter
     *
     * @param typeName The full name of the class who's byte code to be located
     * @return the bytecode
     */
    byte[] findByteCode(String typeName);
}
