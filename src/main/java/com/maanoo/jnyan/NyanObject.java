
package com.maanoo.jnyan;

import java.util.ArrayList;
import java.util.HashMap;


public class NyanObject {

    public static final NyanObject RootObject;
    public static final NyanObject RootPatch;
    static {
        RootObject = new NyanObject("Object");
        RootPatch = new NyanObject("Patch");
        RootPatch.parents.add(RootObject);
    }

    // ===

    public final String name;
    public final NyanObject.Patch patchjob;

    private int mod;

    public final ArrayList<NyanObject> parents;
    public final HashMap<String, NyanType> members;
    // public final HashMap<String, NyanOperation> operations;

    public NyanObject(String name) {
        this(name, null);
    }

    public NyanObject(String name, NyanObject.Patch patch) {
        this.name = name;
        this.patchjob = patch;

        parents = new ArrayList<>();
        members = new HashMap<>();
        // operations = new HashMap<>();
    }

    // ==========

    // TODO

    public NyanObject() {
        throw new RuntimeException();
    }

    public boolean is(NyanObject o) {
        throw new RuntimeException();
    }

    public void patch(Patch patch) {
        throw new RuntimeException();
    }

    public static class Patch extends NyanObject {

        public final NyanObject target;

        public Patch() {
            throw new RuntimeException();
        }
    }

}
