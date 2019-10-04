
package com.maanoo.jnyan;

public class Token {

    public enum Type {

        Keyword, Name,

        Text(true), Number(true), Bool(true),

        Newline, Indent,

        Comment;

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
    public final Object origin;

    public Token(Type type) {
        this(type, null, null);
    }

    public Token(Type type, String text) {
        this(type, text, null);
    }

    public Token(Type type, String text, Object origin) {
        this.type = type;
        this.text = text;
        this.origin = origin;
    }

    @Override
    public String toString() {
        return type.name() + "(" + text + ")";
    }

}
