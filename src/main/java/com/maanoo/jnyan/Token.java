
package com.maanoo.jnyan;

public class Token {

    public enum Type {

        Keyword, Name,

        Text, Number, Bool,

        Newline, Indent,

        Comment;

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
