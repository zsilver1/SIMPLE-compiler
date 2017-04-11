/*
Zach Silver
zsilver1@jhu.edu
*/


import java.lang.*;

/**
 * Code generator for assignment 6, un-optimized.
 */
public class StupidCodeGenerator extends CodeGenerator {
    private Scope scope;
    private Instruction curInstruction;

    /**
     * Create a stupid code generator.
     *
     * @param s scope
     * @param i root of AST
     */
    public StupidCodeGenerator(Scope s, Instruction i) {
        this.curInstruction = i;
        this.scope = s;
        this.totalVarSize = this.traverseScope();
        this.setup();
    }

    @Override
    public String run() {
        while (this.curInstruction != null) {
            this.curInstruction.accept(this);
            this.curInstruction = this.curInstruction.next;
        }
        this.endProgram("0");
        this.outputData(this.totalVarSize);
        return this.stringBuilder.toString();
    }

    @Override
    public void visit(BinaryExpression b) {
        b.right.accept(this);
        b.left.accept(this);
        // r0 is left operand, r1 is right
        this.writeInstruction("pop", "{r0, r1}");
        // dereference pointers to locations
        if (b.right instanceof Location) {
            this.writeInstruction("ldr", "r1", "[r1]");
        }
        if (b.left instanceof Location) {
            this.writeInstruction("ldr", "r0", "[r0]");
        }
        // for DIV and MOD, check if r1 is 0
        switch (b.operator) {
            case "+":
                this.writeInstruction("add", "r0", "r0", "r1");
                break;
            case "-":
                this.writeInstruction("sub", "r0", "r0", "r1");
                break;
            case "*":
                this.writeInstruction("mul", "r0", "r0", "r1");
                break;
            case "DIV":
                this.writeInstruction("cmp", "r1", "#0");
                this.writeInstruction("beq", "divide_err");
                this.writeInstruction("bl", "__aeabi_idiv");
                break;
            case "MOD":
                this.writeInstruction("cmp", "r1", "#0");
                this.writeInstruction("beq", "divide_err");
                this.writeInstruction("bl", "__aeabi_idivmod");
                break;
            default:
                break;
        }
        this.writeInstruction("push", "{r0}");

    }

    @Override
    public void visit(NumberExpression n) {
        int val = (int) n.constant.value;
        // check if value is too large or too small
        this.outputCorrectImmediateInstruction(val, "r0");
        this.writeInstruction("push", "{r0}");
    }

    @Override
    public void visit(VariableLocation v) {
        java.lang.Integer offset = v.variable.address;
        this.outputCorrectImmediateInstruction(offset, "r0");
        this.writeInstruction("add", "r0", "r0", "r7");
        this.writeInstruction("push", "{r0}");
    }

    @Override
    public void visit(IndexLocation i) {
        // subtracting one allows comparison to work
        int len = ((Array) i.location.type).length - 1;
        int typeSize = ((Array) i.location.type).elementType.size;
        i.expression.accept(this);
        i.location.accept(this);
        // r0 is location, r1 is expression
        this.writeInstruction("pop", "{r0, r1}");
        if (i.expression instanceof Location) {
            this.writeInstruction("ldr", "r1", "[r1]");
        }
        // check array bounds (if unsigned
        this.outputCorrectImmediateInstruction(len, "r2");
        this.writeInstruction("cmp", "r1", "r2");
        this.writeInstruction("bhi", "array_err");
        // this.writeInstruction("cmp", "r1", "#0");
        // this.writeInstruction("blt", "array_err");

        // add address in r0 to (offset * element type size)
        // r2 is size of type of array
        this.outputCorrectImmediateInstruction(typeSize, "r2");
        this.writeInstruction("mul", "r1", "r1", "r2");
        this.writeInstruction("add", "r0", "r0", "r1");
        this.writeInstruction("push", "{r0}");
    }

    @Override
    public void visit(FieldLocation f) {
        java.lang.Integer offset = f.variable.variable.address;
        f.location.accept(this);
        this.outputCorrectImmediateInstruction(offset, "r1");
        // r0 is location address, r1 is variable offset
        this.writeInstruction("pop", "{r0}");
        this.writeInstruction("add", "r0", "r0", "r1");
        this.writeInstruction("push", "{r0}");
    }

    @Override
    public void visit(Assign a) {
        a.expression.accept(this);
        a.location.accept(this);
        // r0 is location, r1 is expression
        this.writeInstruction("pop", "{r0, r1}");
        // assigning arrays
        if (a.location.type instanceof Array) {
            int size = ((Array) a.location.type).size;
            // total size kept in r3
            this.outputCorrectImmediateInstruction(size, "r3");
            this.writeCopyLoop();
        } else if (a.location.type instanceof Record) {
            int size = ((Record) a.location.type).size;
            // total size kept in r3
            this.outputCorrectImmediateInstruction(size, "r3");
            this.writeCopyLoop();
        } else {
            // dereference r1 only if we have a location as the expression
            if (a.expression instanceof Location) {
                this.writeInstruction("ldr", "r1", "[r1]");
            }
            this.writeInstruction("str", "r1", "[r0]");
        }
        this.incLiteralPool();
    }

    @Override
    public void visit(If i) {
        i.condition.accept(this);
        Instruction cur;
        String falseLabel = ".L" + this.labelCounter;
        this.labelCounter++;
        String doneLabel = ".L" + this.labelCounter;
        this.labelCounter++;

        // check if condition is true
        this.writeInstruction("pop", "{r0}");
        this.writeInstruction("cmp", "r0", "#0");
        this.writeInstruction("beq", falseLabel);

        cur = i.instructionTrue;
        while (cur != null) {
            cur.accept(this);
            cur = cur.next;
        }
        this.writeInstruction("b", doneLabel);
        // write label if condition false
        this.writeLabel(falseLabel + ":");
        if (i.instructionFalse != null) {
            cur = i.instructionFalse;
            while (cur != null) {
                cur.accept(this);
                cur = cur.next;
            }
        }
        this.writeLabel(doneLabel + ":");
        this.incLiteralPool();
    }

    @Override
    public void visit(Read r) {
        r.location.accept(this);
        // location pointer is r0
        this.writeInstruction("pop", "{r0}");
        // keep the value of r0 after function call, this points to the
        // integer's location in memory to be stored at

        // r4 will store integer's location in memory
        this.writeInstruction("mov", "r4", "r0");

        this.writeInstruction("ldr", "r0", "=read_space");
        this.writeInstruction("mov", "r1", "#10");
        this.writeInstruction("ldr", "r2", "=stdin");
        this.writeInstruction("ldr", "r2", "[r2]");
        this.writeInstruction("bl", "fgets");

        String startLabel = "L" + this.labelCounter;
        this.labelCounter++;
        String doneLabel = "L" + this.labelCounter;
        this.labelCounter++;

        this.writeInstruction("mov", "r1", "#9");
        this.writeInstruction("ldr", "r2", "=read_space");
        this.writeInstruction("mov", "r3", "#31");

        // ENSURE VALID READ
        this.writeLabel(startLabel + ":");
        this.writeInstruction("ldrb", "r0", "[r2]");
        this.writeInstruction("cmp", "r0", "r3");
        this.writeInstruction("ble", doneLabel);
        this.writeInstruction("sub", "r0", "r0", "#48");
        this.writeInstruction("cmp", "r0", "r1");
        this.writeInstruction("bhi", "read_err");
        this.writeInstruction("add", "r2", "r2", "#1");
        this.writeInstruction("b", startLabel);
        this.writeLabel(doneLabel + ":");
        // READ IS VALID, CALL ATOI
        this.writeInstruction("ldr", "r0", "=read_space");
        this.writeInstruction("bl", "atoi");
        this.writeInstruction("str", "r0", "[r4]");
        this.incLiteralPool();
    }

    @Override
    public void visit(Write w) {
        w.expression.accept(this);
        // pop integer result of expression
        this.writeInstruction("pop", "{r1}");
        // de-reference location pointer
        if (w.expression instanceof Location) {
            this.writeInstruction("ldr", "r1", "[r1]");
        }
        this.writeInstruction("ldr", "r0", "=format_string");
        this.writeInstruction("bl", "printf");
        this.incLiteralPool();
    }

    @Override
    public void visit(Repeat r) {
        String loopLabel = ".L" + this.labelCounter;
        this.labelCounter++;
        this.writeLabel(loopLabel + ":");
        Instruction cur = r.instruction;
        while (cur != null) {
            cur.accept(this);
            cur = cur.next;
        }
        r.condition.accept(this);
        this.writeInstruction("pop", "{r0}");
        this.writeInstruction("cmp", "r0", "#0");
        this.writeInstruction("beq", loopLabel);
        this.incLiteralPool();
    }

    @Override
    public void visit(Condition c) {
        c.right.accept(this);
        c.left.accept(this);
        // r1 = right, r0 = left
        this.writeInstruction("mov", "r2", "#0");
        this.writeInstruction("pop", "{r0, r1}");
        // dereference pointers to locations
        if (c.right instanceof Location) {
            this.writeInstruction("ldr", "r1", "[r1]");
        }
        if (c.left instanceof Location) {
            this.writeInstruction("ldr", "r0", "[r0]");
        }
        this.writeInstruction("cmp", "r0", "r1");
        String rel = c.relation;
        switch (rel) {
            case "#":
                this.writeInstruction("movne", "r2", "#1");
                break;
            case "=":
                this.writeInstruction("moveq", "r2", "#1");
                break;
            case "<":
                this.writeInstruction("movlt", "r2", "#1");
                break;
            case ">":
                this.writeInstruction("movgt", "r2", "#1");
                break;
            case "<=":
                this.writeInstruction("movle", "r2", "#1");
                break;
            case ">=":
                this.writeInstruction("movge", "r2", "#1");
                break;
            default:
                break;
        }
        this.writeInstruction("push", "{r2}");
    }

    // allocates space for all variables, finds size of types,
    // ensures constants are 32 bits, returns size of all variables
    private int traverseScope() {
        int curAddress = 0;
        for (Entry e : this.scope.getHashMap().values()) {
            if (e instanceof Variable) {
                // allocate memory
                int memSize = ((Variable) e).type.calculateSize();
                ((Variable) e).address = curAddress;
                curAddress += memSize;
            } else if (e instanceof Type) {
                // make sure size of type is known
                ((Type) e).calculateSize();
            }
        }
        return curAddress;
    }

    private void writeCopyLoop() {
        // current amount copied kept in r2
        this.writeInstruction("mov", "r2", "#0");
        // create labels for loop
        String loopLabel = ".L" + this.labelCounter;
        this.labelCounter++;
        String endLabel = ".L" + this.labelCounter;
        this.labelCounter++;
        // loop begins
        this.writeLabel(loopLabel + ":");
        this.writeInstruction("cmp", "r2", "r3");
        this.writeInstruction("beq", endLabel);
        // r4 used for storing data temporarily
        this.writeInstruction("ldr", "r4", "[r1]");
        this.writeInstruction("str", "r4", "[r0]");
        // increment r0, r1, r2
        this.writeInstruction("add", "r0", "r0", "#4");
        this.writeInstruction("add", "r1", "r1", "#4");
        this.writeInstruction("add", "r2", "r2", "#4");
        this.writeInstruction("b", loopLabel);
        this.writeLabel(endLabel + ":");
    }
}
