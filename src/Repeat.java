/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing the "repeat" instruction.
 */
public class Repeat extends Instruction {

    /**
     * The condition of the repeat.
     */
    public Condition condition;

    /**
     * The instruction(s) to be repeated.
     */
    public Instruction instruction;

    /**
     * Create new repeat instruction.
     * @param c condition
     * @param i instruction(s)
     */
    public Repeat(Instruction i, Condition c) {
        this.condition = c;
        this.instruction = i;
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
        return "Repeat";
    }
}
