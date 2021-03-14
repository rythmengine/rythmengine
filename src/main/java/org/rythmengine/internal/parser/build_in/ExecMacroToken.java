/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

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

import org.rythmengine.exception.ParseException;
import org.rythmengine.internal.CodeBuilder;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.CodeToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 19/07/12
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExecMacroToken extends CodeToken {
    public ExecMacroToken(String macro, IContext context, int line) {
        super(macro, context);
        this.line = line;
    }

    @Override
    public void output() {
        CodeBuilder cb = ctx.getCodeBuilder();
        if (!cb.hasMacro(s)) {
            throw new ParseException(ctx.getEngine(), ctx.getTemplateClass(), line, "Cannot find macro definition for \"%s\"", s);
        }
        List<Token> list = cb.getMacro(s);
        for (Token tb : list) {
            tb.build();
        }
    }
}
