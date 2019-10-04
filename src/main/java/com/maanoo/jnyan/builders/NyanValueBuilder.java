
package com.maanoo.jnyan.builders;

import java.util.ArrayList;
import java.util.Set;

import com.maanoo.jnyan.Database;
import com.maanoo.jnyan.NyanType;
import com.maanoo.jnyan.NyanValue;
import com.maanoo.jnyan.Token;
import com.maanoo.jnyan.TokenIter;
import com.maanoo.jnyan.ValueHolder;


public class NyanValueBuilder implements Builder<NyanValue> {

    private TokenIter subIter;

    private ArrayList<String> names;

    public NyanValueBuilder(TokenIter iter) {
        names = new ArrayList<>();

        final int start = iter.passed();

        while (iter.peek(0).type != Token.Type.Newline) {

            if (iter.peek(0).type == Token.Type.Name) {
                names.add(iter.peek(0).text);
            }

            iter.skip(1);
        }

        final int end = iter.passed();

        subIter = iter.extract(start, end + 1);

    }

    public void depens(Set<String> depens) {

        depens.addAll(names);
    }

    public NyanValue build(Database database, NyanType type) {

        final ValueHolder value = ValueHolder.create(type, subIter, database);

        return new NyanValue(type, value);
    }

    public Token.Origin getOrigin() {
        return subIter.first().origin;
    }

}
