/*
Zach Silver
zsilver1@jhu.edu
*/

import java.util.ArrayList;

/**
 * A symbol table visitor that outputs dot format.
 */
public class GraphicalSTVisitor implements STVisitor {
    /**
     * StringBuilder used for output.
     */
    public StringBuilder sb = new StringBuilder();

    /**
     * Create a GraphicalSTVisitor.
     */
    public GraphicalSTVisitor() {
        this.sb.append("strict digraph X {\n");
    }

    private void addToScope(Entry e, String key) {
        if (e instanceof Integer) {
            this.sb.append(key);
        }
        this.sb.append(e.hashCode());
        this.sb.append(" [label=\"");
        this.sb.append(key);
        this.sb.append("\", shape=box,color=white,fontcolor=black]\n");
    }

    @Override
    public void visit(Scope s) {
        this.sb.append("subgraph cluster_");
        this.sb.append(s.hashCode());
        this.sb.append("{\n");

        // create anchor node for scope
        this.sb.append("anchor");
        this.sb.append(s.hashCode());
        this.sb.append(" [label=\"\", style=invis]\n");
        ArrayList<String> a = s.sortedKeys();
        for (String key : a) {
            Entry e = s.findLocal(key);
            this.addToScope(e, key);
        }
        this.sb.append("}\n");
        for (String key : a) {
            Entry e = s.findLocal(key);
            if (e instanceof Constant) {
                this.visit((Constant) e);
            } else if (e instanceof Variable) {
                this.visit((Variable) e);
            } else if (e instanceof Record) {
                this.visit((Record) e);
                this.sb.append(e.hashCode());
                this.sb.append(" -> ");
                this.sb.append("type");
                this.sb.append(e.hashCode());
                this.sb.append('\n');
            } else if (e instanceof Array) {
                this.visit((Array) e);
                this.sb.append(e.hashCode());
                this.sb.append(" -> ");
                this.sb.append("type");
                this.sb.append(e.hashCode());
                this.sb.append('\n');
            } else {
                this.visit((Integer) e);
                this.sb.append(key);
                this.sb.append(e.hashCode());
                this.sb.append(" -> ");
                this.sb.append("type");
                this.sb.append(e.hashCode());
                this.sb.append('\n');
            }
        }
    }

    @Override
    public void visit(Array a) {
        // create array node
        this.sb.append("type");
        this.sb.append(a.hashCode());
        this.sb.append(" [label=\"Array length: ");
        this.sb.append(a.length);
        this.sb.append("\", shape=box,style=rounded]");
        this.sb.append('\n');

        // create type node
        this.visitType(a.elementType);

        this.sb.append("type");
        this.sb.append(a.hashCode());
        this.sb.append(" -> ");
        this.sb.append("type");
        this.sb.append(a.elementType.hashCode());
        this.sb.append('\n');
    }

    @Override
    public void visit(Record r) {
        // create record node
        this.sb.append('\n');
        this.sb.append("type");
        this.sb.append(r.hashCode());
        this.sb.append(" [label=Record,shape=box,style=rounded]\n");

        // create record's scope
        this.visit(r.scope);
        this.sb.append("type");
        this.sb.append(r.hashCode());
        this.sb.append(" -> ");
        this.sb.append("anchor");
        this.sb.append(r.scope.hashCode());
        this.sb.append('\n');
    }

    @Override
    public void visit(Integer i) {
        this.sb.append("type");
        this.sb.append(i.hashCode());
        this.sb.append(" [label=");
        this.sb.append(i.toString());
        this.sb.append(", shape=box,style=rounded]\n");
        this.sb.append('\n');
    }

    @Override
    public void visit(Constant c) {
        this.sb.append('\n');
        // Create value node
        this.sb.append(c.hashCode());
        this.sb.append(c.value);
        this.sb.append(" [label=");
        this.sb.append(c.value);
        this.sb.append(", shape=diamond]\n");

        this.sb.append(c.hashCode());
        this.sb.append(" -> ");
        this.sb.append(c.hashCode());
        this.sb.append(c.value);
        this.sb.append('\n');

        // Create type node
        this.visitType(c.type);

        this.sb.append(c.hashCode());
        this.sb.append(c.value);
        this.sb.append(" -> ");
        this.sb.append("type");
        this.sb.append(c.type.hashCode());
        this.sb.append('\n');

    }

    @Override
    public void visit(Variable v) {
        this.sb.append('\n');
        // Create variable node
        this.sb.append("var");
        this.sb.append(v.hashCode());
        this.sb.append(" [label=\"\", shape=circle]\n");

        this.sb.append(v.hashCode());
        this.sb.append(" -> ");
        this.sb.append("var");
        this.sb.append(v.hashCode());
        this.sb.append('\n');

        // Create type node
        this.visitType(v.type);

        this.sb.append("var");
        this.sb.append(v.hashCode());
        this.sb.append(" -> ");
        this.sb.append("type");
        this.sb.append(v.type.hashCode());
        this.sb.append('\n');
    }

    @Override
    public void output() {
        this.sb.append("}");
        System.out.print(this.sb.toString());
    }

    private void visitType(Type t) {
        if (t instanceof Integer) {
            this.visit((Integer) t);
        } else if (t instanceof Record) {
            this.visit((Record) t);
        } else if (t instanceof Array) {
            this.visit((Array) t);
        }
    }
}
