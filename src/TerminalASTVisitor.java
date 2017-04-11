/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * Outputs an abstract syntax tree to the terminal.
 */
public class TerminalASTVisitor implements ASTVisitor {
    private StringBuilder sb = new StringBuilder();
    private StringBuilder spaces = new StringBuilder();
    // use this to visit symbol table objects
    private TerminalSTVisitor stVisitor = new TerminalSTVisitor();
    private Scope curScope;

    /**
     * Create TerminalASTVisitor.
     */
    public TerminalASTVisitor() {
        this.stVisitor.sb = this.sb;
        this.stVisitor.spaces = this.spaces;
    }

    @Override
    public void visitInstructions(Instruction i, Scope s) {
        this.curScope = s;
        this.write("instructions =>");
        this.indent();
        Instruction n = i;
        while (n != null) {
            n.accept(this);
            n = n.next;
        }
        this.dedent();
    }

    private void visitInstructions(Instruction i) {
        Instruction n = i;
        while (n != null) {
            n.accept(this);
            n = n.next;
        }
    }

    @Override
    public void visit(Assign a) {
        this.write("Assign:");
        this.write("location =>");
        this.indent();
        a.location.accept(this);
        this.dedent();
        this.write("expression =>");
        this.indent();
        a.expression.accept(this);
        this.dedent();
    }

    @Override
    public void visit(Read r) {
        this.write("Read:");
        this.write("location =>");
        this.indent();
        r.location.accept(this);
        this.dedent();
    }

    @Override
    public void visit(Write w) {
        this.write("Write:");
        this.write("expression =>");
        this.indent();
        w.expression.accept(this);
        this.dedent();
    }

    @Override
    public void visit(If i) {
        this.write("If:");
        this.write("condition =>");
        this.indent();
        i.condition.accept(this);
        this.dedent();
        this.write("true =>");
        this.indent();
        this.visitInstructions(i.instructionTrue);
        this.dedent();
        if (i.instructionFalse != null) {
            this.write("false =>");
            this.indent();
            this.visitInstructions(i.instructionFalse);
            this.dedent();
        }

    }

    @Override
    public void visit(Repeat r) {
        this.write("Repeat:");
        this.write("condition =>");
        this.indent();
        r.condition.accept(this);
        this.dedent();
        this.write("instructions =>");
        this.indent();
        this.visitInstructions(r.instruction);
        this.dedent();
    }

    @Override
    public void visit(Condition c) {
        this.write("Condition (" + c.relation + "):");
        this.write("left =>");
        this.indent();
        c.left.accept(this);
        this.dedent();
        this.write("right =>");
        this.indent();
        c.right.accept(this);
        this.dedent();
    }

    @Override
    public void visit(BinaryExpression b) {
        //TODO FIXME MAYBE
        this.write("Binary (" + b.operator + "):");
        this.write("left =>");
        this.indent();
        b.left.accept(this);
        this.dedent();
        this.write("right =>");
        this.indent();
        b.right.accept(this);
        this.dedent();

    }

    @Override
    public void visit(NumberExpression n) {
        this.write("Number:");
        this.write("value =>");
        this.stVisitor.visit(n.constant);
    }

    @Override
    public void visit(VariableLocation v) {
        this.write("Variable:");
        this.write("variable =>");
        this.stVisitor.visit(v.variable);
    }

    @Override
    public void visit(IndexLocation i) {
        this.write("Index:");
        this.write("location =>");
        this.indent();
        i.location.accept(this);
        this.dedent();
        this.write("expression =>");
        this.indent();
        i.expression.accept(this);
        this.dedent();
    }

    @Override
    public void visit(FieldLocation f) {
        this.write("Field:");
        this.write("location =>");
        this.indent();
        f.location.accept(this);
        this.dedent();
        this.write("variable =>");
        this.indent();
        f.variable.accept(this);
        this.dedent();
    }

    @Override
    public void output() {
        System.out.print(this.sb.toString());
    }

    private void indent() {
        this.spaces.append("  ");
    }

    private void dedent() {
        this.spaces.delete(this.spaces.length() - 2, this.spaces.length());
    }

    private void write(String s) {
        this.sb.append(this.spaces.toString());
        this.sb.append(s);
        this.sb.append('\n');
    }
}
