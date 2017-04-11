/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * Outputs an abstract syntax tree as graph in dot format.
 */
public class GraphicalASTVisitor implements ASTVisitor {
    private StringBuilder sb = new StringBuilder();
    private Scope scope;
    private int varNumber;

    /**
     * Create a graphical ast visitor.
     */
    public GraphicalASTVisitor() {
        this.sb.append("strict digraph X {\n");
    }

    @Override
    public void visitInstructions(Instruction i, Scope s) {
        this.scope = s;
        Instruction n = i;
        Instruction prev = null;
        StringBuilder rank = new StringBuilder();
        rank.append("{rank=same; ");
        // create instruction node
        while (n != null) {
            rank.append(n.hashCode());
            rank.append(";");
            this.createNode(n.hashCode(), n.toString(), "rectangle");
            if (prev != null) {
                this.createArrow(prev.hashCode(), n.hashCode(), "next");
            }
            n.accept(this);
            prev = n;
            n = n.next;
        }
        rank.append("}\n");
        this.sb.append(rank.toString());
    }

    @Override
    public void visit(Assign a) {
        // create location node
        this.createNode(
                a.location.hashCode(), a.location.toString(), "rectangle");
        this.createArrow(a.hashCode(), a.location.hashCode(), "location");
        a.location.accept(this);
        // create expression node
        this.createNode(
                a.expression.hashCode(), a.expression.toString(), "rectangle");
        this.createArrow(a.hashCode(), a.expression.hashCode(), "expression");
        a.expression.accept(this);

    }

    @Override
    public void visit(Read r) {
        // create location node
        this.createNode(
                r.location.hashCode(), r.location.toString(), "rectangle");
        this.createArrow(r.hashCode(), r.location.hashCode(), "location");
        r.location.accept(this);
    }

    @Override
    public void visit(Write w) {
        // create expression node
        this.createNode(w.expression.hashCode(), w.expression.toString(),
                "rectangle");
        this.createArrow(w.hashCode(), w.expression.hashCode(), "expression");
        w.expression.accept(this);
    }

    @Override
    public void visit(If i) {
        // create condition node
        this.createNode(i.condition.hashCode(), i.condition.toString(),
                "rectangle");
        this.createArrow(i.hashCode(), i.condition.hashCode(), "condition");
        i.condition.accept(this);
        // create true instruction
        this.createNode(i.instructionTrue.hashCode(),
                i.instructionTrue.toString(), "rectangle");
        // TODO maybe change label?
        this.createArrow(i.hashCode(), i.instructionTrue.hashCode(),
                "true");
        this.visitInstructions(i.instructionTrue, this.scope);
        // create false instruction
        if (i.instructionFalse != null) {
            this.createNode(i.instructionFalse.hashCode(),
                    i.instructionFalse.toString(), "rectangle");
            this.createArrow(i.hashCode(), i.instructionFalse.hashCode(),
                    "false");
            this.visitInstructions(i.instructionFalse, this.scope);
        }
    }

    @Override
    public void visit(Repeat r) {
        // create condition node
        this.createNode(r.condition.hashCode(), r.condition.toString(),
                "rectangle");
        this.createArrow(r.hashCode(), r.condition.hashCode(), "condition");
        r.condition.accept(this);
        // create instruction node
        this.createNode(r.instruction.hashCode(), r.instruction.toString(),
                "rectangle");
        this.createArrow(
                r.hashCode(), r.instruction.hashCode(), "instructions");
        this.visitInstructions(r.instruction, this.scope);
    }

    @Override
    public void visit(Condition c) {
        // create left node
        this.createNode(c.left.hashCode(), c.left.toString(), "rectangle");
        this.createArrow(c.hashCode(), c.left.hashCode(), "left");
        c.left.accept(this);
        // create right node
        this.createNode(c.right.hashCode(), c.right.toString(), "rectangle");
        this.createArrow(c.hashCode(), c.right.hashCode(), "right");
        c.right.accept(this);
    }

    @Override
    public void visit(BinaryExpression b) {
        // create left node
        this.createNode(b.left.hashCode(), b.left.toString(), "rectangle");
        this.createArrow(b.hashCode(), b.left.hashCode(), "left");
        b.left.accept(this);
        // create right node
        this.createNode(b.right.hashCode(), b.right.toString(), "rectangle");
        this.createArrow(b.hashCode(), b.right.hashCode(), "right");
        b.right.accept(this);
    }

    @Override
    public void visit(NumberExpression n) {
        this.createNode(n.constant.hashCode(), n.constant.value.toString(),
                "diamond");
        this.createArrow(n.hashCode(), n.constant.hashCode(), "ST");
    }

    @Override
    public void visit(VariableLocation v) {
        String symbol = this.scope.findVal(v.variable);
        this.createNode(this.varNumber, symbol, "circle");
        this.createArrow(v.hashCode(), this.varNumber, "ST");
        this.varNumber++;
    }

    @Override
    public void visit(IndexLocation i) {
        // create location node
        this.createNode(i.location.hashCode(), i.location.toString(),
                "rectangle");
        this.createArrow(i.hashCode(), i.location.hashCode(), "location");
        i.location.accept(this);
        // create expression node
        this.createNode(i.expression.hashCode(), i.expression.toString(),
                "rectangle");
        this.createArrow(i.hashCode(), i.expression.hashCode(), "expression");
        i.expression.accept(this);
        // TODO fixme
    }

    @Override
    public void visit(FieldLocation f) {
        // create location node
        this.createNode(f.location.hashCode(), f.location.toString(),
                "rectangle");
        this.createArrow(f.hashCode(), f.location.hashCode(), "location");
        f.location.accept(this);
        // create variable nodes
        this.createNode(
                f.variable.hashCode(), f.variable.toString(), "rectangle");
        this.createArrow(f.hashCode(), f.variable.hashCode(), "variable");
        this.visitField(f.variable, ((Record) f.location.type).scope);
        this.varNumber++;
    }

    private void visitField(VariableLocation v, Scope s) {
        String symbol = s.findVal(v.variable);
        this.createNode(this.varNumber, symbol, "circle");
        this.createArrow(v.hashCode(), this.varNumber, "ST");
        this.varNumber++;
    }

    @Override
    public void output() {
        this.sb.append("}");
        System.out.print(this.sb.toString());
    }

    private void createNode(Object name, String label, String shape) {
        this.sb.append(name);
        this.sb.append(" [label=\"");
        this.sb.append(label);
        this.sb.append("\", shape=");
        this.sb.append(shape);
        this.sb.append("]\n");
    }

    private void createArrow(Object from, Object to, String label) {
        this.sb.append(from);
        this.sb.append(" -> ");
        this.sb.append(to);
        this.sb.append(" [label=\"");
        this.sb.append(label);
        this.sb.append("\"]\n");
    }
}
