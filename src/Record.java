/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

/**
 * A record type, which contains its own scope.
 */
public class Record extends Type {
    /**
     * The scope of the record.
     */
    public Scope scope;

    /**
     * Create a new record.
     * @param s scope of the record
     */
    public Record(Scope s) {
        this.scope = s;
    }

    /**
     * Creates string representation of record.
     * @return string
     */
    public String toString() {
        return "RECORD";
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public int calculateSize() {
        if (this.size != 0) {
            return this.size;
        }
        for (Entry e : this.scope.getHashMap().values()) {
            // set each record variable's address
            ((Variable) e).address = this.size;
            this.size += e.getType().calculateSize();
        }
        return this.size;
    }
}
