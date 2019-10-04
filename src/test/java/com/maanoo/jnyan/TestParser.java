
package com.maanoo.jnyan;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;


public class TestParser {

    @Test
    public void simpleLines() throws Exception {

        final Parser p = new Parser();

        {
            final ArrayList<Token> l = p.parse("hello people");

            assertEquals(2, l.size());
            assertEquals("hello", l.get(0).text);
            assertEquals("people", l.get(1).text);

            assertEquals("[Name(hello), Name(people)]", l.toString());
        }
        {
            final ArrayList<Token> l = p.parse("Hello():\n    people: int = 10\n    compressed:text=\"123\"");

            assertEquals(18, l.size());

            assertEquals("[Name(Hello), Keyword((), Keyword()), Keyword(:), Newline(;\n"
                    + "), Indent(    ), Name(people), Keyword(:), Name(int), Keyword(=), Number(10), Newline(;\n"
                    + "), Indent(    ), Name(compressed), Keyword(:), Name(text), Keyword(=), Text(123)]",
                    l.toString());
        }

    }

}
