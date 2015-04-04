/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.rythmengine.resource;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;

import java.util.UUID;

/**
 * Encapsulate the common logic of an {@link ITemplateResource} implementation
 */
public abstract class TemplateResourceBase implements ITemplateResource {

    protected final static ILogger logger = Logger.get(TemplateResourceBase.class);

    /**
     * When this field is set to null, it assumes using Rythm's singleton mode
     * <p/>
     * This field should be set to null if needs to serialize the template resource to some where, e.g. Cache
     */
    protected final ITemplateResourceLoader loader;
    
    protected boolean isProdMode = false;

    /**
     * There are certain cases that a type of resource does not need a specific loader. E.g the 
     * {@link ToStringTemplateResource}
     */
    public TemplateResourceBase() {
        loader = null;
    }

    public TemplateResourceBase(ITemplateResourceLoader loader) {
        if (null == loader) {
            throw new NullPointerException();
        }
        this.loader = loader;
        this.isProdMode = getEngine().isProdMode();
    }

    protected RythmEngine getEngine() {
        return loader.getEngine();
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    protected String cache;

    private long timestamp;

    protected long nextCheckPoint;

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (null == obj) return false;
        if (getClass().equals(obj.getClass())) {
            return ((TemplateResourceBase) obj).getKey().equals(getKey());
        }
        return false;
    }

    private long checkInterval() {
        if (isProdMode) return -1; // never check when running in product mode
        Long intv = userCheckInterval();
        return null == intv ? defCheckInterval() : intv;
    }

    protected abstract long defCheckInterval();

    protected Long userCheckInterval() {
        return getEngine().conf().resourceRefreshInterval();
    }

    protected abstract long lastModified();

    protected abstract String reload();

    @Override
    public String asTemplateContent() {
        if (null == cache) {
            cache = reload();
            timestamp = lastModified();
        }
        return cache;
    }

    @Override
    /**
     * Refresh the product is modified.
     *
     * @return true if the product has been modified
     */
    public final boolean refresh() {
        long checkInterval = checkInterval();
        if (checkInterval < 0) return false; // never check
        if (0 == checkInterval) {
            // always check
            checkModified();
            return isModified();
        }
        // should I check now ?
        long now = System.currentTimeMillis();
        if (nextCheckPoint < now) {
            checkModified();
            nextCheckPoint = now + checkInterval;
        }
        return isModified();
    }

    private void checkModified() {
        long modified = lastModified();
        if (timestamp < modified) {
            cache = null;
        }
    }

    private boolean isModified() {
        return null == cache;
    }

    @Override
    public String getSuggestedClassName() {
        return "C" + UUID.randomUUID().toString().replace('-', '_');
    }
    
    public static ICodeType getTypeOfPath(RythmEngine engine, String s) {
        String suffix = engine.conf().resourceNameSuffix();
        if (s.endsWith(suffix)) {
            int pos = s.lastIndexOf(suffix);
            if (pos > -1) s = s.substring(0, pos);
        }
        ICodeType codeType = engine.conf().defaultCodeType();
        if (s.endsWith(".html") || s.endsWith(".htm")) {
            codeType = ICodeType.DefImpl.HTML;
        } else if (s.endsWith(".js")) {
            codeType = ICodeType.DefImpl.JS;
        } else if (s.endsWith(".json")) {    
            codeType = ICodeType.DefImpl.JSON;
        } else if (s.endsWith(".xml")) {
            codeType = ICodeType.DefImpl.XML;
        } else if (s.endsWith(".csv")) {    
            codeType = ICodeType.DefImpl.CSV;
        } else if (s.endsWith(".css")) {
            codeType = ICodeType.DefImpl.CSS;
        }
        return codeType;
    }

    @Override
    public ICodeType codeType(RythmEngine engine) {
        Object key = getKey();
        String s = S.str(key);
        return getTypeOfPath(engine, s);
    }

    @Override
    public ITemplateResourceLoader getLoader() {
        return loader;
    }

    protected static String path2CN(String path) {
        int colon = path.indexOf(":");
        if (++colon > 0) {
            path = path.substring(colon); // strip the driver letter from windows path and scheme from the URL
        }
        while (path.startsWith("/")) path = path.substring(1);
        while (path.startsWith("\\")) path = path.substring(1);
        // -- do not strip the file suffix. other wise a.html and a.js template will fetch a same template class
        //    in the end
        //int lastDotPos = path.lastIndexOf(".");
        //path = path.substring(0, lastDotPos);

        // replace characters that are invalid in a java identifier with '_'
        return path.replaceAll("[.\\\\/ -]", "_").replace('~', '_');
    }
}
