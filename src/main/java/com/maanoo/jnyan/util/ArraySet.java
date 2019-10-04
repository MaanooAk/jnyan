
package com.maanoo.jnyan.util;

import java.util.ArrayList;
import java.util.Collection;


public class ArraySet<T> extends ArrayList<T> {

    private static final long serialVersionUID = 9106868193493366394L;

    public ArraySet() {
        super();
    }

    public ArraySet(Collection<? extends T> c) {
        super(c);
    }

    public ArraySet(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(T e) {
        if (contains(e)) return false;
        return super.add(e);
    }

}
