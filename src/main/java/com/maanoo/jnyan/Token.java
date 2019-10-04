
package com.maanoo.jnyan;

public class Token {

    public enum Type {

        Keyword, Name,

        Text(true), Number(true), Bool(true),

        Newline, Indent,

        Comment

        ;

        public final boolean value;

        private Type() {
            this(false);
        }

        private Type(boolean value) {
            this.value = value;
        }

    }

    public final Type type;
    public final String text;
    public final Origin origin;

    public Token(Type type, Origin origin) {
        this(type, null, origin);
    }

    public Token(Type type, String text, Origin origin) {
        this.type = type;
        this.text = text;
        this.origin = origin;
    }

    @Override
    public String toString() {
        return type.name() + "(" + text + ")";
    }

    public static final class Origin {

        public final String source;

        public final int line;
        public final int col;

        public Origin(String source, int line, int col) {
            this.source = source;
            this.line = line;
            this.col = col;
        }

        @Override
        public String toString() {
            return source + ":" + line + ":" + col;
        }

        public static final class Tracker {

            public final String source;

            private int line;
            private int col;

            public Tracker(String source) {
                this.source = source;

                line = 1;
                col = 1;
            }

            public Tracker addCol() {
                col += 1;
                return this;
            }

            public Tracker addLine() {
                line += 1;
                col = 1;
                return this;
            }

            public Origin get() {
                return new Origin(source, line, col);
            }

        }

    }

}
