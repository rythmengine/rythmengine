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
package org.rythmengine;


enum DefaultShutdownService implements ShutdownService {

    INSTANCE;

    // Runtime.addShutdownHook might lead to memory leak
    // checkout https://github.com/greenlaw110/Rythm/issues/199
    // Updates: another issue #296 indicate the shutdown service
    // is okay to be called only on Rythm.engine instance. Thus
    // the comment out code has been re-enabled
    @Override
    public void setShutdown(final Runnable runnable) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (runnable != null)
                        runnable.run();
                }
            });
        } catch (Throwable t) {
            // Nothing to do
        }
    }
}
