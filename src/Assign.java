/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing the "assign" instruction.
 */
public class Assign extends Instruction {
    /**
     * The location of the assignment in memory.
     */
    public Location location;

    /**
     * The value of the assignment as an expression.
     */
    public Expression expression;

    /**
     * Create an assignment.
     * @param l location
     * @param e expression
     */
    public Assign(Location l, Expression e) {
        this.location = l;
        this.expression = e;
    }

    @Override
    public void accept(ASTVisitor a) {
        a.visit(this);
    }

    /**
     * Visit a code generator.
     * @param cg code generator
     */
    public void accept(CodeGenerator cg) {
        cg.visit(this);
    }

    @Override
    public String toString() {
        return ":=";
    }
}
