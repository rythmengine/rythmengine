package com.greenlaw110.rythm.runtime;

import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
@Deprecated
public class Each {

    public static final Each INSTANCE = new Each();

    public Each() {
        //super();
    }

    public void render(Iterable itr, IBody body) {
        loop(itr, body, -1);
    }

    public void render(Collection col, IBody body) {
        loop(col, body, col.size());
    }

    @SuppressWarnings("unchecked")
    public void render(Object[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(int[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(long[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(float[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(boolean[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(char[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void render(double[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == 1);
            boolean isLast = (i == items.length);
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void loop(Iterable itr, IBody body, int size) {
        Iterator it = itr.iterator();
        int start = 0;
        int i = 0;
        body.ensureCapacity(size);
        while (it.hasNext()) {
            i++;
            Object o = it.next();
            boolean isOdd = i % 2 == 1;
            boolean isFirst = (i == (start + 1));
            boolean isLast = !it.hasNext();
            String sep = isLast ? "" : ","; // default loop item separator
            if (!body.render(o, size, i, isOdd, isOdd ? "odd" : "even", isFirst, isLast, sep, new IBody.LoopUtils(/*i, size, */isFirst, isLast)))
                break;
        }
        //p(body.toString());
    }

    public static interface IBody<E> {
        public static class LoopUtils {
            //private final int id;
            private final boolean isFirst;
            private final boolean isLast;

            //private final int size;
            public LoopUtils(/*int id, int size,*/ boolean isFirst, boolean isLast) {
                //this.id = id;
                this.isFirst = isFirst;
                this.isLast = isLast;
                //this.size = size;
            }

            public String sep(String sep) {
                return postSep(sep);
            }

            public String preSep(String sep) {
                return isFirst ? "" : sep;
            }

            public String postSep(String sep) {
                return isLast ? "" : sep;
            }
        }

        boolean render(final E e, final int size, final int index, final boolean isOdd, final String parity, final boolean isFirst, final boolean isLast, final String sep, final LoopUtils utils);

        void ensureCapacity(int loopCnt);
    }

    public static abstract class Looper<E> implements IBody<E> {
        //        private StringBuilder buffer = null;
//        private int bodySize = 16;
        public Looper(TextBuilder tb) {
            //buffer = tb.buffer();
        }

        public Looper(TextBuilder tb, int bodySize) {
            //buffer = tb.buffer();
            //this.bodySize = bodySize;
        }

        public void ensureCapacity(int loopCnt) {
//            int len = buffer.length();
//            int delta = loopCnt * bodySize;
//            buffer.ensureCapacity(len + delta);
            //System.buffer.println(String.format("capacity: %s + %s * %s", len, bodySize, loopCnt ));
        }

//        protected String toString(Object o) {
//            return null == o ? "" : o.toString();
//        }
//
//        private void p_(Object o) {
//            String s = toString(o);
//            if (!s.isEmpty()) buffer.append(s);
//            //buffer.append(o);
//        }
//
//        /**
//         * Append the object specified to the string buffer
//         *
//         * @param o
//         * @return
//         */
//        public final Looper p(Object o) {
//            if (null != o) buffer.append(o);
//            return this;
//        }
//
//        public final Looper p(String s) {
//            if (null != s && !s.isEmpty()) buffer.append(s);
//            return this;
//        }
//
//        /**
//         * Append to object specified to the string buffer and then append
//         * an new line character
//         *
//         * @param o
//         * @return
//         */
//        protected final Looper pn(Object o) {
//            if (null != o) p_(o);
//            buffer.append('\n');
//            return this;
//        }
//
//        protected final Looper pn(String s) {
//            if (null != s && !s.isEmpty()) buffer.append(s);
//            buffer.append("\n");
//            return this;
//        }
//
//        /**
//         * Append an new line character and then append the object specified
//         * to the string buffer
//         *
//         * @param o
//         * @return
//         */
//        protected final Looper np(Object o) {
//            buffer.append('\n');
//            if (null != o) p_(o);
//            return this;
//        }
//
//        protected final Looper np(String s) {
//            buffer.append("\n");
//            if (null != s && !s.isEmpty()) buffer.append(s);
//            return this;
//        }
//
//        protected final Looper pe(Object o) {
//
//        }

    }
}
