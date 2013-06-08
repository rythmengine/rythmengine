package org.rythmengine.resource;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implement common logic of an {@link ITemplateResourceLoader}
 */
public abstract class ResourceLoaderBase implements ITemplateResourceLoader {

    protected static ILogger logger = Logger.get(ResourceLoaderBase.class);

    public String getFullName(TemplateClass tc) {
        String key = tc.getKey().toString();
        if (key.startsWith("/") || key.startsWith("\\")) key = key.substring(1);
        String root = getResourceLoaderRoot();
        if (key.startsWith(root)) {
            key = key.replace(root, "");
        }
        if (key.startsWith("/") || key.startsWith("\\")) key = key.substring(1);
        int pos = key.lastIndexOf(".");
        if (-1 != pos) key = key.substring(0, pos);
        key = key.replace('/', '.').replace('\\', '.');
        key += tc.codeType.resourceNameSuffix();
        return key;
    }

    protected RythmEngine getDefaultEngine() {
        return Rythm.engine();
    }

    @Override
    public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerTemplateClass) {
        return tryLoadTemplate(tmplName, engine, callerTemplateClass, true);
    }
    
    private TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerClass, boolean processTagName) {
        if (null == engine) {
            engine = getDefaultEngine();
        }
        if (engine.templateRegistered(tmplName)) {
            return null;
        }
        String rythmSuffix = engine.conf().resourceNameSuffix();
        final List<String> suffixes = new ArrayList(Arrays.asList(new String[]{
                ".html",
                ".json",
                ".js",
                ".css",
                ".csv",
                ".tag",
                ".xml",
                ""
        }));
        ICodeType codeType = TemplateResourceBase.getTypeOfPath(engine, tmplName);
        if (ICodeType.DefImpl.RAW == codeType) {
            // use caller's code type
            codeType = callerClass.codeType;
        }
        final String tagNameOrigin = tmplName;
        if (processTagName) {
            boolean withRythmSuffix = S.notEmpty(rythmSuffix);
            for (String s : suffixes) {
                if (tmplName.endsWith(s)) {
                    tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                    break;
                }
                if (withRythmSuffix) {
                    s = s + rythmSuffix;
                    if (tmplName.endsWith(s)) {
                        tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                        break;
                    }
                }
            }
        }
        tmplName = tmplName.replace('.', '/');
        String sfx = codeType.resourceNameSuffix();
        if (S.notEmpty(sfx) && !suffixes.get(0).equals(sfx)) {
            suffixes.remove(sfx);
            suffixes.add(0, sfx);
        }

        final List<String> roots = new ArrayList<String>();
        final String root0 = getResourceLoaderRoot();
        roots.add(root0);

        // call tag with import path
        if (null != callerClass.importPaths) {
            for (String s: callerClass.importPaths) {
                roots.add(root0 + "/" + s.replace('.', '/'));
            }
        }

        String tmplName0 = tmplName;
        // call template using relative path
        String currentPath = callerClass.getKey().toString();
        int pos = currentPath.lastIndexOf("/");
        if (-1 != pos) {
            currentPath = currentPath.substring(0, pos);
            if (currentPath.startsWith("/")) currentPath = currentPath.substring(1);
            if (!currentPath.startsWith(root0)) currentPath = root0 + "/" + currentPath;
            roots.add(currentPath);
        }
        
        for (String root : roots) {
            tmplName = root + "/" + tmplName0;
            for (String suffix : suffixes) {
                String path = tmplName + suffix;
                ITemplateResource resource = load(path);
                if (null == resource || !resource.isValid()) {
                    continue;
                }
                TemplateClass tc = engine.resourceManager().resourceLoaded(resource, this, false);
//                TemplateClass tc = engine.classes().getByTemplate(resource.getKey(), false);
//                if (null == tc) {
//                    tc = new TemplateClass(resource, engine);
//                } else if (tc.equals(callerClass)) {
//                    // call self
//                    return callerClass;
//                }
//                tc.asTemplate(engine);
                return tc;
            }
        }
        return processTagName ? tryLoadTemplate(tagNameOrigin, engine, callerClass, false) : null;
    }

    @Override
    public void scan(TemplateResourceManager manager) {
        logger.warn("Resource scan not supported by %s", getClass().getName());
    }
}
