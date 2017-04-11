// Zach Silver
// zsilver1@jhu.edu

/**
 * Base class representing all types.
 */
public abstract class Type extends Entry {
    /**
     * The size of the type.
     */
    public int size;

    /**
     * Calculate the size of a type, set and return it.
     * @return size
     */
    public abstract int calculateSize();
}
