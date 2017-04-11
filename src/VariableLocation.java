/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A location in memory represented by a variable.
 */
public class VariableLocation extends Location {
    /**
     * The variable in the symbol table.
     */
    public Variable variable;

    /**
     * Create a variable location.
     * @param v variable
     */
    public VariableLocation(Variable v) {
        this.variable = v;
        this.type = this.variable.type;
    }

    @Override
    public void accept(ASTVisitor a) {
        a.visit(this);
    }

    /**
     * Accept an interpreter.
     * @param i interpreter
     */
    public void accept(Interpreter i) {
        i.visit(this);
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
        return "Variable";
    }

    @Override
    public VariableLocation cloneExp() {
        return new VariableLocation(this.variable);
    }
}
