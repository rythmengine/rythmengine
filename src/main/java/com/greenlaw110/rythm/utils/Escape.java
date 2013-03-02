package com.greenlaw110.rythm.utils;

import java.util.Arrays;

/**
 * Escape
 */
public enum Escape {
    RAW,
    CSV {
        @Override
        protected RawData apply_(String s) {
            return com.greenlaw110.rythm.utils.S.escapeCsv(s);
        }
    },
    HTML {
        @Override
        protected RawData apply_(String s) {
            return com.greenlaw110.rythm.utils.S.escapeHtml(s);
        }
    },
    JS {
        @Override
        protected RawData apply_(String s) {
            return com.greenlaw110.rythm.utils.S.escapeJavaScript(s);
        }
    },
    JSON {
        @Override
        protected RawData apply_(String s) {
            return com.greenlaw110.rythm.utils.S.escapeJson(s);
        }
    },
    XML {
        @Override
        protected RawData apply_(String s) {
            return com.greenlaw110.rythm.utils.S.escapeXml(s);
        }
    };

    public RawData apply(Object o) {
        if (null == o) return RawData.NULL;
        String s = o.toString();
        return apply_(s);
    }

    protected RawData apply_(String s) {
        return new RawData(s);
    }

    private static String[] sa_ = null;

    public static String[] stringValues() {
        if (null == sa_) {
            Escape[] ea = values();
            String[] sa = new String[ea.length];
            for (int i = 0; i < ea.length; ++i) {
                sa[i] = ea[i].toString();
            }
            Arrays.sort(sa);
            sa_ = sa;
        }
        return sa_.clone();
    }
}
