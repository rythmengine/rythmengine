/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.compiler;

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

import org.rythmengine.exception.FastRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/02/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassReloadException extends FastRuntimeException {
    public ClassReloadException(String msg) {
        super(msg);
    }

    public ClassReloadException(String msg, Throwable t) {
        super(msg, t);
    }
}
