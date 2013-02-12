package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.internal.IContext;

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
