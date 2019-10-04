
package com.maanoo.jnyan;

import java.util.List;

import com.maanoo.jnyan.util.ObjectIter;


public class TokenIter extends ObjectIter<Token> {

    // TODO make custom extensions

    public TokenIter(List<Token> tokens) {
        super(tokens);
    }

    // ===

    public Token next(Token.Type type) {
        check(0, type);
        return next();
    }

    public Token next(Token.Type type, String text) {
        check(0, type, text);
        return next();
    }

    public void consume(Token.Type type) {
        check(0, type);
        skip(1);
    }

    public void consume(Token.Type type, String text) {
        check(0, type, text);
        skip(1);
    }

    // ===

    private void check(int skip, Token.Type type) {

        if (!has(1 + skip)) throw new RuntimeException("Expected " + type + " instead of end.");

        final Token t = peek(skip);
        if (t.type != type) {
            throw new RuntimeException("Expected " + type + " instead of " + peek(0) + ".");
        }
    }

    private void check(int skip, Token.Type type, String text) {

        if (!has(1 + skip)) throw new RuntimeException("Expected " + type + "(" + text + ") instead of end.");

        final Token t = peek(skip);
        if (t.type != type || !t.text.equals(text)) {
            throw new RuntimeException("Expected " + type + "(" + text + ") instead of " + t + ".");
        }
    }
}
