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
    
    private RythmEngine engine;
    
    public RythmEngine getEngine() {
        return this.engine;
    }

    public void setEngine(RythmEngine engine) {
        this.engine = engine;
    }

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
        key += tc.getCodeType().resourceNameSuffix();
        return key;
    }

    protected RythmEngine getDefaultEngine() {
        return Rythm.engine();
    }

    @Override
    public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerClass, ICodeType codeType) {
        return tryLoadTemplate(tmplName, engine, callerClass, codeType, true);
    }
    
    /**
     * try loading the given template
     * @param tmplName
     * @param engine
     * @param callerClass
     * @param codeType
     * @param processTagName
     * @return
     */
    private TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerClass, ICodeType codeType, boolean processTagName) {
        //logger.info(">>> try load %s on [%s] with processTagName: %s", tmplName, callerClass.getKey(), processTagName);
        if (null == engine) {
            engine = getDefaultEngine();
        }
        if (engine.templateRegistered(tmplName)) {
            return null;
        }
        String rythmSuffix = engine.conf().resourceNameSuffix();
        final List<String> suffixes = new ArrayList<String>(Arrays.asList(RythmEngine.VALID_SUFFIXES));
        if (null == codeType) {
            codeType = TemplateResourceBase.getTypeOfPath(engine, tmplName);
        }
        if (ICodeType.DefImpl.RAW == codeType) {
            // use caller's code type
            codeType = callerClass.getCodeType();
        }
        final String tagNameOrigin = tmplName;
        boolean hasSuffix = false;
        String suffix = "";
        if (processTagName) {
            boolean withRythmSuffix = S.notEmpty(rythmSuffix);
            for (String s : suffixes) {
                if (tmplName.endsWith(s)) {
                    tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                    suffix = s;
                    hasSuffix = true;
                    break;
                }
                if (withRythmSuffix && (tmplName.endsWith(s) || tmplName.endsWith(s + rythmSuffix))) {
                    tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                    suffix = s + rythmSuffix;
                    hasSuffix = true;
                    break;
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
        String root0 = this.getResourceLoaderRoot().replace('\\', '/');
        if (root0.endsWith("/")) {
            root0 = root0.substring(0, root0.length() - 1);
        }

        roots.add(root0);

        // call template using relative path
        String currentPath = callerClass.getKey().toString();
        int pos = currentPath.lastIndexOf("/");
        if (-1 != pos) {
            currentPath = currentPath.substring(0, pos);
            if (currentPath.startsWith(root0)) {
                if (currentPath.length() > root0.length()) {
                    roots.add(0, currentPath);
                }
            } else {
                if (currentPath.startsWith("/")) {
                    currentPath = currentPath.substring(1);
                }
                if (!currentPath.startsWith(root0)) currentPath = root0 + "/" + currentPath;
                roots.add(0, currentPath);
            }
        }
        
        // call tag with import path
        if (null != callerClass.getImportPaths()) {
            for (String s: callerClass.getImportPaths()) {
                if (s.startsWith("java")) {
                    continue;
                }
                roots.add(0, root0 + "/" + s.replace('.', '/'));
            }
        }

        String tmplName0 = tmplName;
        for (String root : roots) {
            String tmplName1 = tmplName0;
            if (root.startsWith("/") && !tmplName1.startsWith("/")) {
                tmplName1 = "/" + tmplName0;
            }
            tmplName = tmplName1.startsWith(root) ? tmplName1 : root + "/" + tmplName0;
            if (hasSuffix) {
                ITemplateResource resource = load(tmplName + suffix);
                if (null == resource || !resource.isValid()) {
                    continue;
                }
                TemplateClass tc = engine.resourceManager().resourceLoaded(resource, false);
                return tc;
            } else {
                for (String suffix0 : suffixes) {
                    String path = tmplName + suffix0;
                    ITemplateResource resource = load(path);
                    if (null == resource || !resource.isValid()) {
                        continue;
                    }
                    TemplateClass tc = engine.resourceManager().resourceLoaded(resource, false);
                    return tc;
                }
            }
        }
        TemplateClass tc = processTagName ? tryLoadTemplate(tagNameOrigin, engine, callerClass, codeType, false) : null;
        if (null == tc) {
            TemplateResourceManager.reportNonResource(tmplName);
        }
        return tc;
    }

    @Override
    public void scan(TemplateResourceManager manager) {
        logger.warn("Resource scan not supported by %s", getClass().getName());
    }
}
