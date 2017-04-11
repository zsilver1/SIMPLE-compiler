/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing the "if" instruction.
 */
public class If extends Instruction {
    /**
     * The condition of the if.
     */
    public Condition condition;

    /**
     * The instruction(s) to execute if condition is true.
     */
    public Instruction instructionTrue;

    /**
     * The "else" instruction(s).
     */
    public Instruction instructionFalse;

    /**
     * Create an If instruction.
     * @param c condition
     * @param t "true" instruction
     * @param f "false" instruction
     */
    public If(Condition c, Instruction t, Instruction f) {
        this.condition = c;
        this.instructionTrue = t;
        this.instructionFalse = f;
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
        return "If";
    }
}
