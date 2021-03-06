
package com.maanoo.jnyan.builders;

public abstract class Builder<T> {

    protected final String namespace;

    public Builder(String namespace) {
        this.namespace = namespace;
    }

    protected String nm(String name) {
        return nm(name, namespace);
    }

    public static String nm(String name, String namespace) {
        if (name.equals("Object") || name.equals("Patch")) return name;
        return name.indexOf('.') == -1 ? namespace + "." + name : name;
    }

}
