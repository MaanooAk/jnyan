
package com.maanoo.jnyan;

import java.util.ArrayList;
import java.util.HashMap;


public class View {

    public final View parent;
    private final ArrayList<View> children;

    protected final HashMap<String, NyanObject> objects;

    protected View(View parent) {
        this.parent = parent;

        objects = new HashMap<>();
        children = new ArrayList<>();
    }

    public void add(NyanObject o) {
        objects.put(o.name, o);
    }

    public View newView() {
        final View v = new View(this);
        children.add(v);

        v.objects.putAll(objects);

        return v;
    }

    // ===

    public NyanObject get(String name) {

        return objects.get(name);
//        final NyanObject o = objects.get(name);
//        if (o != null) return o;
//
//        return parent.get(name);
    }

    public final NyanObject.Patch getPatch(String name) {
        return (NyanObject.Patch) get(name);
    }

    // ===

    public void patch(String name) {

        final NyanObject.Patch patch = getPatch(name);
        patch.target.patch(patch);

        for (final View i : children) {
            i.patch(name);
        }
    }

}
