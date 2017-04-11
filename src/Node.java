/*
Zach Silver
zsilver1@jhu.edu
 */

import java.lang.reflect.Field;

/**
 * An interface that is implemented by all nodes of the abstract syntax tree.
 */
public abstract class Node {

    /**
     * Get string representation for debugging.
     * @return string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().toString());
        sb.append('\n');
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            sb.append("  ");
            sb.append(field.getName());
            sb.append(": ");
            try {
                if (field.get(this) != null) {
                    sb.append(field.get(this).toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Accept an ASTVisitor object.
     * @param a ASTVisitor
     */
    public abstract void accept(ASTVisitor a);

    /**
     * Accept an interpreter.
     * @param i interpreter
     */
    public abstract void accept(Interpreter i);
}
