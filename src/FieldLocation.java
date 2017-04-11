/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A location in memory of a record field.
 */
public class FieldLocation extends Location {
    /**
     * The location of the record in memory.
     */
    public Location location;

    /**
     * The variable of the record.
     */
    public VariableLocation variable;

    /**
     * Create a field.
     * @param l location
     * @param v variable
     */
    public FieldLocation(Location l, VariableLocation v) {
        this.location = l;
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
        return "Field";
    }

    @Override
    public Location cloneExp() {
        return new FieldLocation(
                this.location.cloneExp(), this.variable.cloneExp());
    }
}
