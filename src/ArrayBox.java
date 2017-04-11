/*
Zach Silver
zsilver1@jhu.edu
*/
import java.util.Arrays;

/**
 * Box representing an array.
 */
public class ArrayBox extends Box {
    private Box[] array;

    /**
     * Create a new array box.
     * @param length length of the array
     */
    public ArrayBox(int length) {
        this.array = new Box[length];
    }

    /**
     * Get value from array.
     * @param index index of array
     * @return box value
     */
    public Box get(int index) {
        if (index < 0 || index >= this.array.length) {
            throw new UnsupportedOperationException(
                    "array index out of bounds, " + index
                            + " must be in range [0,"
                            + (this.array.length - 1) + "]");
        }
        return this.array[index];
    }

    /**
     * Set the value of array.
     * @param index index of array
     * @param value new value box
     */
    public void set(int index, Box value) {
        if (index < 0 || index >= this.array.length) {
            throw new UnsupportedOperationException(
                    "array index out of bounds, " + index
                            + " must be in range [0,"
                            + (this.array.length - 1) + "]");
        }
        this.array[index] = value;
    }

    /**
     * Get the length of the array.
     * @return length
     */
    public int getLength() {
        return this.array.length;
    }

    /**
     * Set array box to other array box.
     * @param other the array box to copy
     */
    public void assignToArray(ArrayBox other) {
        this.array = new Box[other.getLength()];
        for (int i = 0; i < other.getLength(); i++) {
            this.array[i] = other.get(i).cloneBox();
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(this.array);
    }

    @Override
    public Box cloneBox() {
        ArrayBox a = new ArrayBox(this.array.length);
        for (int i = 0; i < this.array.length; i++) {
            a.set(i, this.get(i).cloneBox());
        }
        return a;
    }
}
