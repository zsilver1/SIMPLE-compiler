import java.util.HashMap;
import java.util.Set;

/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * A box representing a record.
 */
public class RecordBox extends Box {
    private HashMap<Variable, Box> map = new HashMap<Variable, Box>();

    /**
     * Get a value from the record box.
     * @param v variable index
     * @return value
     */
    public Box get(Variable v) {
        return this.map.get(v);
    }

    /**
     * Set a value in the record box.
     * @param v variable index
     * @param value value
     */
    public void set(Variable v, Box value) {
        this.map.put(v, value);
    }

    /**
     * Get the set of variable keys.
     * @return set
     */
    public Set<Variable> getKeySet() {
        return this.map.keySet();
    }

    /**
     * Set record box to other record box.
     * @param other record box to copy
     */
    public void assignToRecord(RecordBox other) {
        this.map = new HashMap<Variable, Box>();
        for (Variable v : other.getKeySet()) {
            this.map.put(v, other.get(v));
        }
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    @Override
    public Box cloneBox() {
        RecordBox r = new RecordBox();
        for (Variable v : this.map.keySet()) {
            r.set(v, this.get(v).cloneBox());
        }
        return r;
    }
}
