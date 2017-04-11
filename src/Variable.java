// Zach Silver
// zsilver1@jhu.edu

/**
 * Variable class which contains a pointer to the variable's type.
 */
public class Variable extends Entry {
    /**
     * The type of the variable.
     */
    public Type type;

    /**
     * The address of the variable in memory, from 0.
     */
    public int address;

    /**
     * Creates a variable.
     * @param t type of the variable
     */
    public Variable(Type t) {
        this.type = t;
    }

    /**
     * Creates string representation of variable.
     * @return string
     */
    public String toString() {
        return "Variable<Type:" + this.type.toString() + ">";
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
