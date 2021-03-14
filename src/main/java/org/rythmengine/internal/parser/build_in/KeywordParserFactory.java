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

import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.IKeyword;
import org.rythmengine.internal.IKeywordParserFactory;
import org.rythmengine.internal.parser.ParserBase;
import com.stevesoft.pat.Regex;

import java.util.regex.Pattern;

public abstract class KeywordParserFactory extends CaretParserFactoryBase implements IKeywordParserFactory {
    public abstract IKeyword keyword();

    public String[] interests() {
        return new String[]{};
    }

    protected abstract String patternStr();

    private Pattern p = null;

    protected Pattern ptn(IDialect d) {
        if (null == p) {
            p = ParserBase.pattern(patternStr(), d.a(), keyword());
        }
        return p;
    }

    protected Regex reg(IDialect d) {
        return new Regex(String.format(patternStr(), d.a(), keyword()));
    }

}
