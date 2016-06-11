/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

import org.rythmengine.utils.Escape;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Specify a language (e.g. JavaScript) or a format (e.g. csv). The information could be used by
 * Rythm to support {@link org.rythmengine.conf.RythmConfigurationKey#FEATURE_NATURAL_TEMPLATE_ENABLED
 * natural template feature} and
 * {@link org.rythmengine.conf.RythmConfigurationKey#FEATURE_SMART_ESCAPE_ENABLED smart escape feature}
 */
public interface ICodeType {

    /**
     * Return comment start. E.g. for HTML, it should be <code>&lt!--</code>
     *
     * @return comment start
     */
    String commentStart();

    /**
     * Return comment end. For HTML it should be <code>--&gt;</code>
     *
     * @return comment end
     */
    String commentEnd();

    /**
     * Return escape scheme
     *
     * @return escape
     */
    Escape escape();

    /**
     * Some type could be embedded into another. E.g. JS and CSS could be
     * embedded into HTML. This method returns a regex string the direct the start
     * of the embedded type.
     * <p/>
     * <p>Note the regex string must support group and the {@link java.util.regex.Matcher#group(int) group 1}
     * must be the captured block start. For example, JS block start is &lt;script&gt; or
     * &lt;script type="..."...&gt;, then the <code>blockStart</code> method of JS type should be
     * <code>(\&lt;\s*script\s*.*?\&lt;).*</code></p>
     *
     * @return block start
     */
    String blockStart();

    /**
     * Return a regex string indicate an end of a type block
     *
     * @return block end
     * @see #blockStart() for regex requirement
     */
    String blockEnd();

    /**
     * Return true if this file type impl allow another
     * type be embedded inside. e.g. HTML should return true
     * for this method because it allows JS and CSS
     * be embedded inside
     *
     * @return true if this type allows embedded type
     */
    boolean allowInternalTypeBlock();

    /**
     * Return a set of other types that could embed this
     * type impl. For example, JS should return a Set
     * contains an HTML impl. If no other type is allowed
     * to embed this type, then an empty set shall
     * be returned
     *
     * @return true if this type allows external type
     */
    Set<ICodeType> allowedExternalTypes();

    /**
     * Set the parent type to the embedded type
     *
     * @param parent
     */
    void setParent(ICodeType parent);

    /**
     * Return parent type or null if there is no parent
     * set on it
     *
     * @return parent type
     */
    ICodeType getParent();

    /**
     * Return a string that could be write into
     * the target java source code to create an instance
     * of this type
     *
     * @return the java code
     */
    String newInstanceStr();

    /**
     * Return recommended resource name suffix, e.g. ".html" etc
     *
     * @return proposed resource name suffix
     */
    String resourceNameSuffix();


    public static class DefImpl implements ICodeType, Cloneable {

        public static final DefImpl RAW = new DefImpl("RAW", null, null, Escape.RAW, "");

        public static final DefImpl HTML = new DefImpl("HTML", "<!--", "-->", Escape.XML, ".html") {
            @Override
            public boolean allowInternalTypeBlock() {
                // HTML allow CSS and JS inside
                return true;
            }

            @Override
            public String toString() {
                return "HTML";
            }
        };

        public static final DefImpl XML = new DefImpl("XML", "<!--", "-->", Escape.XML, ".xml");

        public static final DefImpl JS = new DefImpl("JS", "/*", "*/", Escape.JS, "(<\\s*script[^<>]*?>).*", "(\\<\\/\\s*script\\s*\\>).*", ".js") {
            @Override
            public Set<ICodeType> allowedExternalTypes() {
                Set<ICodeType> set = new HashSet<ICodeType>();
                set.add(HTML);
                return set;
            }
        };

        public static final DefImpl CSS = new DefImpl("CSS", "/*", "*/", Escape.JS, "(<\\s*style[^<>]*?>).*", "(\\<\\/\\s*style\\s*\\>).*", ".css") {
            @Override
            public Set<ICodeType> allowedExternalTypes() {
                Set<ICodeType> set = new HashSet<ICodeType>();
                set.add(HTML);
                return set;
            }
        };

        public static final DefImpl JSON = new DefImpl("JSON", null, null, Escape.JSON, ".json");
        public static final DefImpl CSV = new DefImpl("CSV", null, null, Escape.CSV, ".csv");

        private final String id;
        private final String commentStart;
        private final String commentEnd;
        private final Escape escape;

        private final String blockStart;
        private final String blockEnd;

        private ICodeType parent;

        private final String suffix;


        protected DefImpl(String id, String commentStart, String commentEnd, Escape escape, String suffix) {
            this(id, commentStart, commentEnd, escape, null, null, suffix);
        }

        protected DefImpl(String id, String commentStart, String commentEnd, Escape escape, String blockStart, String blockEnd, String suffix) {
            this.id = id;
            this.commentEnd = commentEnd;
            this.commentStart = commentStart;
            this.escape = escape;
            this.blockEnd = blockEnd;
            this.blockStart = blockStart;
            this.suffix = suffix;
        }

        @Override
        public String newInstanceStr() {
            StringBuilder sb = new StringBuilder();
            String clsName = ICodeType.class.getName();
            sb.append("(").append(clsName).append(")").append(clsName).append(".DefImpl.").append(this.id).append(".clone()");
            return sb.toString();
        }

        @Override
        public String commentStart() {
            return commentStart;
        }

        @Override
        public String commentEnd() {
            return commentEnd;
        }

        @Override
        public Escape escape() {
            return escape;
        }

        @Override
        public String blockStart() {
            return blockStart;
        }

        @Override
        public String blockEnd() {
            return blockEnd;
        }

        @Override
        public boolean allowInternalTypeBlock() {
            return false;
        }

        @Override
        public void setParent(ICodeType parent) {
            this.parent = parent;
        }

        @Override
        public ICodeType getParent() {
            return parent;
        }

        @Override
        public Set<ICodeType> allowedExternalTypes() {
            return Collections.emptySet();
        }

        @Override
        public String resourceNameSuffix() {
            return suffix;
        }

        @Override
        public String toString() {
            return newInstanceStr();
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
