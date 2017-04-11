/*
* Zach Silver
* zsilver1@jhu.edu
 */


/**
 * This interface is used for creating observers for the parser.
 * These observers can be used to output the concrete syntax tree
 * in an number of ways.
 */
public interface ParserObserver {
    /**
     * Notifies the observer that we are starting a non-terminal.
     * @param nonTerminal the nonterminal token
     */
    void notifyStart(String nonTerminal);

    /**
     * Notifies the observer that we are ending a non-terminal.
     */
    void notifyEnd();

    /**
     * Match a specified token.
     * @param t the token to match
     */
    void match(Token t);

    /**
     * Outputs the observer to stdout.
     */
    void output();

}
