/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

/**
 * A singleton integer type.
 */
public final class Integer extends Type {
    /**
     * The type is always integer, included for convenience.
     */
    private static Integer instance;

    private Integer() {
        this.size = 4;
    }

    /**
     * Get the single integer instance.
     * @return integer instance
     */
    public static Integer getInstance() {
        if (instance == null) {
            instance = new Integer();
        }
        return instance;
    }

    /**
     * Creates sting representation of integer.
     * @return string
     */
    public String toString() {
        return "INTEGER";
    }

    @Override
    public Type getType() {
        return instance;
    }

    @Override
    public int calculateSize() {
        return this.size;
    }
}
