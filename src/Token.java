/*
 * Zach Silver
 * zsilver1@jhu.edu
 */

/**
 * Class that represents a single SIMPLE token.
 */
public class Token {

    /**
     * The name of an identifier string.
     */
    public String str;

    /**
     * The value of an integer string.
     */
    public int val;

    /**
     * The kind of the token.
     */
    public String kind; // ex: identifier, number, keyword, operator etc.

    /**
     * The starting position of the token.
     */
    public int startPos;

    /**
     * The ending position of the token.
     */
    public int endPos; // track as offset in input stream

    /**
     * Creates a new token.
     * @param kind the type of token
     * @param start the starting index
     * @param end the ending index
     */
    public Token(String kind, int start, int end) {
        this.kind = kind;
        this.startPos = start;
        this.endPos = end;
    }

    /**
     * Creates a string representation of a token.
     * @return string representing token
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.kind);
        if ("identifier".equals(this.kind)) {
            sb.append("<");
            sb.append(this.str);
            sb.append(">");
        } else if ("integer".equals(this.kind)) {
            sb.append("<");
            sb.append(this.val);
            sb.append(">");
        }
        sb.append("@(");
        sb.append(this.startPos);
        sb.append(", ");
        sb.append(this.endPos);
        sb.append(")");
        return sb.toString();
    }
}
