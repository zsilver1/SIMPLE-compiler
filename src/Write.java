/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing the "write" instruction.
 */
public class Write extends Instruction {
    /**
     * The expression to be written.
     */
    public Expression expression;

    /**
     * Create a new write instruction.
     * @param e expression to write
     */
    public Write(Expression e) {
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
        return "Write";
    }
}
