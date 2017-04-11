/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A symbol table visitor used for outputting symbol tables.
 */
public interface STVisitor {
    /**
     * Visit a scope for output.
     * @param s scope
     */
    void visit(Scope s);

    /**
     * Visit a array for output.
     * @param a array
     */
    void visit(Array a);

    /**
     * Visit a record for output.
     * @param r record
     */
    void visit(Record r);

    /**
     * Visit a integer for output.
     * @param i integer
     */
    void visit(Integer i);

    /**
     * Visit a constant for output.
     * @param c constant
     */
    void visit(Constant c);

    /**
     * Visit a variable for output.
     * @param v variable
     */
    void visit(Variable v);

    /**
     * Output the visitor.
     */
    void output();
}
