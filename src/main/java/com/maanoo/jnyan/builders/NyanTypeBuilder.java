
package com.maanoo.jnyan.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.maanoo.jnyan.Database;
import com.maanoo.jnyan.NyanType;
import com.maanoo.jnyan.NyanType.Type;
import com.maanoo.jnyan.Token;
import com.maanoo.jnyan.TokenIter;


public class NyanTypeBuilder implements Builder<NyanType> {

    public static HashMap<String, Type> map = new HashMap<>();
    static {
        for (final Type i : Type.values()) {
            map.put(i.name().toLowerCase(), i);
        }
    }

    // ===

    public final NyanType.Type type;
    public final String name;
    public final NyanTypeBuilder[] params;

    public NyanTypeBuilder(TokenIter iter) {

        {
            final String typeText = iter.next().text;

            final NyanType.Type type = map.get(typeText);
            if (type == null) {
                this.type = Type.Object;
                this.name = typeText;
            } else {
                this.type = type;
                this.name = null;
            }
        }

        if (type == Type.Set || type == Type.OrderedSet || type == Type.List) {

            iter.consume(Token.Type.Keyword, "(");
            final NyanTypeBuilder param = new NyanTypeBuilder(iter);
            iter.consume(Token.Type.Keyword, ")");

            params = new NyanTypeBuilder[] { param };

        } else if (type == Type.Dict) {

            iter.consume(Token.Type.Keyword, "(");
            final NyanTypeBuilder param1 = new NyanTypeBuilder(iter);
            iter.consumeOptional(Token.Type.Keyword, ",");
            final NyanTypeBuilder param2 = new NyanTypeBuilder(iter);
            iter.consume(Token.Type.Keyword, ")");

            params = new NyanTypeBuilder[] { param1, param2 };

        } else {
            params = null;
        }

    }

    public void depens(Set<String> depens) {

        if (name != null) depens.add(name);

        if (params != null) {
            for (final NyanTypeBuilder i : params) {
                i.depens(depens);
            }
        }
    }

    public NyanType build(Database database) {

        assert name == null || params == null;

        if (name != null) {
            return new NyanType.Reference(database.get(name));

        } else if (params != null) {

            final ArrayList<NyanType> l = new ArrayList<>(params.length);
            for (final NyanTypeBuilder i : params)
                l.add(i.build(database));

            return new NyanType.Paramed(type, l.toArray(new NyanType[l.size()]));

        } else {
            return new NyanType.Simple(type);
        }
    }

}
