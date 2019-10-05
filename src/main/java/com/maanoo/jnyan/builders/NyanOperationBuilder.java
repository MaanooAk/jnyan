
package com.maanoo.jnyan.builders;

import java.util.Set;

import com.maanoo.jnyan.Database;
import com.maanoo.jnyan.NyanOperation;
import com.maanoo.jnyan.NyanOperation.Operation;
import com.maanoo.jnyan.NyanType;
import com.maanoo.jnyan.TokenIter;


public class NyanOperationBuilder extends Builder<NyanOperation> {

    public final String operation;
    public final boolean defer;
    public final NyanValueBuilder value;

    public NyanOperationBuilder(TokenIter iter, String namespace) {
        super(namespace);

        final String first = iter.next().text;

        if (first.equals("defer")) {
            operation = iter.next().text;
            defer = true;
        } else {
            operation = first;
            defer = false;
        }
        this.value = new NyanValueBuilder(iter, namespace);
    }

    public void depens(Set<String> depens) {

//        value.depens(depens);
    }

    public NyanOperation build(Database database, NyanType target) {
        RuntimeException first = null;

        if (operation.equals("=")) {

            return new NyanOperation(Operation.Assign, value.build(database, target), defer);

        } else {

            for (final Operation i : Operation.values()) {
                if (!i.symbol.equals(operation)) continue;
                if (i.t1 != target.type) continue;

                try {
                    if (i.t2 == null) {
                        return new NyanOperation(i, value.build(database, target), defer);
                    } else {
                        return new NyanOperation(i, value.build(database, new NyanType.Simple(i.t2)), defer);
                    }
                } catch (final RuntimeException ex) {
                    if (first == null) first = ex;
                }
            }

        }

        throw first != null ? new RuntimeException("Could not parse value of operation @ " + value.getOrigin(), first)
                : new RuntimeException("Not supported operation " + operation + " on " + target);
    }

}
