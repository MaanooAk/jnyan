
package com.maanoo.jnyan;

import java.util.List;

import com.maanoo.jnyan.util.C3;


public class C3NyanObject extends C3<NyanObject> {

    @Override
    public List<NyanObject> parents(NyanObject target) {
        return target.parents;
    }

    // ===

    private static final C3NyanObject instance = new C3NyanObject();

    public static List<NyanObject> get(NyanObject target) {
        return instance.linear(target);
    }

}
