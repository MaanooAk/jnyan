
package com.maanoo.jnyan;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestOperations {

    @Test
    public void numbersBasic() throws Exception {

        final ValueHolder.Int v5 = new ValueHolder.Int(5);
        final ValueHolder.Int v10 = new ValueHolder.Int(10);

        NyanOperation.Operation.MultiplyI.perform(v10, v5);
        assertEquals(50, v10.value);
        NyanOperation.Operation.DivideI.perform(v10, v5);
        assertEquals(10, v10.value);
        NyanOperation.Operation.AddI.perform(v10, v5);
        assertEquals(15, v10.value);

    }

}
