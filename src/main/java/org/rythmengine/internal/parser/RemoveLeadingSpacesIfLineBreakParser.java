/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

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

import org.rythmengine.internal.IContext;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/02/13
 * Time: 7:20 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RemoveLeadingSpacesIfLineBreakParser extends ParserBase implements IRemoveLeadingSpacesIfLineBreak {
    protected RemoveLeadingSpacesIfLineBreakParser(IContext context) {
        super(context);
    }
}
