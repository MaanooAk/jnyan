
package com.maanoo.jnyan.util;

import java.util.Iterator;
import java.util.List;


public class ObjectIter<T> implements Iterator<T> {

    private final List<T> tokens;
    private int index;

    public ObjectIter(List<T> tokens) {
        this.tokens = tokens;
        index = 0;
    }

    public boolean has() {
        return index < tokens.size();
    }

    public boolean has(int count) {
        assert count >= 0;
        return index + count < tokens.size();
    }

    public T peek() {
        return tokens.get(index);
    }

    public T peek(int skip) {
        return tokens.get(index + skip);
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
        return tokens.get(index++);
    }

    // Iterator implementation

    @Override
    @Deprecated
    public boolean hasNext() {
        return has();
    }

}
