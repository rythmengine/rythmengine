/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser;

import org.rythmengine.internal.IContext;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/02/13
 * Time: 7:20 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RemoveLeadingLineBreakAndSpacesParser extends ParserBase implements IRemoveLeadingLineBreakAndSpaces {
    protected RemoveLeadingLineBreakAndSpacesParser(IContext context) {
        super(context);
    }
}
