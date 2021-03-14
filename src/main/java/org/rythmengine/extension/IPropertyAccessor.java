/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * The interface can be implemented by application that use Rythm 
 * with {@link org.rythmengine.conf.RythmConfigurationKey#FEATURE_DYNAMIC_EXP dynamic expression} 
 * option enabled.
 * 
 * <p>The implementation should be registered via {@link org.rythmengine.RythmEngine#registerPropertyAccessor(IPropertyAccessor...)} API or via {@link org.rythmengine.conf.RythmConfigurationKey#EXT_PROP_ACCESSOR} configuration</p>
 */
public interface IPropertyAccessor {

    /**
     * Which type this property accessor can be applied
     * @return the class 
     */
    @SuppressWarnings("rawtypes")
    public Class getTargetType();
    
    /**
     * Retrieves the value of the property.
     *
     * @param name            - the name of the property to be resolved.
     * @param contextObj      - the current context object.
     * @return - the value of the property.
     */
    public Object getProperty(String name, Object contextObj);
  
  
    /**
     * Sets the value of the property.
     *
     * @param name            - the name of the property to be resolved.
     * @param contextObj      - the current context object.
     * @param value           - the value to be set to the resolved property
     * @return - the resultant value of the property (should normally be the same as the value passed)
     */
    public Object setProperty(String name, Object contextObj, Object value);
}
