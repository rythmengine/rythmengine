/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

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

/**
 * Use application or framework plugins based on rythm engine could
 * implement this interface to allow further process compiled
 * template classes.
 * <p/>
 * <p>One {@link org.rythmengine.RythmEngine engine instance} can have zero
 * or one <code>ITemplateClassEnhancer</code></p>
 */
public interface IByteCodeEnhancer {
    /**
     * Enhance byte code. This method is called after a template
     * class get compiled and before it is cached to disk
     *
     * @param className
     * @param classBytes
     * @return the bytecode
     * @throws Exception
     */
    byte[] enhance(String className, byte[] classBytes) throws Exception;

    /**
     * Not to be used by user application
     */
    public static class INSTS {
        public static final IByteCodeEnhancer NULL = new IByteCodeEnhancer() {
            @Override
            public byte[] enhance(String className, byte[] classBytes) throws Exception {
                return new byte[0];
            }
        };

        private INSTS() {
        }
    }
}
