// Zach Silver
// zsilver1@jhu.edu

/**
 * A class that represents an entry in the compiler's symbol table.
 */
public abstract class Entry {

    /**
     * Get the type of the given entry, some entries are themselves types
     * in which case this function will return "this".
     * @return the type of the entry
     */
    public abstract Type getType();
}
