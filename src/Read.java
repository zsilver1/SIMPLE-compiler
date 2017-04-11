/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A class representing the "read" instruction.
 */
public class Read extends Instruction {
    /**
     * The location of the read in memory.
     */
    public Location location;

    /**
     * Create a new read instruction.
     * @param l location to read from
     */
    public Read(Location l) {
        this.location = l;
    }

    @Override
    public void accept(ASTVisitor a) {
        a.visit(this);
    }

    /**
     * Visit a code generator.
     * @param cg code generator
     */
    public void accept(CodeGenerator cg) {
        cg.visit(this);
    }

    @Override
    public String toString() {
        return "Read";
    }
}
