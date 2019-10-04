
package com.maanoo.jnyan;

import java.util.ArrayList;

import com.maanoo.jnyan.util.StringIter;
import com.maanoo.jnyan.util.StringSet;


public class Parser {

    // TODO make string set on costructor

    private StringSet delims = StringSet.createDelim(": = ( ) < > [ ] { } o{ , @ + += -= *= /= &= |= # \n", " ");
    private StringSet whitespace = StringSet.create(" ", "\t");
    private StringSet newline = StringSet.create(";\n", ";", "\n");
    private StringSet stringend = StringSet.create("\"", "\\");
    private StringSet nothing = StringSet.create();

    private final class Iter extends StringIter {

        // TODO move elsewhere

        public Iter(String text) {
            super(text);
        }

        public String collectUntill(StringSet delims, StringSet without) {
            final int start = index;
            while (has(2) && !contains(delims, peek(), peek(1)) && match(without, peek()) == null) { // TODO fix the 2
                skip();
            }
            return text.substring(start, index);
        }

        public String collectUntillLast(StringSet delims) {
            final int start = index;
            while (has(3) && contains(delims, peek(1), peek(2))) { // TODO fix the 2
                skip();
            }
            return text.substring(start, index + 1);
        }

        private boolean contains(StringSet delims, char c1, char c2) {

            return delims.contains(c1) || delims.contains(c1 + "" + c2);
        }

        private String match(StringSet delims, char c1) {

            if (delims.contains(c1)) return c1 + "";

            return null;
        }

        private String match(StringSet delims, char c1, char c2) {

            if (delims.contains(c1, c2)) return c1 + "" + c2;
            if (delims.contains(c1)) return c1 + "";

            return null;
        }
    }

    public ArrayList<Token> parse(String text) {
        final ArrayList<Token> l = new ArrayList<>();
        l.add(new Token(Token.Type.Newline));

        final Iter iter = new Iter(text);

        while (iter.has()) {
            final char c = iter.peek();

            // ===

            if (c == '#') { // single line comments
                final String comment = iter.collectUntill(newline, nothing);
                l.add(new Token(Token.Type.Comment, comment));

            } else if (c == '\n') { // end of line
                l.add(new Token(Token.Type.Newline, ";\n"));
                iter.skip();

            } else if (iter.contains(delims, iter.peek(), iter.peek(1))) {
                final String m = iter.match(delims, iter.peek(), iter.peek(1));
                l.add(new Token(Token.Type.Keyword, m));
                iter.skip(m.length());

            } else if (iter.contains(whitespace, iter.peek(), iter.peek(1))) {
                final String pad = iter.collectUntillLast(whitespace);
                if (l.get(l.size() - 1).type == Token.Type.Newline) {
                    l.add(new Token(Token.Type.Indent, pad));
                }
                iter.skip();

            } else if (c == '"') {
                iter.skip(1);
                String token = iter.collectUntill(stringend, stringend);
                while (iter.peek(0) == '\\') {
                    switch (iter.peek(1)) {
                    case 'n':
                        token += "\n";
                        break;
                    case 't':
                        token += "\t";
                        break;
                    default:
                        token += iter.peek(1);
                    }
                    iter.skip(2);
                    token += iter.collectUntill(stringend, stringend);
                }
                iter.skip(1);
                l.add(new Token(Token.Type.Text, token));

            } else {
                final String token = iter.collectUntill(delims, whitespace);

                final Token.Type type = token.matches("[-+]?([0-9]*.?[0-9]+|inf)") ? Token.Type.Number : //
                        token.matches("(true|false)") ? Token.Type.Bool : //
                                Token.Type.Name;

                l.add(new Token(type, token));

            }

        }

        l.add(new Token(Token.Type.Newline));
        return l;
    }

}
