package org.rythmengine.resource;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.compiler.TemplateClass;

/**
 * Implement common logic of an {@link ITemplateResourceLoader}
 */
public abstract class ResourceLoaderBase implements ITemplateResourceLoader {

    @Override
    public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerTemplateClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFullName(TemplateClass tc, RythmEngine engine) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void scan(String root, TemplateResourceManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
