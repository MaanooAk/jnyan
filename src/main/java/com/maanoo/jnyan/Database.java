
package com.maanoo.jnyan;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.maanoo.jnyan.builders.NyanObjectBuilder;


public class Database extends View {

    public Database() {
        super(null);

        add(NyanObject.RootObject);
        add(NyanObject.RootPatch);
    }

    @Override
    @Deprecated
    public NyanObject get(String name) {
        return objects.get(name);
    }

    public NyanObject get(String name, String localNamespace) {
        final NyanObject o = get(name);
        if (o != null) return o;

        return get(localNamespace + "." + name);
    }

    // ===

    public void load(Path path) throws IOException {
        final String text = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
        load(text, path.getFileName().toString());
    }

    public void load(String text, String source) {

        // TODO clean up

        final Parser p = new Parser();
        final List<Token> l = p.parse(text, source);

        final TokenIter iter = new TokenIter(l);

        final ArrayList<NyanObjectBuilder> all = new ArrayList<>();

        final String namespace = source.substring(0, source.indexOf('.'));

        while (iter.has(1) && iter.peek(0).type == Token.Type.Newline)
            iter.skip(1);
        while (iter.has(1)) {

            if (iter.peek(0).text.equals("namespace")) {
                iter.skip(1);
                String ns = iter.next().text;
                if (ns.equals(".")) ns = null;

                // namespace = ns;

            } else {

                final NyanObjectBuilder ob = new NyanObjectBuilder(iter, namespace);
                all.add(ob);
                all.addAll(ob.children.values());
            }

            while (iter.has(1) && iter.peek(0).type == Token.Type.Newline)
                iter.skip(1);
        }

        Collections.shuffle(all);
//      Collections.sort(all, new Comparator<NyanObject.Builder>(){
//              @Override
//              public int compare(NyanObject.Builder p1, NyanObject.Builder p2) {
//                  return p1.depens().size() - p2.depens().size();
//              }
//          });

        final Set<String> done = objects.keySet();

        final HashSet<NyanObjectBuilder> left = new HashSet<>(all);

        int lastsize = 0;
        while (!left.isEmpty()) {
            if (left.size() == lastsize) {
                dependecyError(done, left);
            }
            lastsize = left.size();
            for (final Iterator<NyanObjectBuilder> it = left.iterator(); it.hasNext();) {
                final NyanObjectBuilder i = it.next();

                if (done.containsAll(i.depens())) {
                    it.remove();

                    final NyanObject o = i.build(this);
                    add(o);

                }
            }
        }

    }

    private void dependecyError(Set<String> done, Set<NyanObjectBuilder> left) {
        final StringBuilder sb = new StringBuilder("Cannot find depens of:\n");

        for (final NyanObjectBuilder i : left) {

            final HashSet<String> depens = new HashSet<>(i.depens());
            depens.removeAll(done);

            sb.append(i.name).append(": ").append(depens).append("\n");
        }

        throw new RuntimeException(sb.toString());
    }

}
