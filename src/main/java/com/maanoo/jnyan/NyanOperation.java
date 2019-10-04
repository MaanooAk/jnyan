
package com.maanoo.jnyan;

import static com.maanoo.jnyan.NyanType.Type.*;


public class NyanOperation {

    public enum Operation {

        Assign(null, "="),

        TextAppend(Text, "+="),

        AddI(Int, "+="), //
        AddIF(Int, "+=", Float), //
        AddF(Float, "+="), //
        AddFI(Float, "+=", Int), //
        AddSet(Set, "+="), //
        AddOrderedSet(OrderedSet, "+="), //
        AddList(List, "+="), //
        AddDict(Dict, "+="), //

        SubtractI(Int, "-="), //

        DivideI(Int, "/="), //
        DivideIF(Int, "/=", Float), //
        DivideF(Float, "/="),

        MultiplyI(Int, "*="), //
        MultiplyIF(Int, "*=", Float), //
        MultiplyF(Float, "*="),

        // TODO complete the list

        ;

        public final String symbol;
        public final NyanType.Type t1;
        public final NyanType.Type t2;

        private Operation(NyanType.Type t1, String symbol) {
            this(t1, symbol, null);
        }

        private Operation(NyanType.Type t1, String symbol, NyanType.Type t2) {
            this.symbol = symbol;
            this.t1 = t1;
            this.t2 = t2;
        };

        public boolean perform(ValueHolder target, ValueHolder value) {

            switch (this) {
            case TextAppend:
                ((ValueHolder.Text) target).value += ((ValueHolder.Text) value).value;
                return true;
            case AddI:
                ((ValueHolder.Int) target).value += ((ValueHolder.Int) value).value;
                return true;
            case AddIF:
                ((ValueHolder.Int) target).value += ((ValueHolder.Float) value).value;
                return true;
            case AddF:
                ((ValueHolder.Float) target).value += ((ValueHolder.Float) value).value;
                return true;
            case AddFI:
                ((ValueHolder.Float) target).value += ((ValueHolder.Int) value).value;
                return true;
            case SubtractI:
                ((ValueHolder.Int) target).value -= ((ValueHolder.Int) value).value;
                return true;
            case MultiplyI:
                ((ValueHolder.Int) target).value *= ((ValueHolder.Int) value).value;
                return true;
            case MultiplyIF:
                ((ValueHolder.Int) target).value *= ((ValueHolder.Float) value).value;
                return true;
            case DivideI:
                ((ValueHolder.Int) target).value /= ((ValueHolder.Int) value).value;
                return true;
            case DivideF:
                ((ValueHolder.Float) target).value /= ((ValueHolder.Float) value).value;
                return true;
            case DivideIF:
                ((ValueHolder.Int) target).value /= ((ValueHolder.Float) value).value;
                return true;
            case MultiplyF:
                ((ValueHolder.Float) target).value *= ((ValueHolder.Float) value).value;
                return true;
            case AddSet:
                ((ValueHolder.Set) target).values.addAll(((ValueHolder.Set) value).values);
                return true;
            case AddOrderedSet:
                ((ValueHolder.OrderedSet) target).values.addAll(((ValueHolder.OrderedSet) value).values);
                return true;
            case AddList:
                ((ValueHolder.Lista) target).values.addAll(((ValueHolder.Lista) value).values);
                return true;
            case AddDict:
                ((ValueHolder.Dict) target).values.putAll(((ValueHolder.Dict) value).values);
                return true;
            case Assign:
                throw new RuntimeException();
            }

            throw new RuntimeException(name() + " not implemented");
        }
    }

    // ===

    public final Operation operation;
    public final NyanValue value;

    public final boolean defer;

    public NyanOperation(Operation operation, NyanValue value, boolean defer) {
        this.operation = operation;
        this.value = value;
        this.defer = defer;
    }

    @Override
    public String toString() {
        return operation + " " + value;
    }

}
