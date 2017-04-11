/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing an expression, which can be a number, a location, or
 * a binary expression.
 */
public abstract class Expression extends Node {
    /**
     * The type of the expression.
     */
    public Type type;

    /**
     * Clone an expression.
     * @return clone
     */
    public abstract Expression cloneExp();

    /**
     * Accept a code generator.
     * @param cg code generator
     */
    public abstract void accept(CodeGenerator cg);
}
