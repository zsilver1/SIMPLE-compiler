// Zach Silver
// zsilver1@jhu.edu

/**
 * Constant class which contains the constant's type and value.
 */
public class Constant extends Entry {

    /**
     * The type of the constant.
     */
    public Type type;

    /**
     * The value of the constant.
     */
    public Object value;

    /**
     * Create a constant.
     * @param t type of the constant
     * @param v value of the constant
     */
    public Constant(Type t, Object v) {
        this.type = t;
        this.value = v;
    }

    /**
     * Creates string representation.
     * @return string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Constant<Type:");
        sb.append(this.type.toString());
        sb.append(", Value:");
        sb.append(this.value.toString());
        sb.append(">");
        return sb.toString();
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
