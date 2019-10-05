
package com.maanoo.jnyan;

import java.util.List;

import com.maanoo.jnyan.util.ObjectIter;


public class TokenIter extends ObjectIter<Token> {

    // TODO make custom extensions

    public TokenIter(List<Token> tokens) {
        super(tokens);
    }

    public TokenIter extract(int start, int end) {
        return new TokenIter(this.source().subList(start, end));
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

    public void consumeOptional(Token.Type type, String text) {
        final Token t = peek(0);
        if (t.type == type && t.text.equals(text)) {
            skip(1);
        }
    }

    // ===

    private void check(int skip, Token.Type type) {

        if (!has(1 + skip)) throw new RuntimeException("Expected " + type + " instead of end @ " + peek(0).origin);

        final Token t = peek(skip);
        if (t.type != type) {
            throw new RuntimeException("Expected " + type + " instead of " + peek(0) + " @ " + peek(0).origin);
        }
    }

    private void check(int skip, Token.Type type, String text) {

        if (!has(1 + skip))
            throw new RuntimeException("Expected " + type + "(" + text + ") instead of end @ " + peek(0).origin);

        final Token t = peek(skip);
        if (t.type != type || !t.text.equals(text)) {
            throw new RuntimeException("Expected " + type + "(" + text + ") instead of " + t + " @ " + peek(0).origin);
        }
    }

}
