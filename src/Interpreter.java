import java.util.HashMap;
import java.util.Stack;

/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * An interpreter that runs the simple program.
 */
public class Interpreter {

    private Stack<Box> st = new Stack<Box>();
    private Instruction curInstruction;
    private HashMap<String, Box> environment;
    private Scope scope;
    private java.util.Scanner scanner = new java.util.Scanner(System.in);

    /**
     * Create a new interpreter.
     * @param i first instruction of the program
     * @param environment the environment of the program
     * @param s the scope of the program
     */
    public Interpreter(Instruction i,
                       HashMap<String, Box> environment,
                       Scope s) {
        this.curInstruction = i;
        this.environment = environment;
        this.scope = s;
    }

    /**
     * Run the interpreter.
     */
    public void run() {
        while (this.curInstruction != null) {
            this.curInstruction.accept(this);
            //after every instruction
            this.checkIfStackEmpty();
            this.curInstruction = this.curInstruction.next;
        }
    }

    /**
     * Visit a binary expression.
     * @param b binary expression
     */
    public void visit(BinaryExpression b) {
        IntegerBox result = new IntegerBox();
        b.left.accept(this);
        b.right.accept(this);
        int r = ((IntegerBox) this.st.pop()).get();
        int l = ((IntegerBox) this.st.pop()).get();
        switch (b.operator) {
            case "+":
                result.set(l + r);
                break;
            case "-":
                result.set(l - r);
                break;
            case "*":
                result.set(l * r);
                break;
            case "DIV":
                if (r != 0) {
                    result.set(l / r);
                } else {
                    throw new UnsupportedOperationException("divide by zero");
                }
                break;
            case "MOD":
                if (r != 0) {
                    result.set(l % r);
                } else {
                    throw new UnsupportedOperationException("divide by zero");
                }
                break;
            default:
                break;
        }
        this.st.push(result);
    }


    /**
     * Visit a number expression.
     * @param n number expression
     */
    public void visit(NumberExpression n) {
        IntegerBox i = new IntegerBox();
        i.set((java.lang.Integer) n.constant.value);
        this.st.push(i);
    }

    /**
     * Visit a variable location.
     * @param v variable location
     */
    public void visit(VariableLocation v) {
        String name = this.scope.findVal(v.variable);
        Box b = this.environment.get(name);
        this.st.push(b);
    }

    /**
     * Visit an index location.
     * @param i index location
     */
    public void visit(IndexLocation i) {
        i.location.accept(this);
        i.expression.accept(this);
        IntegerBox index = ((IntegerBox) this.st.pop());
        ArrayBox a = (ArrayBox) this.st.pop();
        Box b = a.get(index.get());
        this.st.push(b);
    }

    /**
     * Visit a field location.
     * @param f field location
     */
    public void visit(FieldLocation f) {
        f.location.accept(this);
        RecordBox r = (RecordBox) this.st.pop();
        Box b = r.get(f.variable.variable);
        this.st.push(b);
    }

    /**
     * Visit an instruction of the AST.
     * @param instruction instruction to visit
     */
    public void visit(Instruction instruction) {
        if (instruction instanceof Assign) {
            this.visit((Assign) instruction);
        } else if (instruction instanceof If) {
            this.visit((If) instruction);
        } else if (instruction instanceof Read) {
            this.visit((Read) instruction);
        } else if (instruction instanceof Write) {
            this.visit((Write) instruction);
        } else if (instruction instanceof Repeat) {
            this.visit((Repeat) instruction);
        }
    }

    private void visit(Assign a) {
        a.location.accept(this);
        a.expression.accept(this);
        Box exp = this.st.pop();
        if (exp instanceof ArrayBox) {
            ArrayBox loc = (ArrayBox) this.st.pop();
            loc.assignToArray((ArrayBox) exp);
        } else if (exp instanceof RecordBox) {
            RecordBox loc = (RecordBox) this.st.pop();
            loc.assignToRecord((RecordBox) exp);
        } else {
            IntegerBox loc = (IntegerBox) this.st.pop();
            loc.set(((IntegerBox) exp).get());
        }
    }

    private void visit(If i) {
        i.condition.accept(this);
        Instruction cur;
        int condResult = ((IntegerBox) this.st.pop()).get();
        if (condResult == 1) {
            cur = i.instructionTrue;
            while (cur != null) {
                cur.accept(this);
                cur = cur.next;
            }
        } else {
            if (i.instructionFalse != null) {
                cur = i.instructionFalse;
                while (cur != null) {
                    cur.accept(this);
                    cur = cur.next;
                }
            }
        }
    }

    private void visit(Read r) {
        r.location.accept(this);
        IntegerBox i = (IntegerBox) this.st.pop();
        try {
            i.set(this.scanner.nextInt());
        } catch (java.util.NoSuchElementException e) {
            throw new UnsupportedOperationException("invalid read");
        }
    }

    private void visit(Write w) {
        w.expression.accept(this);
        IntegerBox i = (IntegerBox) this.st.pop();
        System.out.println(i.get());
    }

    private void visit(Repeat r) {
        Instruction cur;
        int cond;
        do {
            cur = r.instruction;
            while (cur != null) {
                cur.accept(this);
                cur = cur.next;
            }
            r.condition.accept(this);
            cond = ((IntegerBox) this.st.pop()).get();
        } while (cond == 0);
    }

    /**
     * Visit a condition.
     * @param c condition
     */
    public void visit(Condition c) {
        IntegerBox result = new IntegerBox();
        c.left.accept(this);
        c.right.accept(this);
        String rel = c.relation;
        int r = ((IntegerBox) this.st.pop()).get();
        int l = ((IntegerBox) this.st.pop()).get();
        if ("=".equals(rel) && r == l) {
            result.set(1);
        } else if ("#".equals(rel)) {
            result = this.checkNotEqual(r, l);
        } else if ("<=".equals(rel) && l <= r) {
            result.set(1);
        } else if (">=".equals(rel) && l >= r) {
            result.set(1);
        } else if ("<".equals(rel)) {
            result = this.checkLessThan(r, l);
        } else if (">".equals(rel)) {
            result = this.checkGreaterThan(r, l);
        } else {
            result.set(0);
        }
        this.st.push(result);
    }

    private IntegerBox checkLessThan(int r, int l) {
        IntegerBox result = new IntegerBox();
        if (l < r) {
            result.set(1);
        }
        return result;
    }

    private IntegerBox checkGreaterThan(int r, int l) {
        IntegerBox result = new IntegerBox();
        if (l > r) {
            result.set(1);
        }
        return result;
    }

    private IntegerBox checkNotEqual(int r, int l) {
        IntegerBox result = new IntegerBox();
        if (l != r) {
            result.set(1);
        }
        return result;
    }


    private void checkIfStackEmpty() {
        if (!(this.st.empty())) {
            throw new UnsupportedOperationException(
                    "stack not empty!!!\n" + this.st.toString());
        }
    }
}
