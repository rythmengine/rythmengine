/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ITemplateResourceLoader;

import java.io.Serializable;

/**
 * Implement a template resource which can be load from some where. For example, a file in a
 * file system or an input stream from the network, or even from the database
 */
public interface ITemplateResource extends Serializable {

    /**
     * The unique identifier used to fetch this resource by a 
     * {@link org.rythmengine.extension.ITemplateResourceLoader resource loader}
     *
     * @return the key of the resource
     */
    Object getKey();

    /**
     * Propose a name of generated java class for this resource
     *
     * @return suggested class name
     */
    String getSuggestedClassName();

    /**
     * Return template content as a string. Call refresh() first to check
     * if the resource has been modified
     *
     * @return resource content
     */
    String asTemplateContent();

    /**
     * Refresh resource if necessary
     *
     * @return true if resource is modified false otherwise
     */
    boolean refresh();

    /**
     * Whether this resource is a valid resource
     *
     * @return true if it's a valid resource
     */
    boolean isValid();

    /**
     * Return code type suggested by this resource, e.g. html or js etc 
     * 
     * @return code type suggested
     */
    ICodeType codeType(RythmEngine engine);

    /**
     * Return the loader that loaded this resource
     * @return the template resource loader that loaded this resource
     */
    ITemplateResourceLoader getLoader();
    
    /**
     * @return the error if there was one
     */
    public Throwable getError();

    /**
     * @param error the error to set
     */
    public void setError(Throwable error);

}
