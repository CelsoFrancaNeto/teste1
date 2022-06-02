package interpreter.value;

import java.util.List;

public class ArrayValue extends Value<List<Value<?>>> {

    private List<Value<?>> value;

    public ArrayValue(List<Value<?>> value) {
        this.value = value;
    }

    @Override
    public List<Value<?>> value() {
        return this.value;
    }

    @Override
    public boolean eval() {
        return !value.isEmpty();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ArrayValue) {
            return this.value == ((ArrayValue) obj).value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");

        for (Value<?> v : this.value) {
            sb.append(v == null ? "null" : v.toString());
            sb.append(", ");
        }

        if (sb.length() > 1)
            sb.setLength(sb.length() - 2);

        sb.append("]");
        return sb.toString();
    }

}