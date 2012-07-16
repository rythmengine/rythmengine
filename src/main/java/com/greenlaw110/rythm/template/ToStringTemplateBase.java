package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.toString.ToStringStyle;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 14/07/12
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ToStringTemplateBase extends TagBase {
    protected ToStringStyle __style = ToStringStyle.DEFAULT_STYLE;
    protected void foo() {
        __style.append(out(), "", "", null);
    }
}
