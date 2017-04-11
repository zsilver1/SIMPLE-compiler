/*
  Zach Silver
  zsilver1@jhu.edu
*/

/**
 * An interface for code generators, should reduce redundancy.
 */
public abstract class CodeGenerator {
    /**
     * String builder used to create source code string.
     */
    protected StringBuilder stringBuilder = new StringBuilder();

    /**
     * Keeps track of how many instructions it's been since last literal pool.
     */
    protected int numInstructions;

    /**
     * The total size of the variables.
     */
    protected int totalVarSize;

    /**
     * Used to create labels.
     */
    protected int labelCounter;

    /**
     * Run the code generator.
     * @return string representation of program
     */
    public abstract String run();

    /**
     * Visit a binary expression.
     * @param b binary expression
     */
    public abstract void visit(BinaryExpression b);


    /**
     * Visit a number expression.
     * @param n number expression
     */
    public abstract void visit(NumberExpression n);

    /**
     * Visit a variable location.
     * @param v variable location
     */
    public abstract void visit(VariableLocation v);

    /**
     * Visit an index location.
     * @param i index location
     */
    public abstract void visit(IndexLocation i);

    /**
     * Visit a field location.
     * @param f field location
     */
    public abstract void visit(FieldLocation f);

    /**
     * Visit an assignment.
     * @param a assignment
     */
    public abstract void visit(Assign a);

    /**
     * Visit an if instruction.
     * @param i if
     */
    public abstract void visit(If i);

    /**
     * Visit a read instruction.
     * @param r read
     */
    public abstract void visit(Read r);

    /**
     * Visit a write instruction.
     * @param w write
     */
    public abstract void visit(Write w);

    /**
     * Visit a repeat instruction.
     * @param r repeat
     */
    public abstract void visit(Repeat r);

    /**
     * Visit a condition.
     * @param c condition
     */
    public abstract void visit(Condition c);


    /**
     * Ends the program with a return value.
     * @param returnVal value to return
     */
    protected void endProgram(String returnVal) {
        returnVal = "#" + returnVal;
        if ("#0".equals(returnVal)) {
            this.writeInstruction("pop", "{fp, pc}");
            this.writeInstruction("mov", "r0", returnVal);
            this.write("");
        } else {
            this.writeInstruction("mov", "r0", returnVal);
            this.writeInstruction("bl", "exit");
        }
    }

    /**
     * Outputs the data for the program.
     * @param size the amount of memory needed for variables
     */
    protected void outputData(java.lang.Integer size) {
        this.outputStringConstants();
        this.outputVariableAllocation(size);
        this.write(".end");
    }

    /**
     * Outputs setup.
     */
    protected void setup() {
        this.write(".text");
        this.outputErrors();
        this.write(".global main");
        this.writeLabel("main:");
        this.writeInstruction("ldr", "r7", "=vars");
        this.writeInstruction("push", "{fp, lr}");
    }

    /**
     * Write an instruction.
     * @param instruction instruction
     * @param r1 arg1
     * @param r2 arg2
     * @param r3 arg3
     */
    protected void writeInstruction(String instruction,
                                    String r1,
                                    String r2,
                                    String r3) {
        this.write(instruction + " " + r1 + "," + r2 + "," + r3);
    }

    /**
     * Write an instruction.
     * @param instruction instruction
     * @param r1 arg1
     * @param r2 arg2
     */
    protected void writeInstruction(String instruction, String r1, String r2) {
        this.write(instruction + " " + r1 + "," + r2);
    }

    /**
     * Write an instruction.
     * @param instruction instruction
     * @param r1 arg
     */
    protected void writeInstruction(String instruction, String r1) {
        this.write(instruction + " " + r1);
    }

    /**
     * Output the correct load instruction for integer i into register.
     * @param i integer
     * @param register register
     */
    protected void outputCorrectImmediateInstruction(int i, String register) {
        if (i >= 256) {
            String strVal = "=" + java.lang.Integer.toString(i);
            this.writeInstruction("ldr", register, strVal);
        } else {
            String strVal = "#" + java.lang.Integer.toString(i);
            this.writeInstruction("mov", register, strVal);
        }
    }

    /**
     * Writes a line.
     * @param s the line to write
     */
    protected void write(String s) {
        this.stringBuilder.append("  ");
        this.stringBuilder.append(s);
        this.stringBuilder.append("\n");
    }

    /**
     * Writes a label.
     * @param s the label to write
     */
    protected void writeLabel(String s) {
        this.stringBuilder.append(s);
        this.stringBuilder.append("\n");
    }

    /**
     * Used to keep track of where to put literal pools.
     */
    protected void incLiteralPool() {
        this.numInstructions++;
        if (this.numInstructions >= 30) {
            this.numInstructions = 0;
            String label = "L" + this.labelCounter;
            this.writeInstruction("b", label);
            this.write(".LTORG");
            this.writeLabel(label + ":");
        }
    }

    /**
     * Outputs string constants.
     */
    private void outputStringConstants() {
        this.write(".data");
        this.writeLabel("array_bounds:");
        this.write(".asciz \"error: array out of bounds\\n\"");
        this.writeLabel("divide_by_0:");
        this.write(".asciz \"error: divide by 0\\n\"");
        this.writeLabel("read_error:");
        this.write(".asciz \"error: incorrect read\\n\"");
        this.writeLabel("format_string:");
        this.write(".asciz \"%d\\n\"");
        this.write("");
    }

    /**
     * Outputs errors to branch to
     */
    private void outputErrors() {
        this.writeLabel("exit_on_error:");
        this.writeInstruction("ldr", "r0", "=stderr");
        this.writeInstruction("ldr", "r0", "[r0]");
        this.writeInstruction("bl", "fprintf");
        this.writeInstruction("mov", "r0", "#1");
        this.writeInstruction("bl", "exit");
        this.writeLabel("array_err:");
        this.writeInstruction("ldr", "r1", "=array_bounds");
        this.writeInstruction("b", "exit_on_error");
        this.writeLabel("read_err:");
        this.writeInstruction("ldr", "r1", "=read_error");
        this.writeInstruction("b", "exit_on_error");
        this.writeLabel("divide_err:");
        this.writeInstruction("ldr", "r1", "=divide_by_0");
        this.writeInstruction("b", "exit_on_error");

    }

    /**
     * Outputs variable allocation.
     * @param size the amount of memory needed
     */
    private void outputVariableAllocation(java.lang.Integer size) {
        this.write(".bss");
        this.writeLabel("vars:");
        this.write(".space " + size.toString());
        this.write("");
        // FIXME?
        this.writeLabel("read_space:");
        this.write(".space 10");
    }
}
