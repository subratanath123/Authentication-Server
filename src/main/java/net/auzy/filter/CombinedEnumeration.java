package net.auzy.filter;

import java.util.Enumeration;
import java.util.Set;

public class CombinedEnumeration<T> implements Enumeration<T> {

    private final Enumeration<T> first;
    private final Set<T> second;

    public CombinedEnumeration(Enumeration<T> first, Set<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasMoreElements() {
        return first.hasMoreElements() || second.iterator().hasNext();
    }

    @Override
    public T nextElement() {
        if (first.hasMoreElements()) {
            return first.nextElement();
        }
        return second.iterator().next();
    }
}
