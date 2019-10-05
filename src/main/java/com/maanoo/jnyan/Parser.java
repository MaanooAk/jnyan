
package com.maanoo.jnyan;

import java.util.ArrayList;

import com.maanoo.jnyan.Token.Origin;
import com.maanoo.jnyan.util.StringIter;
import com.maanoo.jnyan.util.StringSet;


public class Parser {

    // TODO move all to constructor

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
        return parse(text, ":");
    }

    public ArrayList<Token> parse(String text, String source) {
        final Origin.Tracker origin = new Origin.Tracker(source);

        final ArrayList<Token> l = new ArrayList<>();

        // add padding for the start of the line condition check
        l.add(new Token(Token.Type.Newline, origin.get()));

        // add padding for the lookahead check
        final Iter iter = new Iter(text + "\n");

        // check for two in order to skip the last added previously new line
        while (iter.has(2)) {
            final char c = iter.peek();

            // ===

            if (c == '#') { // single line comments
                @SuppressWarnings("unused")
                final String comment = iter.collectUntill(newline, nothing);
//                l.add(new Token(Token.Type.Comment, comment, origin.get()));

            } else if (c == '\n') { // end of line

                while (!l.isEmpty() && (l.get(l.size() - 1).type == Token.Type.Indent
                        || l.get(l.size() - 1).type == Token.Type.Newline)) {
                    l.remove(l.size() - 1);
                }

                origin.addLine();
                l.add(new Token(Token.Type.Newline, "\n", origin.get()));
                iter.skip();

            } else if (iter.contains(delims, iter.peek(), iter.peek(1))) { // keywords
                final String m = iter.match(delims, iter.peek(), iter.peek(1));
                l.add(new Token(Token.Type.Keyword, m, origin.get()));
                iter.skip(m.length());

            } else if (iter.contains(whitespace, iter.peek(), iter.peek(1))) { // whitespace
                final String pad = iter.collectUntillLast(whitespace).replace("\t", "    ");
                if (l.get(l.size() - 1).type == Token.Type.Newline) {
                    // TODO improve
                    l.add(new Token(Token.Type.Indent, pad.length() / 4 + "", origin.get()));
                }
                iter.skip();

            } else if (c == '"') { // string and file literals
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
                l.add(new Token(Token.Type.Text, token, origin.get()));

            } else { // values and names
                final String token = iter.collectUntill(delims, whitespace);

                final Token.Type type = token.matches("[-+]?([0-9]*.?[0-9]+|inf)") ? Token.Type.Number : //
                        token.matches("(true|false)") ? Token.Type.Bool : //
                                Token.Type.Name;

                l.add(new Token(type, token, origin.get()));

            }

        }

        // l.remove(0); // remove the padding added at the start
        l.add(new Token(Token.Type.Newline, origin.get()));
        return l;
    }

}
