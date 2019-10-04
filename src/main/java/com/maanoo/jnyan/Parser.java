
package com.maanoo.jnyan;

import com.maanoo.jnyan.util.StringIter;
import com.maanoo.jnyan.util.StringSet;


public class Parser {

    private StringSet delims = StringSet.createDelim(": = ( ) < > [ ] { } o{ , @ + += -= *= /= &= |= # \n", " ");
    private StringSet whitespace = StringSet.create(" ", "\t");
    private StringSet newline = StringSet.create(";\n", ";", "\n");
    private StringSet stringend = StringSet.create("\"", "\\");

    private final class Iter extends StringIter {

        public Iter(String text) {
            super(text);
        }

    }

}
