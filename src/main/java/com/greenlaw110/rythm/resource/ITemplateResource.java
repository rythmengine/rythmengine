package com.greenlaw110.rythm.resource;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITemplateResource extends Serializable {

    /**
     * The unique identifier used to fetch this resource from ResourceManager
     * @return
     */
    String getKey();

    /**
     * Propose a name of generated java class for this resource
     * @return
     */
    String getSuggestedClassName();

    /**
     * Return template content as a string. Call refresh() first to check
     * if the resource has been modified
     *
     * @return
     */
    String asTemplateContent();

    /**
     * Refresh resource if necessary
     * @return true if resource is modified false otherwise
     */
    boolean refresh();

    /**
     * Whether this resource is a valid resource
     * @return true if it's a valid resource
     */
    boolean isValid();
}
