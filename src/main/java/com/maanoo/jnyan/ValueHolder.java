
package com.maanoo.jnyan;

public abstract class ValueHolder {

    public static ValueHolder create(NyanType type, TokenIter iter, Database database) {

        if (type.type == NyanType.Type.Int) {
            return new Int(iter);
        } else if (type.type == NyanType.Type.Float) {
            return new Float(iter);
        } else if (type.type == NyanType.Type.Bool) {
            return new Bool(iter);
        } else if (type.type == NyanType.Type.Text) {
            return new Text(iter);
        } else if (type.type == NyanType.Type.File) {
            return new File(iter);
        }

        throw new RuntimeException("Cannot parse value of " + type);
    }

    // ===

    public abstract ValueHolder copy();

    @Override
    public boolean equals(Object obj) {
        final ValueHolder other = (ValueHolder) obj;
        if (other == this) return true;
        return other.toString().equals(this.toString()); // TODO fix;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public abstract String toString();

    // ===

    public static final class Int extends ValueHolder {

        int value;

        public Int(int value) {
            this.value = value;
        }

        public Int(TokenIter iter) {
            value = Integer.parseInt(iter.next(Token.Type.Number).text);
        }

        @Override
        public Int copy() {
            return new Int(value);
        }

        @Override
        public String toString() {
            return "" + value;
        }

    }

    public static final class Float extends ValueHolder {

        float value;

        public Float(float value) {
            this.value = value;
        }

        public Float(TokenIter iter) {
            value = java.lang.Float.parseFloat(iter.next(Token.Type.Number).text);
        }

        @Override
        public Float copy() {
            return new Float(value);
        }

        @Override
        public String toString() {
            return "" + value;
        }

    }

    public static final class Bool extends ValueHolder {

        private boolean value;

        public Bool(boolean value) {
            this.value = value;
        }

        public Bool(TokenIter iter) {
            value = Boolean.parseBoolean(iter.next(Token.Type.Bool).text);
        }

        @Override
        public ValueHolder copy() {
            return new Bool(value);
        }

        @Override
        public String toString() {
            return "" + value;
        }

    }

    public static class Text extends ValueHolder {

        String value;

        public Text(String value) {
            this.value = value;
        }

        public Text(TokenIter iter) {
            value = iter.next(Token.Type.Text).text;
        }

        @Override
        public Text copy() {
            return new Text(value);
        }

        @Override
        public String toString() {
            return "" + value;
        }

    }

    public static final class File extends Text {

        public File(String value) {
            super(value);
        }

        public File(TokenIter iter) {
            super(iter);
        }

    }

}
