
package com.maanoo.jnyan.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;


public class TestC3 {

    @Test
    public void testName() throws Exception {

        // SOURCE: https://en.wikipedia.org/wiki/C3_linearization @2019-10-05

        final C3<String> c = new C3<String>() {

            private final HashMap<String, List<String>> map = new HashMap<>();

            {
                map.put("O", Arrays.asList());
                map.put("A", asList("O"));
                map.put("B", asList("O"));
                map.put("C", asList("O"));
                map.put("D", asList("O"));
                map.put("E", asList("O"));
                map.put("K1", asList("A, B, C"));
                map.put("K2", asList("D, B, E"));
                map.put("K3", asList("D, A"));
                map.put("Z", asList("K1, K2, K3"));
            }

            @Override
            public List<String> parents(String target) {
                return map.get(target);
            }
        };

        assertEquals(asList("O"), c.linear("O"));
        assertEquals(asList("A, O"), c.linear("A"));
        assertEquals(asList("B, O"), c.linear("B"));
        assertEquals(asList("C, O"), c.linear("C"));
        assertEquals(asList("D, O"), c.linear("D"));
        assertEquals(asList("E, O"), c.linear("E"));
        assertEquals(asList("K1, A, B, C, O"), c.linear("K1"));
        assertEquals(asList("K2, D, B, E, O"), c.linear("K2"));
        assertEquals(asList("K3, D, A, O"), c.linear("K3"));
        assertEquals(asList("Z, K1, K2, K3, D, A, B, C, E, O"), c.linear("Z"));

    }

    private static final List<String> asList(String text) {
        return Arrays.asList(text.split(", "));
    }

}
