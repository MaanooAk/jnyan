
package com.maanoo.jnyan;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TestOperations {

    @Test
    public void numbersBasic() throws Exception {

        final ValueHolder.Int v5 = new ValueHolder.Int(5);
        final ValueHolder.Int v10 = new ValueHolder.Int(10);

        NyanOperation.Operation.MultiplyI.perform(v10, v5);
        assertTrue(v10.value == 50);
        NyanOperation.Operation.DivideI.perform(v10, v5);
        assertTrue(v10.value == 10);
        NyanOperation.Operation.AddI.perform(v10, v5);
        assertTrue(v10.value == 15);

    }

}
