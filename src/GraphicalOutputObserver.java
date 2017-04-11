/*
* Zach Silver
* zsilver1@jhu.edu
 */

import java.util.Stack;

/**
 * Creates a graphical tree with DOT syntax.
 */
public class GraphicalOutputObserver implements ParserObserver {
    private Stack<java.lang.Integer> s = new Stack<java.lang.Integer>();
    private int nodeName;
    private StringBuilder sb = new StringBuilder();

    /**
     * Creates graphical observer.
     */
    public GraphicalOutputObserver() {
        this.sb.append("digraph G {\n");
    }

    @Override
    public void notifyStart(String nonTerminal) {
        this.sb.append(this.nodeName);
        this.sb.append(" [label=");
        this.sb.append(nonTerminal);
        this.sb.append(",shape=box];\n");
        if (this.s.empty()) {
            this.s.push(this.nodeName);
        } else {
            this.sb.append(this.s.peek());
            this.sb.append(" -> ");
            this.sb.append(this.nodeName);
            this.sb.append(";\n");
            this.s.push(this.nodeName);
        }
        this.nodeName++;
    }

    @Override
    public void notifyEnd() {
        this.s.pop();
        if (this.s.empty()) {
            this.sb.append("\n}");
        }
    }

    @Override
    public void match(Token t) {
        String str;
        if ("identifier".equals(t.kind)) {
            str = t.str;
        } else if ("integer".equals(t.kind)) {
            str = java.lang.Integer.toString(t.val);
        } else {
            str = t.kind;
        }
        this.sb.append(this.nodeName);
        this.sb.append(" [label=");
        this.sb.append("\"");
        this.sb.append(str);
        this.sb.append("\"");
        this.sb.append(",shape=diamond];");
        this.sb.append(this.s.peek());
        this.sb.append(" -> ");
        this.sb.append(this.nodeName);
        this.sb.append(";\n");
        this.nodeName++;
    }

    @Override
    public void output() {
        System.out.print(this.sb.toString());
    }
}
