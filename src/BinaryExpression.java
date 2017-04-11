/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * An expression that has a left and right expression.
 */
public class BinaryExpression extends Expression {
    /**
     * The operator of the binary expression.
     */
    public String operator;

    /**
     * Left part of expression.
     */
    public Expression left;

    /**
     * Right part of expression.
     */
    public Expression right;

    /**
     * Create a binary expression.
     * @param o operator
     * @param l left
     * @param r right
     */
    public BinaryExpression(String o, Expression l, Expression r) {
        this.operator = o;
        this.left = l;
        this.right = r;
        this.type = this.left.type;
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
        return this.operator;
    }

    @Override
    public Expression cloneExp() {
        return new BinaryExpression(
                this.operator, this.left.cloneExp(), this.right.cloneExp());
    }
}
