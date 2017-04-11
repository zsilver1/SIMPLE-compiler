/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * Interface that represents the output of an AST.
 */
public interface ASTVisitor {
    /**
     * Visit multiple instructions for output.
     * @param i instruction
     * @param s scope
     */
    void visitInstructions(Instruction i, Scope s);

    /**
     * Visit a assignment for output.
     * @param a assignment
     */
    void visit(Assign a);

    /**
     * Visit a read for output.
     * @param r read
     */
    void visit(Read r);

    /**
     * Visit a write for output.
     * @param w write
     */
    void visit(Write w);

    /**
     * Visit an if for output.
     * @param i if
     */
    void visit(If i);

    /**
     * Visit a repeat for output.
     * @param r repeat
     */
    void visit(Repeat r);

    /**
     * Visit a condition for output.
     * @param c condition
     */
    void visit(Condition c);

    /**
     * Visit a binary expression for output.
     * @param b binary expression
     */
    void visit(BinaryExpression b);

    /**
     * Visit a number expression for output.
     * @param n number expression
     */
    void visit(NumberExpression n);

    /**
     * Visit a variable location for output.
     * @param v variable location
     */
    void visit(VariableLocation v);

    /**
     * Visit an index location for output.
     * @param i index location
     */
    void visit(IndexLocation i);

    /**
     * Visit a field location for output.
     * @param f field location
     */
    void visit(FieldLocation f);

    /**
     * Output the visitor.
     */
    void output();

}
