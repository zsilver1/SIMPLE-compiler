/*
Zach Silver
zsilver1@jhu.edu
*/

/**
 * An expression that is a location in memory.
 */
public abstract class Location extends Expression {
    /**
     * Clone a location.
     * @return clone
     */
    public abstract Location cloneExp();
}
