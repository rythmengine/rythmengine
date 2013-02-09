package com.greenlaw110.rythm.utils;

import java.util.Iterator;

/**
 * Utility class to generate ranges for iteration purpose
 */
public abstract class Range<TYPE extends Comparable<TYPE>> implements Iterable<TYPE> {
    private final TYPE minInclusive;
    private final TYPE maxExclusive;

    public Range(final TYPE minInclusive, final TYPE maxExclusive) {
        if (minInclusive == null || maxExclusive == null) {
            throw new NullPointerException();
        }
        if (minInclusive.compareTo(maxExclusive) > 0) {
            throw new IllegalArgumentException("max is greater than min");
        }
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    protected abstract TYPE next(TYPE element);

    @Override
    public Iterator<TYPE> iterator() {
        return new Iterator<TYPE>() {
            private TYPE cur = minInclusive;

            @Override
            public boolean hasNext() {
                return !cur.equals(maxExclusive);
            }

            @Override
            public TYPE next() {
                return Range.this.next(cur);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
