package com.greenlaw110.rythm.runtime;

import java.util.Collection;
import java.util.Iterator;

import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.TextBuilder;

@SuppressWarnings("rawtypes") 
public class Each extends TextBuilder {
    public Each(TemplateBase template) {
        super(template);
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
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(int[] items, IBody body) {
        int size = items.length;
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(long[] items, IBody body) {
        int size = items.length;
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(float[] items, IBody body) {
        int size = items.length;
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(boolean[] items, IBody body) {
        int size = items.length;
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(char[] items, IBody body) {
        int size = items.length;
        for (int i = 0; i < size; ++i) {
            boolean isOdd = i % 2 == 1;
            body.render(items[i], size, i, isOdd, isOdd ? "odd" : "even", i == 1, i == items.length);
        }
    }

    @SuppressWarnings("unchecked")
    public void render(double[] items, IBody body) {
        int size = items.length;
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
        while (it.hasNext()) {
            i++;
            Object o = it.next();
            boolean isOdd = i % 2 == 1;
            body.render(o, size, i, isOdd, isOdd ? "odd" : "even", i == start + 1, !it.hasNext());
        }
        //p(body.toString());
    }
    
    public static interface IBody<E>  {
        public abstract void render(final E e, final int size, final int index, final boolean isOdd, final String parity, final boolean isFirst, final boolean isLast);
    }
}
