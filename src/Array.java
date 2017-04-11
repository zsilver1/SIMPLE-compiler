/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

/**
 * An array type, which contains the element type and the array length.
 */
public class Array extends Type {
    /**
     * The type of the elements in the array.
     */
    public Type elementType;

    /**
     * The length of the array.
     */
    public int length;

    /**
     * Create a new Array.
     * @param elemType the type of the elements
     * @param len the length of the array
     */
    public Array(Type elemType, int len) {
        this.elementType = elemType;
        this.length = len;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return "ARRAY";
    }

    @Override
    public int calculateSize() {
        if (this.size != 0) {
            return this.size;
        }
        this.size = this.elementType.calculateSize() * this.length;
        return this.size;
    }
}