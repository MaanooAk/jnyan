
package com.maanoo.jnyan.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class C3<T> {

    public abstract List<T> parents(T target);

    public List<T> linear(T target) {
        final ArrayList<T> l = new ArrayList<>();
        l.add(target);

        final List<T> parents = parents(target);

        if (parents.size() == 0) {
            // nothing

        } else if (parents.size() == 1) {
            l.add(parents.get(0));

        } else {

            final ArrayList<ArrayDeque<T>> subs = collectSubs(parents);

            while (subs.size() > 0) {
                final T selected = selectValidCanidate(subs);

//                System.out.println(subs);
//                System.out.println(selected);

                l.add(selected);
                reduceSubs(subs, selected);
            }

        }

        return l;
    }

    private ArrayList<ArrayDeque<T>> collectSubs(final List<T> parents) {

        final ArrayList<ArrayDeque<T>> subs = new ArrayList<>(parents.size() + 1);

        for (final T i : parents) {
            final List<T> linear = linear(i);
            if (linear.isEmpty()) continue;

            subs.add(new ArrayDeque<T>(linear));
        }

        subs.add(new ArrayDeque<T>(parents));

        return subs;
    }

    private T selectValidCanidate(final ArrayList<ArrayDeque<T>> subs) {

        for (final ArrayDeque<T> sub : subs) {
            for (final T canidate : sub) {

                if (validCanidate(canidate, subs)) {
                    return canidate;
                }

            }
        }

        throw new RuntimeException("Cannot linear");
    }

    private boolean validCanidate(T canidate, ArrayList<ArrayDeque<T>> subs) {

        for (final ArrayDeque<T> sub : subs) {

            final boolean first = sub.getFirst().equals(canidate);
            final boolean inside = sub.contains(canidate);

            if (!first && inside) return false;
        }

        return true;
    }

    private void reduceSubs(ArrayList<ArrayDeque<T>> subs, T selected) {

        for (final Iterator<ArrayDeque<T>> iter = subs.iterator(); iter.hasNext();) {
            final ArrayDeque<T> sub = iter.next();

            if (sub.getFirst().equals(selected)) {
                sub.removeFirst();

                if (sub.isEmpty()) {
                    iter.remove();
                }
            }
        }

    }

}
