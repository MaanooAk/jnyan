
package com.maanoo.jnyan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class NyanObject {

    public static final NyanObject RootObject;
    public static final NyanObject RootPatch;
    static {
        RootObject = new NyanObject("Object");
        RootPatch = new NyanObject("Patch");
        RootPatch.parents.add(RootObject);
    }

    // ===

    public final String name;
    public final NyanObject.Patch patchjob;

    @SuppressWarnings("unused")
    private int mod;

    public final ArrayList<NyanObject> parents;
    public final HashMap<String, NyanType> members;
    public final HashMap<String, NyanOperation> operations;

    public NyanObject(String name) {
        this(name, null);
    }

    public NyanObject(String name, NyanObject.Patch patch) {
        this.name = name;
        this.patchjob = patch;

        parents = new ArrayList<>();
        members = new HashMap<>();
        operations = new HashMap<>();
    }

    private void reset() {

        parents.clear();
        members.clear();
        operations.clear();

        mod++;
    }

    // ===

    public boolean is(NyanObject o) {
        return this.equals(o) || hasParent(o);
    }

    public boolean hasParent(NyanObject parent) {

        if (parents.contains(parent)) return true;

        for (final NyanObject i : parents) {
            if (i.hasParent(parent)) return true;
        }

        return false;
    }

    public boolean hasPatch(NyanObject.Patch patch) {

        for (final NyanObject i : parents) {
            if (i.patchjob == patch) return true;
        }
        for (final NyanObject i : parents) {
            if (i.hasPatch(patch)) return true;
        }

        return false;
    }

    public List<NyanObject> parents() {
        final ArrayList<NyanObject> l = new ArrayList<>();
        // wrongly simulate bfs
        for (final NyanObject i : parents)
            l.add(i);
        for (final NyanObject i : parents)
            l.addAll(i.parents());

        final HashSet<NyanObject> found = new HashSet<>();
        for (final Iterator<NyanObject> iter = l.iterator(); iter.hasNext();) {
            final NyanObject i = iter.next();
            if (found.contains(i)) iter.remove();
            else found.add(i);
        }

        return l;
    }

    // ===

    public NyanType getMemberType(String member) {
        final NyanType type = getOptionalMemberType(member);
        if (type != null) return type;

        throw new RuntimeException("" + name + " does not have member named " + member);
    }

    public NyanType getOptionalMemberType(String member) {
        NyanType type = members.get(member);
        if (type != null) return type;

        for (final NyanObject i : parents) {
            type = i.getOptionalMemberType(member);
            if (type != null) return type;
        }

        return null;
    }

    // ===

    public HashMap<String, ValueHolder> calc() {
        final HashMap<String, NyanType> types = new HashMap<>();
        final HashMap<String, ValueHolder> values = new HashMap<>();

        // if (!patches.isEmpty()) throw new RuntimeException(this + " has patches");

        final List<NyanObject> pars = parents();
        pars.add(0, this);

        System.out.println(pars);

        for (final NyanObject i : pars) {
            types.putAll(i.members);
        }

        for (final String i : types.keySet()) {
            values.put(i, null);

            final ArrayList<NyanOperation> l = new ArrayList<>();
            final ArrayList<NyanOperation> ldefer = new ArrayList<>();

            for (final NyanObject par : pars) {
                if (par.operations.containsKey(i)) {
                    final NyanOperation op = par.operations.get(i);

                    if (!op.defer) {
                        l.add(op);
                    } else {
                        ldefer.add(0, op);
                    }
                }
            }

            l.addAll(0, ldefer);
            if (ldefer.size() > 0) System.out.println(">>>" + l);

            int ip = 0;
            while (l.get(ip).operation != NyanOperation.Operation.Assign) {
                ip += 1;
                if (ip == l.size()) throw new RuntimeException("In " + name + ", " + i + " is abstract");
            }

            final ValueHolder value = l.get(ip).value.value.copy();

            System.out.println(i + " =\n  " + "" + " " + value);
            while (--ip >= 0) {
                final NyanOperation op = l.get(ip);
                op.operation.perform(value, op.value.value);

                System.out.println("  " + "" + " (" + op + ") " + value);
            }

            values.put(i, value);
        }

        return values;
    }

    // ===

    void patch(Patch patch) {
        if (patch.target != this) throw new RuntimeException();

        final NyanObject old = new NyanObject(patch.name + "#" + name, patch);
        old.parents.addAll(parents);
        old.members.putAll(members);
        old.operations.putAll(operations);
        // TODO update dependencies
        reset();

        parents.add(old);
        parents.addAll(patch.parentmods);
        operations.putAll(patch.operations);
    }

    // ===

    @Override
    public String toString() {
        return name;
    }

    public String toStringExtended() {
        final StringBuilder sb = new StringBuilder(name);

        sb.append("(");
        for (final NyanObject i : parents)
            sb.append(i.name).append(", ");
        if (parents.size() > 0) sb.delete(sb.length() - 2, sb.length());
        sb.append("):");

        return sb.toString() + " " + members + " " + operations;

    }

    // ==

    public static class Patch extends NyanObject {

        public final NyanObject target;
        public final ArrayList<NyanObject> parentmods;

        public Patch(String name, NyanObject target) {
            super(name);
            this.target = target;

            parentmods = new ArrayList<>();

            parents.add(target);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String toStringExtended() {
            final StringBuilder sb = new StringBuilder(name);
            sb.append("<").append(target.name).append(">");

            if (!parentmods.isEmpty()) {
                sb.append("[");
                for (final NyanObject i : parents)
                    sb.append(i.name).append(", ");
                if (parents.size() > 0) sb.delete(sb.length() - 2, sb.length());
                sb.append("]");
            }

            sb.append("(");
            for (final NyanObject i : parents)
                if (i != target) sb.append(i.name).append(", ");
            if (parents.size() > 1) sb.delete(sb.length() - 2, sb.length());
            sb.append("):");

            return sb.toString() + " " + members + " " + operations;
        }

    }

}
