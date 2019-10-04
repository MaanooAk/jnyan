
package com.maanoo.jnyan.util;

public abstract class StringSet {

    public static StringSet create(String... values) {

        return new SmallStringSet(values);
    }

    public static StringSet createDelim(String values, String delim) {
        return create(values.split(delim));
    }

    // ===

    public abstract boolean contains(String value);

    public boolean contains(char c1) {
        return contains(c1 + "");
    }

    // TODO this is ugly
    public boolean contains(char c1, char c2) {
        return contains(c1 + "" + c2);
    }

    // ===

    private static final class SmallStringSet extends StringSet {

        private String[] values;

        public SmallStringSet(String[] values) {
            this.values = values;
        }

        @Override
        public boolean contains(String value) {

            for (final String i : values) {
                if (i.equals(value)) return true;
            }
            return false;
        }
    }

}
