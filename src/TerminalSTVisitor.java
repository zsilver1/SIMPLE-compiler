/*
Zach Silver
zsilver1@jhu.edu
*/

import java.util.ArrayList;

/**
 * A class that takes a scope object and outputs it to the terminal formatted
 * correctly.
 */
public class TerminalSTVisitor implements STVisitor {
    /**
     * Build output.
     */
    public StringBuilder sb = new StringBuilder();

    /**
     * Keep track of spaces.
     */
    public StringBuilder spaces = new StringBuilder();


    @Override
    public void visit(Scope s) {
        ArrayList<String> a = s.sortedKeys();
        this.write("SCOPE BEGIN");
        for (String key : a) {
            Entry e = s.findLocal(key);
            this.indent();
            this.write(key + " =>");
            if (e instanceof Constant) {
                this.visit((Constant) e);
            } else if (e instanceof Variable) {
                this.visit((Variable) e);
            } else if (e instanceof Record) {
                this.visit((Record) e);
            } else if (e instanceof Array) {
                this.visit((Array) e);
            } else {
                this.visit((Integer) e);
            }
            this.dedent();
        }
        this.write("END SCOPE");
    }

    @Override
    public void visit(Array a) {
        this.indent();
        this.write("ARRAY BEGIN");
        this.indent();
        this.write("type:");
        this.visit(a.elementType);
        this.write("length:");
        this.indent();
        this.write(java.lang.Integer.toString(a.length));
        this.dedent();
        this.dedent();
        this.write("END ARRAY");
        this.dedent();
    }

    @Override
    public void visit(Record r) {
        this.indent();
        this.write("RECORD BEGIN");
        this.indent();
        this.visit(r.scope);
        this.dedent();
        this.write("END RECORD");
        this.dedent();
    }

    @Override
    public void visit(Integer i) {
        this.indent();
        this.write("INTEGER");
        this.dedent();
    }

    @Override
    public void visit(Constant c) {
        this.indent();
        this.write("CONST BEGIN");
        this.indent();
        this.write("type:");
        this.indent();
        this.write(c.type.toString());
        this.dedent();
        this.write("value:");
        this.indent();
        this.write(c.value.toString());
        this.dedent();
        this.dedent();
        this.write("END CONST");
        this.dedent();
    }

    @Override
    public void visit(Variable v) {
        this.indent();
        this.write("VAR BEGIN");
        this.indent();
        this.write("type:");
        this.visit(v.getType());
        this.dedent();
        this.write("END VAR");
        this.dedent();
    }

    private void visit(Type t) {
        if (t instanceof Integer) {
            this.visit((Integer) t);
        } else if (t instanceof Record) {
            this.visit((Record) t);
        } else if (t instanceof Array) {
            this.visit((Array) t);
        }
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
