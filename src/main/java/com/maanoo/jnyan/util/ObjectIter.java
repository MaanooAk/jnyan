
package com.maanoo.jnyan.util;

import java.util.Iterator;
import java.util.List;


public class ObjectIter<T> implements Iterator<T> {

    private final List<T> objects;
    private int index;

    public ObjectIter(List<T> tokens) {
        this.objects = tokens;
        index = 0;
    }

    public List<T> source() {
        return objects;
    }

    public int passed() {
        return index;
    }

    public T first() {
        return objects.get(0);
    }

    // ===

    public boolean has() {
        return index < objects.size();
    }

    public boolean has(int count) {
        assert count >= 0;
        return index + count < objects.size();
    }

    public T peek() {
        return objects.get(index);
    }

    public T peek(int skip) {
        return objects.get(index + skip);
    }

    public void skip() {
        index += 1;
    }

    public void skip(int count) {
        assert count != 0;
        index += count;
    }

    @Override
    public T next() {
        return objects.get(index++);
    }

    // Iterator implementation

    @Override
    @Deprecated
    public boolean hasNext() {
        return has();
    }

}
