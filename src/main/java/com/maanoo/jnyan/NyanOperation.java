
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
        AddDict(Dict, "+="), //

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
