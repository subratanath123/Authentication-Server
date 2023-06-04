package net.auzy.filter;


import java.util.Enumeration;
import java.util.NoSuchElementException;

public class SingleValueEnumeration<T> implements Enumeration<T> {

    private final T value;
    private boolean hasMore = true;

    public SingleValueEnumeration(T value) {
        this.value = value;
    }

    @Override
    public boolean hasMoreElements() {
        return hasMore;
    }

    @Override
    public T nextElement() {
        if (!hasMore) {
            throw new NoSuchElementException();
        }
        hasMore = false;
        return value;
    }
}

