package models;

import org.rythmengine.extension.IPropertyAccessor;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/06/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyPropertyAccessor implements IPropertyAccessor {
    @Override
    public Class getTargetType() {
        return JavaBean.class;
    }

    @Override
    public Object getProperty(String name, Object contextObj) {
        JavaBean jb = (JavaBean)contextObj;
        if ("id".equals(name)) {
            return jb.getId();
        } else if ("count".equals(name)) {
            return jb.getCount();
        } else if ("enabled".equals(name)) {
            return jb.isEnabled();
        } else if ("date".equals(name)) {
            return jb.getDate();
        }
        return jb.get(name);
    }

    @Override
    public Object setProperty(String name, Object contextObj, Object value) {
        ((JavaBean) contextObj).set("name", value);
        return null;
    }
}
