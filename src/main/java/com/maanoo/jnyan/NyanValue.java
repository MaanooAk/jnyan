
package com.maanoo.jnyan;

public class NyanValue {

    public final NyanType type;
    public final ValueHolder value;

    public NyanValue(NyanType type, ValueHolder value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
