package org.rythmengine;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

class _VM {

    public static final String INFO = System.getProperty("java.vm.name");
    public static final Boolean IS_SERVER = INFO.toUpperCase().contains("SERVER");
    public static final Boolean IS_64 = INFO.contains("64");
    public static final String SPEC_VERSION = System.getProperty("java.specification.version");

    public static final int VERSION; static {
        int _pos = SPEC_VERSION.lastIndexOf('.');
        VERSION = Integer.parseInt(SPEC_VERSION.substring(_pos + 1));
    }

    private _VM() {}

}
