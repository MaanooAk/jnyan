
package com.maanoo.jnyan;

import java.util.Arrays;


public abstract class NyanType {

    // public static final NyanType RootObject = new
    // NyanType.Reference(NyanObject.RootObject);
    // public static final NyanType RootPatch = new
    // NyanType.Reference(NyanObject.RootPatch);

    // ===

    public enum Type {

        // primitive
        Int, Float, Bool, Text, File,

        // collections
        Set(true), OrderedSet(true), List(true), Dict(true),

        Object

        ;

        public final boolean multi;

        private Type() {
            this(false);
        }

        private Type(boolean multi) {
            this.multi = multi;

        }

    }

    // ===

    public final Type type;

    public NyanType(Type type) {
        this.type = type;
    }

    @Override
    public abstract String toString();

    // ===

    public static final class Simple extends NyanType {

        public Simple(Type type) {
            super(type);
        }

        @Override
        public String toString() {
            return type.name();
        }
    }

    public static final class Paramed extends NyanType {

        public final NyanType[] params;

        public Paramed(Type type, NyanType[] params) {
            super(type);
            this.params = params;
        }

        @Override
        public String toString() {
            // TODO improve
            return type.name() + Arrays.toString(params);
        }

    }

    public static final class Reference extends NyanType {

        public final NyanObject name;

        public Reference(NyanObject name) {
            super(Type.Object);
            this.name = name;
        }

        @Override
        public String toString() {
            // TODO improve
            return name.toString();
        }

    }

}
