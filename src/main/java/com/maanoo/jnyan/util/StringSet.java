
package com.maanoo.jnyan.util;

public class StringSet {

    public static StringSet create(String... values) {

        return new SmallStringSet(values);
    }

    public static StringSet createDelim(String values, String delim) {
        return create(values.split(delim));
    }

    private static final class SmallStringSet extends StringSet {

        private String[] values;

        public SmallStringSet(String[] values) {
            this.values = values;
        }

    }

}
