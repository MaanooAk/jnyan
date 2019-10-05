
package com.maanoo.jnyan;

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

    public TestMaster(String name) {

        directory = Paths.get(PATH, name);
        main = directory.resolve("main.nyan");

        if (!Files.exists(main)) {
            main = null;
        }
    }

    // ===

    @Test
    public void parse() throws Exception {

        final Parser p = new Parser();
        final String text = new String(Files.readAllBytes(main), Charset.forName("UTF-8"));
        final ArrayList<Token> tokens = p.parse(text);

//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println(main);
//        System.out.println(tokens);

        assertTrue(tokens.size() > 0);
    }

    @Test
    public void load() throws Exception {

        final Database db = new Database();
        db.load(main);

        assertTrue(db.size() > 0);
    }

}
