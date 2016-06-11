/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.template.TagBase;
import org.rythmengine.toString.ToStringStyle;

/**
 * The template base used in ToString mode or AutoToString mode
 */
public abstract class ToStringTemplateBase extends TagBase {
    protected ToStringStyle __style = ToStringStyle.DEFAULT_STYLE;
}
