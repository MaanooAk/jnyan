
package com.maanoo.jnyan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.maanoo.jnyan.builders.Builder;


@RunWith(Parameterized.class)
public class TestMaster {

    private static final String PATH = "src/test/resources/tests";

    @Parameters(name = "{0}")
    public static List<String> data() throws IOException {

        return Files.list(Paths.get(PATH)) //
                .filter(i -> Files.isDirectory(i)) //
                .filter(i -> !Files.exists(i.resolve("ignore"))) //
                .map(i -> i.getFileName().toString()) //
                .sorted() //
                .collect(Collectors.toList());
    }

    // ===

    private Path directory;
    private Path main;
    private List<Path> sources;
    private List<Path> sims;

    public TestMaster(String name) throws IOException {

        directory = Paths.get(PATH, name);

        main = directory.resolve("main.nyan");
        sources = Files.list(directory) //
                .filter(Files::isRegularFile)//
                // .filter(i -> i.getFileName().endsWith(".nyan")) //
                .collect(Collectors.toList());

        final Path sim = directory.resolve("sim");
        if (Files.exists(sim)) {
            sims = Files.list(sim) //
                    .filter(Files::isRegularFile) //
                    // .filter(i -> i.getFileName().endsWith(".txt")) //
                    .collect(Collectors.toList());
        }

        if (!Files.exists(main)) {
            main = sources.get(0);
        }
    }

    // ===

    @Test
    public void parse() throws Exception {

        for (final Path i : sources) {

            final Parser p = new Parser();
            final String text = realAllText(i);
            final ArrayList<Token> tokens = p.parse(text);

//            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//            System.out.println(main);
//            System.out.println(tokens);

            assertTrue(tokens.size() > 0);

        }
    }

    @Test
    public void load() throws Exception {

        final Database db = new Database();
        db.load(main);

        assertTrue(db.size() > 0);
    }

    @Test
    public void sim() throws Exception {
        if (sims == null) return;

        for (final Path i : sims) {

            final Database db = new Database();
            db.load(main);

            final String sim = realAllText(i);

            String namespace = "main";

            for (final String line : sim.split("([ \t]*\n+[ \t]*)+")) {
                if (line.length() == 0) continue;
                if (line.startsWith("#")) continue;

                final String[] parts = line.split("[ \t]+", 4);

                if (parts[0].equals("namespace")) {
                    namespace = parts[1];

                } else if (parts[0].equals("patch")) {
                    final String name = Builder.nm(parts[1], namespace);

                    assertNotNull("Object " + name + " cannot be found", db.getPatch(name));

                    db.patch(name);

                } else {
                    final String name = Builder.nm(parts[0], namespace);

                    final NyanObject object = db.get(name);
                    assertNotNull("Object " + name + " cannot be found", object);

                    if (parts[1].equals("#")) {
                        // count members

                        final int values = object.calc().size();
                        assertEquals(line, parts[2], values + "");

                    } else {
                        // check single member

                        final ValueHolder value = object.calc().get(parts[1]);
                        assertNotNull("Member " + parts[1] + " in " + name + " cannot be found", value);

                        if (parts[2].equals("==")) {
                            assertEquals(line, parts[3], value.toString());
                        } else {
                            throw new RuntimeException("Unknown operator " + parts[2]);
                        }
                    }
                }

            }

        }

    }

    private String realAllText(final Path i) throws IOException {
        return new String(Files.readAllBytes(i), Charset.forName("UTF-8"));
    }
}
