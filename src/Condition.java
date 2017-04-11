/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing a condition, which contains two expressions and a
 * relation.
 */
public class Condition extends Node {
    /**
     * The left expression.
     */
    public Expression left;

    /**
     * The right expression.
     */
    public Expression right;

    /**
     * The relationship between the two expressions.
     */
    public String relation;

    /**
     * Create a condition.
     * @param l the left expression
     * @param r the right expression
     * @param relation the relationship
     */
    public Condition(Expression l, Expression r, String relation) {
        this.left = l;
        this.right = r;
        this.relation = relation;
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
        return this.relation;
    }
}
