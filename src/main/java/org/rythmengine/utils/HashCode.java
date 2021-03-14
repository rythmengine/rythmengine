/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.utils;

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

import java.lang.reflect.Array;

/**
 * Utility help generating hashcode
 */
public class HashCode {
    private static final int HC_INIT = 17;
    private static final int HC_FACT = 37;

    private HashCode() {
    }

    public final static int iterableHashCode(Iterable<?> it) {
        int ret = HC_INIT;
        for (Object o : it) {
            ret = ret * HC_FACT + hc(o);
        }
        return ret;
    }

    public final static int hc(boolean o) {
        return o ? 1231 : 1237;
    }

    public final static int hc(boolean[] oa) {
        int ret = HC_INIT;
        for (boolean b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(short o) {
        return (int)o;
    }

    public final static int hc(short[] oa) {
        int ret = HC_INIT;
        for (short b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(byte o) {
        return (int)o;
    }

    public final static int hc(byte[] oa) {
        int ret = HC_INIT;
        for (byte b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(char o) {
        return (int)o;
    }

    public final static int hc(char[] oa) {
        int ret = HC_INIT;
        for (char b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(int o) {
        return o;
    }

    public final static int hc(int[] oa) {
        int ret = HC_INIT;
        for (int b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(float o) {
        return Float.floatToIntBits(o);
    }

    public final static int hc(float[] oa) {
        int ret = HC_INIT;
        for (float b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(long o) {
        return  (int) (o ^ (o >> 32));
    }

    public final static int hc(long[] oa) {
        int ret = HC_INIT;
        for (long b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(double o) {
        return hc(Double.doubleToLongBits(o));
    }

    public final static int hc(double[] oa) {
        int ret = HC_INIT;
        for (double b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public final static int hc(Object o) {
        return hc_(o);
    }

    public static final int hc(Object o1, Object o2) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        return i;
    }

    public static final int hc(Object o1, Object o2, Object o3) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        return i;
    }

    public static final int hc(Object o1, Object o2, Object o3, Object o4) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        i = 31 * i + hc_(o4);
        return i;
    }

    public static final int hc(Object o1, Object o2, Object o3, Object o4, Object o5) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        i = 31 * i + hc_(o4);
        i = 31 * i + hc_(o5);
        return i;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param args
     * @return the calculated hash code
     */
    public final static int hc(Object o1, Object o2, Object o3, Object o4, Object o5, Object... args) {
        int i = hc(o1, o2, o3, o4, o5);
        for (Object o : args) {
            i = 31 * i + hc(o);
        }
        return i;
    }

    private static int hc_(Object o) {
        if (null == o) {
            return HC_INIT * HC_FACT;
        }
        if (o.getClass().isArray()) {
            if (o instanceof int[]) {
                return hc((int[]) o);
            } else if (o instanceof long[]) {
                return hc((long[]) o);
            } else if (o instanceof char[]) {
                return hc((char[]) o);
            } else if (o instanceof byte[]) {
                return hc((byte[]) o);
            } else if (o instanceof double[]) {
                return hc((double[]) o);
            } else if (o instanceof float[]) {
                return hc((float[]) o);
            } else if (o instanceof short[]) {
                return hc((short[]) o);
            } else if (o instanceof boolean[]) {
                return hc((boolean[]) o);
            }
            int len = Array.getLength(o);
            int hc = 17;
            for (int i = 0; i < len; ++i) {
                hc = 31 * hc + hc_(Array.get(o, i));
            }
            return hc;
        } else {
            return o.hashCode();
        }
    }
}
