
package com.maanoo.jnyan;

public class Database extends View {

    public Database() {
        super(null);

        add(NyanObject.RootObject);
        add(NyanObject.RootPatch);
    }

    @Override
    public NyanObject get(String name) {
        return objects.get(name);
    }
}
