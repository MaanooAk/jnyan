
package com.maanoo.jnyan.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.maanoo.jnyan.Database;
import com.maanoo.jnyan.NyanObject;
import com.maanoo.jnyan.NyanObject.Patch;
import com.maanoo.jnyan.Token;
import com.maanoo.jnyan.TokenIter;


public class NyanObjectBuilder extends Builder<NyanObject> {

    public final String name;
    public final ArrayList<String> parents;

    public final String target;
    public final ArrayList<String> parentmods;

    public final HashMap<String, NyanTypeBuilder> members;
    public final HashMap<String, NyanOperationBuilder> operations;

    public final HashMap<String, NyanObjectBuilder> children;

    public NyanObjectBuilder(TokenIter iter, String namespace) {
        this(iter, namespace, 1);
    }

    public NyanObjectBuilder(TokenIter iter, String namespace, int depth) {
        super(namespace);

        name = nm(iter.next().text);

        parents = new ArrayList<>();
        members = new HashMap<>();
        operations = new HashMap<>();
        children = new HashMap<>();
        parentmods = new ArrayList<>();

        if (iter.peek(0).text.equals("<")) {
            iter.consume(Token.Type.Keyword, "<");

            target = nm(iter.next().text);
            iter.consume(Token.Type.Keyword, ">");
        } else {
            target = null;
        }

        if (target != null && iter.peek(0).text.equals("[")) {
            iter.consume(Token.Type.Keyword, "[");
            while (!iter.peek(0).text.equals("[")) {

                // TODO parse the +

                parentmods.add(nm(iter.peek(0).text));
                parents.add(nm(iter.peek(0).text));
                iter.skip(1);

                if (iter.peek(0).text.equals("]")) {
                    break;
                } else {
                    iter.consumeOptional(Token.Type.Keyword, ",");
                }
            }
            iter.consume(Token.Type.Keyword, "]");
        }

        iter.consume(Token.Type.Keyword, "(");
        while (!iter.peek(0).text.equals(")")) {

            parents.add(nm(iter.next().text));

            if (iter.peek(0).text.equals(")")) {
                break;
            } else {
                iter.consumeOptional(Token.Type.Keyword, ",");
            }
        }
        iter.consume(Token.Type.Keyword, ")");
        iter.consume(Token.Type.Keyword, ":");
        iter.consume(Token.Type.Newline);

        // String indent = iter.next().text; //(Token.Type.Indent);

        while (iter.has(2) && iter.peek(0).type == Token.Type.Indent && iter.peek(0).text.equals(depth + "")) {
            iter.skip(1);

            if (iter.peek(0).type == Token.Type.Newline) {
                iter.skip(1);
                continue;
            }

            if (iter.peek(0).text.equals("...")) {
                iter.skip(1);
                iter.consume(Token.Type.Newline);
                continue;
            }

            final String name = iter.peek(0).text;
            final String op = iter.peek(1).text;

            if (op.equals(":")) {
                iter.skip(2);
                members.put(name, new NyanTypeBuilder(iter, namespace));

                if (iter.peek(0).type != Token.Type.Newline) {

                    operations.put(name, new NyanOperationBuilder(iter, namespace));
                }

            } else if (op.equals("(") || op.equals("<")) {
                children.put(name, new NyanObjectBuilder(iter, namespace, depth + 1));
                continue;

            } else {
                iter.skip(1);
                operations.put(name, new NyanOperationBuilder(iter, namespace));

            }
            iter.consume(Token.Type.Newline);

            while (iter.has() && iter.peek(0).type == Token.Type.Newline) {
                iter.skip();
            }

        }

    }

    @Override
    protected String nm(String name) {
        return name.indexOf('.') == -1 ? namespace + "." + name : name;
    }

    public Set<String> depens() {
        final Set<String> s = new HashSet<>();
        depens(s);
        return s;
    }

    public void depens(Set<String> depens) {

        depens.addAll(parents);

        if (target != null) depens.add(target);
        depens.addAll(parentmods);

        for (final NyanTypeBuilder i : members.values()) {
            i.depens(depens);
        }

        for (final NyanOperationBuilder i : operations.values()) {
            // TODO remove the depen, create empty objects at the start and then fill them
            // after the references are set
            i.depens(depens);
        }

        for (final NyanObjectBuilder i : children.values()) {
            i.depens(depens);
        }

    }

    private NyanObject o;

    public NyanObject prebuild() {

        if (target == null) {
            o = new NyanObject(name);

        } else {
            NyanObject.Patch p;
            o = p = new NyanObject.Patch(name);

        }

        return o;
    }

    public NyanObject build(Database database) {
        if (o == null) prebuild();

        if (target == null) {

            // add root object
            if (parents.isEmpty()) o.parents.add(NyanObject.RootObject);

        } else {
            final NyanObject.Patch p = (Patch) o;
            p.setTarget(database.get(target));

            for (final String i : parentmods)
                p.parentmods.add(database.get(i));

            // add root object
            o.parents.add(NyanObject.RootPatch);
        }

        for (final String i : parents)
            o.parents.add(database.get(i));

        for (final Map.Entry<String, NyanTypeBuilder> i : members.entrySet()) {
            o.members.put(i.getKey(), i.getValue().build(database));
        }

        for (final Map.Entry<String, NyanOperationBuilder> i : operations.entrySet()) {

            o.operations.put(i.getKey(), i.getValue().build(database, o.getMemberType(i.getKey())));
        }

        return o;
    }

    @Override
    public String toString() {
        return name + (target == null ? "" : " " + target) //
                + (parentmods.isEmpty() ? "" : " " + parentmods) //
                + " " + parents + " " + members + " " + operations //
                + " << " + depens();
    }

}
