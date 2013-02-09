package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.toString.ToStringStyle;

/**
 * The template base used in ToString mode or AutoToString mode
 */
public abstract class ToStringTemplateBase extends TagBase {
    protected ToStringStyle __style = ToStringStyle.DEFAULT_STYLE;
}
