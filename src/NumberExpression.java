/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * An expression that is a constant number.
 */
public class NumberExpression extends Expression {
    /**
     * The constant of the number expression.
     */
    public Constant constant;

    /**
     * Create a number expression.
     * @param c constant
     */
    public NumberExpression(Constant c) {
        this.constant = c;
        this.type = this.constant.type;
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
        return "Number";
    }

    @Override
    public Expression cloneExp() {
        return new NumberExpression(
                new Constant(this.constant.type, this.constant.value));
    }
}
