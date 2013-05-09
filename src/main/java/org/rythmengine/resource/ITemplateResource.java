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
     * Return non-null value if this resource present a tag
     *
     * @return tag name
     */
    String tagName();

    /**
     * Return code type suggested by this resource, e.g. html or js etc 
     * 
     * @return code type suggested
     */
    ICodeType codeType();

    /**
     * Set engine instance to the resource
     * @param engine
     */
    void setEngine(RythmEngine engine);

}
