
package com.maanoo.jnyan.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;


public class ArraySet<T> extends AbstractList<T> {

    private final ArrayList<T> list;

    public ArraySet() {
        list = new ArrayList<>();
    }

    public ArraySet(Collection<? extends T> c) {
        list = new ArrayList<>(c);
    }

    @Override
    public boolean add(T e) {
        if (contains(e)) return false;
        return list.add(e);
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void clear() {
        list.clear();
    }

}
