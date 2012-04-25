package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 25/04/12
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateBuilder extends TextBuilder {

    // --- print expression interface
    public final TextBuilder pe(char c) {
        return p(c);
    }

    public final TextBuilder pe(int i) {
        return p(i);
    }

    public final TextBuilder pe(long l) {
        return p(l);
    }

    public final TextBuilder pe(float f) {
        return p(f);
    }

    public final TextBuilder pe(double d) {
        return p(d);
    }

    public final TextBuilder pe(boolean b) {
        return p(b);
    }

    public TemplateBuilder pe(Object o, ITemplate.Escape escape) {
        if (null != o) {
            if (o instanceof ITemplate.RawData) {
                return (TemplateBuilder)p(o);
            }
            if (null == escape) escape = ITemplate.Escape.HTML;
            switch (escape) {
                case HTML:
                    return (TemplateBuilder)p(S.escapeHtml(o));
                case JSON:
                    return (TemplateBuilder)p(S.escapeJson(o));
                case JS:
                    return (TemplateBuilder)p(S.escapeJavaScript(o));
                case JAVA:
                    return (TemplateBuilder)p(S.escapeJava(o));
                case CSV:
                    return (TemplateBuilder)p(S.escapeCsv(o));
                case XML:
                    return (TemplateBuilder)p(S.escapeXml(o));
            }
        }
        return this;
    }

    public final TextBuilder pe(char c, ITemplate.Escape escape) {
        return p(c);
    }

    public final TextBuilder pe(int i, ITemplate.Escape escape) {
        return p(i);
    }

    public final TextBuilder pe(long l, ITemplate.Escape escape) {
        return p(l);
    }

    public final TextBuilder pe(float f, ITemplate.Escape escape) {
        return p(f);
    }

    public final TextBuilder pe(double d, ITemplate.Escape escape) {
        return p(d);
    }

    public final TextBuilder pe(boolean b, ITemplate.Escape escape) {
        return p(b);
    }

}
