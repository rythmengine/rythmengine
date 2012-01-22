package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.template.ITemplate;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITemplateClonable extends Cloneable {
    ITemplate cloneMe();
}
