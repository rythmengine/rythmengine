/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

import org.rythmengine.RythmEngine;
import org.rythmengine.template.ITemplate;
import org.rythmengine.utils.S;

/**
 * Define interface for customized i18n message resolver
 */
public interface II18nMessageResolver {

    /**
     * <p>Return i18n message of a given key and args, use the locale info from the template specified. 
     * if <tt>null</tt> template passed in then it will try to guess from the current engine via
     * {@link org.rythmengine.RythmEngine#get()}</p>
     * 
     * @param template
     * @param key
     * @param args the format arguments. If the first argument is of type Locale then it will be used to specify
     * the locale of the processing, and the rest elements are used as format arguments
     * @return the i18n message
     */
    String getMessage(ITemplate template, String key, Object... args);
    
    public static class DefaultImpl implements II18nMessageResolver {

        public static final II18nMessageResolver INSTANCE = new DefaultImpl();
    
        private DefaultImpl() {}
    
        @Override
        public String getMessage(ITemplate template, String key, Object... args) {
            return S.i18n(template, key, args);
        }
    }
}
