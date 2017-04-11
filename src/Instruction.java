/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * The base class of all instructions in the language.
 */
public abstract class Instruction extends Node {
    /**
     * The next instruction.
     */
    public Instruction next;

    /**
     * Accept an interpreter.
     * @param i interpreter
     */
    public void accept(Interpreter i) {
        i.visit(this);
    }

    /**
     * Accept a code generator.
     * @param cg code generator
     */
    public abstract void accept(CodeGenerator cg);
}
