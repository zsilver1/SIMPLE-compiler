/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * Box representing an integer.
 */
public class IntegerBox extends Box {
    private int value;

    /**
     * Get the value of the integer box.
     * @return value
     */
    public int get() {
        return this.value;
    }

    /**
     * Set the value of the integer box.
     * @param val value
     */
    public void set(int val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return java.lang.Integer.toString(this.value);
    }

    @Override
    public Box cloneBox() {
        IntegerBox i = new IntegerBox();
        i.set(this.value);
        return i;
    }
}
