
package com.maanoo.jnyan.util;

import java.util.Iterator;


public class StringIter implements Iterator<Character> {

    protected final String text;
    protected int index;

    public StringIter(String text) {
        this.text = text;
        index = 0;
    }

    public char peek() {
        return text.charAt(index);
    }

    public char peek(int skip) {
        return text.charAt(index + skip);
    }

    public void skip() {
        index++;
    }

    public void skip(int count) {
        index += count;
    }

    public void skipUntill(char c) {
        while (has() && peek() != c) {
            skip();
        }
    }

    public boolean has() {
        return index < text.length();
    }

    public boolean has(int count) {
        return index + count - 1 < text.length();
    }

    // Iterator implementation

    @Override
    @Deprecated
    public boolean hasNext() {
        return has();
    }

    @Override
    @Deprecated
    public Character next() {
        return next();
    }

}
