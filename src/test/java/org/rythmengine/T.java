package org.rythmengine;

import org.rythmengine.template.TemplateBase;

public class T extends org.rythmengine.template.TagBase {

    @Override
    public java.lang.String __getName() {
        return "tags_toolbar_html__R_T_C__";
    }

    @Override
    protected void __setup() {
        if (buttons == null) {
            buttons = (String) __get("buttons");
        }
        if (returnButton == null) {
            returnButton = (Boolean) __get("returnButton");
        }
        if (viewButton == null) {
            viewButton = (Boolean) __get("viewButton");
        }
        if (id == null) {
            id = (String) __get("id");
        }
        if (name == null) {
            name = (String) __get("name");
        }
        if (onclick == null) {
            onclick = (String) __get("onclick");
        }
        if (title == null) {
            title = (String) __get("title");
        }
        if (icon == null) {
            icon = (String) __get("icon");
        }
        if (useSearch == null) {
            useSearch = (Boolean) __get("useSearch");
        }
        if (imgName == null) {
            imgName = (String) __get("imgName");
        }
        if (titles == null) {
            titles = (String) __get("titles");
        }
    }

    protected String buttons = ""; //line: 1
    protected Boolean returnButton = true; //line: 2
    protected Boolean viewButton = true; //line: 2
    protected String id = ""; //line: 3
    protected String name = ""; //line: 3
    protected String onclick = ""; //line: 3
    protected String title = ""; //line: 4
    protected String icon = ""; //line: 4
    protected Boolean useSearch = false; //line: 6
    protected String imgName = "uc.png"; //line: 37
    protected String titles = ""; //line: 37

    protected java.lang.String __renderArgName(int __pos) {
        int __p = 0;
        if (__p++ == __pos) return "buttons";
        else if (__p++ == __pos) return "returnButton";
        else if (__p++ == __pos) return "viewButton";
        else if (__p++ == __pos) return "id";
        else if (__p++ == __pos) return "name";
        else if (__p++ == __pos) return "onclick";
        else if (__p++ == __pos) return "title";
        else if (__p++ == __pos) return "icon";
        else if (__p++ == __pos) return "useSearch";
        else if (__p++ == __pos) return "imgName";
        else if (__p++ == __pos) return "titles";
        throw new ArrayIndexOutOfBoundsException();
    }

    protected java.util.Map<java.lang.String, java.lang.Class> __renderArgTypeMap() {
        java.util.Map<java.lang.String, java.lang.Class> __m = new java.util.HashMap<String, Class>();
        __m.put("buttons", String.class);
        __m.put("returnButton", Boolean.class);
        __m.put("viewButton", Boolean.class);
        __m.put("id", String.class);
        __m.put("name", String.class);
        __m.put("onclick", String.class);
        __m.put("title", String.class);
        __m.put("icon", String.class);
        __m.put("useSearch", Boolean.class);
        __m.put("imgName", String.class);
        __m.put("titles", String.class);
        return __m;
    }

    @SuppressWarnings("unchecked")
    public TemplateBase __setRenderArgs(java.util.Map<java.lang.String, java.lang.Object> __args) {
        if (null == __args) throw new NullPointerException();
        if (__args.isEmpty()) return this;
        super.__setRenderArgs(__args);
        if (__args.containsKey("buttons")) this.buttons = (String) __args.get("buttons");
        if (__args.containsKey("returnButton")) this.returnButton = (Boolean) __args.get("returnButton");
        if (__args.containsKey("viewButton")) this.viewButton = (Boolean) __args.get("viewButton");
        if (__args.containsKey("id")) this.id = (String) __args.get("id");
        if (__args.containsKey("name")) this.name = (String) __args.get("name");
        if (__args.containsKey("onclick")) this.onclick = (String) __args.get("onclick");
        if (__args.containsKey("title")) this.title = (String) __args.get("title");
        if (__args.containsKey("icon")) this.icon = (String) __args.get("icon");
        if (__args.containsKey("useSearch")) this.useSearch = (Boolean) __args.get("useSearch");
        if (__args.containsKey("imgName")) this.imgName = (String) __args.get("imgName");
        if (__args.containsKey("titles")) this.titles = (String) __args.get("titles");
        return this;
    }

    @SuppressWarnings("unchecked")
    public TemplateBase __setRenderArgs(java.lang.Object... __args) {
        int __p = 0, __l = __args.length;
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            buttons = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            returnButton = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            viewButton = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            id = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            name = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            onclick = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            title = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            icon = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            useSearch = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            imgName = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (__p < __l) {
            Object v = __args[__p++];
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            titles = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        return this;
    }

    protected java.lang.Class[] __renderArgTypeArray() {
        return new java.lang.Class[]{String.class, Boolean.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class,};
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateBase __setRenderArg(java.lang.String __name, java.lang.Object __arg) {
        if ("buttons".equals(__name)) this.buttons = (String) __arg;
        else if ("returnButton".equals(__name)) this.returnButton = (Boolean) __arg;
        else if ("viewButton".equals(__name)) this.viewButton = (Boolean) __arg;
        else if ("id".equals(__name)) this.id = (String) __arg;
        else if ("name".equals(__name)) this.name = (String) __arg;
        else if ("onclick".equals(__name)) this.onclick = (String) __arg;
        else if ("title".equals(__name)) this.title = (String) __arg;
        else if ("icon".equals(__name)) this.icon = (String) __arg;
        else if ("useSearch".equals(__name)) this.useSearch = (Boolean) __arg;
        else if ("imgName".equals(__name)) this.imgName = (String) __arg;
        else if ("titles".equals(__name)) this.titles = (String) __arg;
        super.__setRenderArg(__name, __arg);
        return this;
    }

    @SuppressWarnings("unchecked")
    public TemplateBase __setRenderArg(int __pos, java.lang.Object __arg) {
        int __p = 0;
        if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            buttons = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            returnButton = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            viewButton = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            id = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            name = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            onclick = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            title = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            icon = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean"));
            useSearch = (Boolean) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            imgName = (String) (isString ? (null == v ? "" : v.toString()) : v);
        } else if (__p++ == __pos) {
            Object v = __arg;
            boolean isString = ("java.lang.String".equals("String") || "String".equals("String"));
            titles = (String) (isString ? (null == v ? "" : v.toString()) : v);
        }
        if (0 == __pos) __setRenderArg("arg", __arg);
        return this;
    }


    @Override
    public org.rythmengine.utils.TextBuilder build() {
            return this;
        }

    private void foo() {

    }

}
