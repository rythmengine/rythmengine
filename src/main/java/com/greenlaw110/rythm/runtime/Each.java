package com.greenlaw110.rythm.runtime;

import java.util.Collection;
import java.util.Iterator;

import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.TextBuilder;

@SuppressWarnings("rawtypes") 
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
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(int[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(long[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(float[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(boolean[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(char[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(double[] items, IBody body) {
        int size = items.length;
        body.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
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
            body.render(o, size, i, isOdd, isOdd ? "odd" : "even", i == start + 1, !it.hasNext());
        }
        //p(body.toString());
    }
    
    public static interface IBody<E>  {
        void render(final E e, final int size, final int index, final boolean isOdd, final String parity, final boolean isFirst, final boolean isLast);
        void ensureCapacity(int loopCnt);
    }
    
    public static abstract class Looper<E> implements IBody<E> {
        private StringBuilder out = null;
        private int bodySize = 16;
        public Looper (TextBuilder tb) {
            out = tb.out();
        }
        public Looper(TextBuilder tb, int bodySize) {
            out = tb.out();
            this.bodySize = bodySize;
        }

        public void ensureCapacity(int loopCnt) {
            int len = out.length();
            int delta = loopCnt * bodySize;
            out.ensureCapacity(len + delta);
            //System.out.println(String.format("capacity: %s + %s * %s", len, bodySize, loopCnt ));
        }

        protected String toString(Object o) {
            return null == o ? "" : o.toString();
        }

        private void p_(Object o) {
            String s = toString(o);
            if (!s.isEmpty()) out.append(s);
            //out.append(o);
        }

        /**
         * Append the object specified to the string buffer
         *
         * @param o
         * @return
         */
        public final Looper p(Object o) {
            if (null != o) out.append(o);
            return this;
        }
        
        public final Looper p(String s) {
            if (null != s && !s.isEmpty()) out.append(s);
            return this;
        }

        /**
         * Append to object specified to the string buffer and then append
         * an new line character
         *
         * @param o
         * @return
         */
        protected final Looper pn(Object o) {
            if (null != o) p_(o);
            out.append('\n');
            return this;
        }
        
        protected final Looper pn(String s) {
            if (null != s && !s.isEmpty()) out.append(s);
            out.append("\n");
            return this;
        }

        /**
         * Append an new line character and then append the object specified
         * to the string buffer
         *
         * @param o
         * @return
         */
        protected final Looper np(Object o) {
            out.append('\n');
            if (null != o) p_(o);
            return this;
        }
        
        protected final Looper np(String s) {
            out.append("\n");
            if (null != s && !s.isEmpty()) out.append(s);
            return this;
        }

    }
}
