/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

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
