/*
 * Zach Silver
 * zsilver1@jhu.edu
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The Scanner class is used for lexical analysis.
 */
public class Scanner {
    /**
     * The array of keywords.
     */
    public static final String[] KEYWORD_ARRAY = {
        "PROGRAM",
        "BEGIN",
        "END",
        "CONST",
        "TYPE",
        "VAR",
        "ARRAY",
        "OF",
        "RECORD",
        "END",
        "DIV",
        "MOD",
        "IF",
        "THEN",
        "ELSE",
        "REPEAT",
        "UNTIL",
        "WHILE",
        "DO",
        "WRITE",
        "READ",
    };

    /**
     * HashSet of keywords for easy identification.
     */
    public static final HashSet<String> KEYWORDS =
            new HashSet<String>(Arrays.asList(KEYWORD_ARRAY));

    /**
     * The array of symbols.
     */
    public static final Character[] SYMBOL_ARRAY = {
        ';',
        '.',
        '=',
        ':',
        '+',
        '-',
        '*',
        '(',
        ')',
        '#',
        '<',
        '>',
        ',',
        '[',
        ']',
    };

    /**
     * HashSet of symbols for easy identification.
     */
    public static final HashSet<Character> SYMBOLS =
            new HashSet<Character>(Arrays.asList(SYMBOL_ARRAY));

    /**
     * The current line number of the program.
     */
    public int lineNumber = 1;

    private String src;
    private int position;
    private boolean hasBeenAccessed;
    private boolean insideComment;
    private ArrayList<Token> tokenList = new ArrayList<>();
    private  HashSet<String> keywords = new HashSet<>();
    private int commentLevel;

    /**
     * Create a scanner class.
     * @param src source code as string
     */
    public Scanner(String src) {
        this.src = src;
    }

    /**
     * Returns the next token from the source string.
     * @return the next token
     */
    public Token next() {
        this.hasBeenAccessed = true;
        char c;
        Token t;
        while (this.position < this.src.length()) {
            c = this.src.charAt(this.position);
            // if statements to check first character of possible token
            // NOTE: '\013' is vertical tab literal
            if (this.checkForWhitespace(c)) {
                this.position++;
            } else if (Character.isLetter(c)) {
                t = this.processLetter(c);
                return t;
            } else if (Character.isDigit(c)) {
                t = this.processDigit(c);
                return t;
            } else if (c == '(' && this.src.charAt(this.position + 1) == '*') {
                // check if we are entering a comment
                this.commentLevel++;
                // skip the opening comment token
                this.position += 2;
                this.processComment();
            } else if (this.isValidSymbol(c)) {
                t = this.processSymbol(c);
                return t;
            } else {
                throw new UnsupportedOperationException(
                        "unrecognized symbol at " + this.position);
            }
        }
        t = new Token("eof", this.position, this.position);
        return t;
    }

    /**
     * Returns the list of all tokens from the source string.
     * @return the list of tokens
     */
    public ArrayList<Token> all() {
        if (this.hasBeenAccessed) {
            throw new UnsupportedOperationException(
                    "cannot call all() after next()");
        }
        // get the first token
        Token t = this.next();

        do {
            this.tokenList.add(t);
            t = this.next();
        } while (!this.tokenList.get(
                this.tokenList.size() - 1).kind.equals("eof"));
        return this.tokenList;
    }

    private boolean isValidSymbol(char c) {
        return SYMBOLS.contains(c);
    }

    private Token processSymbol(char c) {
        Token t;
        int startPosition = this.position;
        this.position++;
        // check if the symbol is one of the possible two character symbols
        if (c == ':' && this.src.charAt(this.position) == '=') {
            t = new Token(":=", startPosition, this.position);
            this.position++;
        } else if (c == '<' && this.src.charAt(this.position) == '=') {
            t = new Token("<=", startPosition, this.position);
            this.position++;
        } else if (c == '>' && this.src.charAt(this.position) == '=') {
            t = new Token(">=", startPosition, this.position);
            this.position++;
        } else {
            t = new Token(Character.toString(c),
                    startPosition, this.position - 1);
        }
        return t;
    }

    private void processComment() {
        // we track the comment level to achieve nested comments
        while (this.commentLevel > 0) {
            // check for eof
            if (this.position >= this.src.length()) {
                return;
            }

            if (this.checkForOpeningComment()) {
                this.commentLevel++;
            } else if (this.checkForClosingComment()) {
                this.commentLevel--;
            }
            this.position++;
        }
    }

    private boolean checkForOpeningComment() {
        char c = this.src.charAt(this.position);
        if (c == '(' && this.src.charAt(this.position + 1) == '*') {
            this.position++;
            return true;
        }
        return false;
    }

    private boolean checkForClosingComment() {
        char c = this.src.charAt(this.position);
        if (c == '*' && this.src.charAt(this.position + 1) == ')') {
            this.position++;
            return true;
        }
        return false;
    }

    // processes a token starting with a letter
    private Token processLetter(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        // set the starting position of the token
        int startPosition = this.position;
        this.position++;
        c = this.src.charAt(this.position);
        while (Character.isLetterOrDigit(c)) {
            sb.append(c);
            this.position++;
            c = this.src.charAt(this.position);
        }
        String tokenString = sb.toString();
        Token t;
        if (KEYWORDS.contains(tokenString)) {
            // create a keyword token
            t = new Token(tokenString, startPosition, this.position - 1);
        } else {
            // create an identifier token
            t = new Token("identifier", startPosition, this.position - 1);
            t.str = tokenString;
        }
        return t;
    }

    // processes a token starting with a digit
    private Token processDigit(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        // set the starting position of the token
        int startPosition = this.position;
        this.position++;
        c = this.src.charAt(this.position);
        while (Character.isDigit(c)) {
            sb.append(c);
            this.position++;
            c = this.src.charAt(this.position);
        }
        Token t = new Token("integer", startPosition, this.position - 1);
        try {
            t.val = java.lang.Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            throw new UnsupportedOperationException("invalid integer at line " + this.lineNumber);
        }
        return t;
    }

    // Check for whitespace, and deal with line number
    private boolean checkForWhitespace(char c) {
        if (c == '\013') {
            throw new UnsupportedOperationException(
                    "Illegal character at " + this.position);
        }

        if (Character.isWhitespace(c)) {
            if (c == '\n') {
                this.lineNumber++;
            }
            return true;
        }
        return false;
    }
}
