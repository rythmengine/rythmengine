/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

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

import org.rythmengine.RythmEngine;
import org.rythmengine.exception.ParseException;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.internal.compiler.TemplateClass;

import java.util.Locale;

public interface IContext {

    /**
     * Return the root source code builder
     *
     * @return the code builder
     */
    CodeBuilder getCodeBuilder();

    /**
     * Return the remaining template string that has not parsed yet
     *
     * @return remaining text to be parsed
     */
    String getRemain();

    /**
     * Do have have remain template content to be parsed
     *
     * @return true if there are remaining text to be parsed
     */
    boolean hasRemain();
    

    /**
     * @return the first remain character without moving cursor
     */
    char peek();

    /**
     * @return the first remain character and move the cursor one step
     */
    char pop();

    /**
     * Move the current cursor i steps ahead
     *
     * @param i
     */
    void step(int i);

    /**
     * Return any segment of template source
     *
     * @param start
     * @param end
     * @return template source
     */
    String getTemplateSource(int start, int end);

    /**
     * Notify context to enter a block
     *
     * @param bh the block handler
     */
    void openBlock(IBlockHandler bh);

    /**
     * Return current block handler
     *
     * @return current block handler
     * @throws ParseException
     */
    IBlockHandler currentBlock();

    /**
     * Notify context to close current block and return
     * the block close carets
     *
     * @throws ParseException
     */
    String closeBlock() throws ParseException;

    /**
     * Returns the current section name
     * @return the name of the current section
     */
    String currentSection();

    void setDialect(IDialect dialect);

    IDialect getDialect();

    /**
     * Return current line number
     *
     * @return current parsing line
     */
    int currentLine();

    int cursor();

    TemplateClass getTemplateClass();

    RythmEngine getEngine();

    public static enum Break {
        BREAK("break;"), RETURN("return false;");
        private String statement;

        private Break(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
    }

    public static enum Continue {
        CONTINUE("continue;"), RETURN("return true;");
        private String statement;

        private Continue(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
    }

    void pushBreak(Break b);

    Break peekBreak();

    Break popBreak();

    void pushContinue(Continue c);

    Continue peekContinue();

    Continue popContinue();

    boolean compactMode();

    void pushCompact(Boolean compact);

    Boolean peekCompact();

    Boolean popCompact();

    boolean insideBody();

    Boolean peekInsideBody();

    void pushInsideBody(Boolean b);

    Boolean popInsideBody();

    boolean insideBody2();

    Boolean peekInsideBody2();

    void pushInsideBody2(Boolean b);

    Boolean popInsideBody2();

    boolean insideDirectiveComment();

    void enterDirectiveComment();

    void leaveDirectiveComment();

    ICodeType peekCodeType();

    void pushCodeType(ICodeType type);

    ICodeType popCodeType();
    
    Locale peekLocale();

    void pushLocale(Locale locale);
    
    Locale popLocale();
    
}
