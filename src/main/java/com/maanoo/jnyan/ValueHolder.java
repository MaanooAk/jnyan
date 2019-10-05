
package com.maanoo.jnyan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.maanoo.jnyan.util.ArraySet;


public abstract class ValueHolder {

    public static ValueHolder create(NyanType type, TokenIter iter, Database database, String localNamespace) {

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

        } else if (type.type == NyanType.Type.Set) {
            final NyanType param1 = type.<NyanType.Paramed>get().params[0];
            return new Set(iter, param1, database, localNamespace);

        } else if (type.type == NyanType.Type.OrderedSet) {
            final NyanType param1 = type.<NyanType.Paramed>get().params[0];
            return new OrderedSet(iter, param1, database, localNamespace);

        } else if (type.type == NyanType.Type.List) {
            final NyanType param1 = type.<NyanType.Paramed>get().params[0];
            return new Lista(iter, param1, database, localNamespace);

        } else if (type.type == NyanType.Type.Dict) {
            final NyanType param1 = type.<NyanType.Paramed>get().params[0];
            final NyanType param2 = type.<NyanType.Paramed>get().params[1];
            return new Dict(iter, param1, param2, database, localNamespace);

        } else if (type.type == NyanType.Type.Object) {
            final NyanObject name = type.<NyanType.Reference>get().name;
            return new Reference(iter, name, database, localNamespace);
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
            final String text = iter.next(Token.Type.Number).text;
            try {
                value = Integer.parseInt(text);
            } catch (final NumberFormatException e) {
                iter.skip(-1); // got back one step
                throw new RuntimeException(text + " is not a integer");
            }
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
            final String text = iter.next(Token.Type.Number).text;
            if (text.equals("inf")) value = java.lang.Float.POSITIVE_INFINITY;
            else if (text.equals("-inf")) value = java.lang.Float.NEGATIVE_INFINITY;
            else value = java.lang.Float.parseFloat(text);
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

    // ===

    public static final class Reference extends ValueHolder {

        public NyanObject value;

        public Reference(NyanObject value) {
            this.value = value;
        }

        public Reference(TokenIter iter, NyanObject type, Database database, String localNamespace) {

            final String text = iter.next(Token.Type.Name).text;
            value = database.get(text, localNamespace);

            if (value == null) throw new RuntimeException("Cannot find " + text);
            if (!value.is(type)) throw new RuntimeException(text + " is not instance of " + type);
        }

        @Override
        public Reference copy() {
            return new Reference(value);
        }

        @Override
        public String toString() {
            return value.toString();
        }

    }

    // ===

    protected static void consumeCollection(Collection<ValueHolder> values, TokenIter iter, String start, String sep,
            String end, NyanType type, Database database, String localNamespace) {

        iter.consume(Token.Type.Keyword, start);

        if (iter.peek().text.equals(end)) {
            iter.consume(Token.Type.Keyword, end);
            return; // empty collection
        }

        while (iter.has()) {

            values.add(ValueHolder.create(type, iter, database, localNamespace));

            if (iter.peek().text.equals(end)) {
                iter.consume(Token.Type.Keyword, end);
                return;
            } else {
                iter.consumeOptional(Token.Type.Keyword, sep);
            }
        }

    }

    protected static void consumeCollection(Map<ValueHolder, ValueHolder> values, TokenIter iter, String start,
            String sep, String sepsep, String end, NyanType type1, NyanType type2, Database database,
            String localNamespace) {

        iter.consume(Token.Type.Keyword, start);

        if (iter.peek().text.equals(end)) {
            iter.consume(Token.Type.Keyword, end);
            return; // empty collection
        }

        while (iter.has()) {

            final ValueHolder v1 = ValueHolder.create(type1, iter, database, localNamespace);
            iter.consume(Token.Type.Keyword, sepsep);
            final ValueHolder v2 = ValueHolder.create(type2, iter, database, localNamespace);

            values.put(v1, v2);

            if (iter.peek().text.equals(end)) {
                iter.consume(Token.Type.Keyword, end);
                return;
            } else {
                iter.consumeOptional(Token.Type.Keyword, sep);
            }
        }

    }

    // ===

    public static final class Set extends ValueHolder {

        public final HashSet<ValueHolder> values;

        public Set(HashSet<ValueHolder> values) {
            this.values = new HashSet<ValueHolder>(values);
        }

        public Set(TokenIter iter, NyanType type, Database database, String localNamespace) {
            values = new HashSet<>();

            consumeCollection(values, iter, "{", ",", "}", type, database, localNamespace);
        }

        @Override
        public Set copy() {
            return new Set(values);
        }

        @Override
        public String toString() {
            return values.toString();
        }

    }

    public static final class OrderedSet extends ValueHolder {

        public final ArraySet<ValueHolder> values;

        public OrderedSet(ArraySet<ValueHolder> values) {
            this.values = new ArraySet<ValueHolder>(values);
        }

        public OrderedSet(TokenIter iter, NyanType type, Database database, String localNamespace) {
            this.values = new ArraySet<>();

            consumeCollection(values, iter, "o{", ",", "}", type, database, localNamespace);
        }

        @Override
        public OrderedSet copy() {
            return new OrderedSet(values);
        }

        @Override
        public String toString() {
            return values.toString();
        }

    }

    public static final class Lista extends ValueHolder {

        public final ArrayList<ValueHolder> values;

        public Lista(ArrayList<ValueHolder> values) {
            this.values = new ArrayList<ValueHolder>(values);
        }

        public Lista(TokenIter iter, NyanType type, Database database, String localNamespace) {
            this.values = new ArrayList<>();

            consumeCollection(values, iter, "[", ",", "]", type, database, localNamespace);
        }

        @Override
        public Lista copy() {
            return new Lista(values);
        }

        @Override
        public String toString() {
            return values.toString();
        }

    }

    public static final class Dict extends ValueHolder {

        public final HashMap<ValueHolder, ValueHolder> values;

        public Dict(HashMap<ValueHolder, ValueHolder> values) {
            this.values = new HashMap<ValueHolder, ValueHolder>(values);
        }

        public Dict(TokenIter iter, NyanType key, NyanType value, Database database, String localNamespace) {
            this.values = new HashMap<>();

            consumeCollection(values, iter, "{", ",", ":", "}", key, value, database, localNamespace);
        }

        @Override
        public Dict copy() {
            return new Dict(values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

}
