/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A class representing a program scope.
 */
public class Scope {
    /**
     * The outer scope.
     */
    public Scope outer;

    private HashMap<String, Entry> map = new HashMap<String, Entry>();

    private HashMap<String, Box> environment = new HashMap<String, Box>();

    /**
     * Create a scope.
     * @param outerScope scope's outer scope
     */
    public Scope(Scope outerScope) {
        this.outer = outerScope;
    }

    /**
     * Insert a value into the scope.
     * @param k name of the value.
     * @param v value.
     */
    public void insert(String k, Entry v) {
        this.map.put(k, v);
    }

    /**
     * Gets the hash map that represents the scope.
     * @return hash map
     */
    public HashMap<String, Entry> getHashMap() {
        return this.map;
    }

    /**
     * Find a value in the scope or outer scopes.
     * @param k the name of the value to find
     * @return the found value, or null if does not exist
     */
    public Entry find(String k) {
        if (this.map.containsKey(k)) {
            return this.map.get(k);
        }
        if (this.outer != null) {
            return this.outer.find(k);
        }
        return null;
    }

    /**
     * If a key is local, get it's value.
     * @param k key
     * @return value
     */
    public Entry findLocal(String k) {
        return this.map.get(k);
    }

    /**
     * Checks if value exists in current scope.
     * @param k Value's name
     * @return true if value exists in scope, false otherwise
     */
    public boolean local(String k) {
        return this.map.containsKey(k);
    }

    /**
     * Sorts the keys of the map.
     * @return ArrayList of sorted keys
     */
    public ArrayList<String> sortedKeys() {
        ArrayList<String> sorted = new ArrayList<String>(this.map.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Finds a key in the map given the value.
     * @param e value
     * @return key
     */
    public String findVal(Entry e) {
        for (String s : this.map.keySet()) {
            if (e.equals(this.map.get(s))) {
                return s;
            }
        }
        return "";
    }

    /**
     * Creates a string representation of scope.
     * @return string
     */
    public String toString() {
        return this.map.toString();
    }

    /**
     * Creates an environment from the symbol table.
     * @return environment map
     */
    public HashMap<String, Box> getEnvironment() {
        for (String k : this.map.keySet()) {
            Entry e = this.map.get(k);
            if (e instanceof Variable) {
                Variable v = (Variable) e;
                if (v.getType() == Integer.getInstance()) {
                    this.environment.put(k, new IntegerBox());
                } else if (v.getType() instanceof Array) {
                    this.environment.put(
                            k, this.getArrayBox((Array) v.getType()));
                } else {
                    this.environment.put(
                            k, this.getRecordBox((Record) v.getType()));
                }
            }
        }
        return this.environment;
    }


    private ArrayBox getArrayBox(Array a) {
        ArrayBox aBox = new ArrayBox(a.length);
        if (a.elementType == Integer.getInstance()) {
            for (int i = 0; i < a.length; i++) {
                aBox.set(i, new IntegerBox());
            }
        } else if (a.elementType instanceof Array) {
            for (int i = 0; i < a.length; i++) {
                aBox.set(i, this.getArrayBox((Array) a.elementType));
            }
        } else {
            for (int i = 0; i < a.length; i++) {
                aBox.set(i, this.getRecordBox((Record) a.elementType));
            }
        }
        return aBox;
    }

    private RecordBox getRecordBox(Record r) {
        RecordBox rBox = new RecordBox();
        for (Entry e : r.scope.map.values()) {
            if (e instanceof Variable) {
                Variable v = (Variable) e;
                if (v.getType() == Integer.getInstance()) {
                    rBox.set(v, new IntegerBox());
                } else if (v.getType() instanceof Array) {
                    rBox.set(v, this.getArrayBox((Array) v.getType()));
                } else {
                    rBox.set(v, this.getRecordBox((Record) v.getType()));
                }
            }
        }
        return rBox;
    }
}
