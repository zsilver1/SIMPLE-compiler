/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A location in memory with an index.
 */
public class IndexLocation extends Location {
    /**
     * The location in memory.
     */
    public Location location;

    /**
     * The expression that represents the index.
     */
    public Expression expression;

    /**
     * Create an index location.
     * @param l location
     * @param e expression
     */
    public IndexLocation(Location l, Expression e) {
        this.location = l;
        this.expression = e;
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
        return "Index";
    }

    @Override
    public Location cloneExp() {
        return new IndexLocation(
                this.location.cloneExp(), this.expression.cloneExp());
    }
}
