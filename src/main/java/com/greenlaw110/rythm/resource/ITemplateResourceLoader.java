package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;


/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 27/01/12
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ITemplateResourceLoader {
    ITemplateResource load(String key);
    TemplateClass tryLoadTag(String tagName, TemplateClass tc);
    String getFullTagName(TemplateClass tc);
}
