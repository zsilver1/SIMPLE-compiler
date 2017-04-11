/*
* Zach Silver
* zsilver1@jhu.edu
 */

/**
 * A ParserObserver that outputs the parser's CST to stdout.
 */
public class TerminalOutputObserver implements ParserObserver {
    private StringBuilder spaces = new StringBuilder();
    private StringBuilder sb = new StringBuilder();

    @Override
    public void notifyStart(String nonTerminal) {
        // prints name of non-terminal
        this.sb.append(this.spaces.toString());
        this.sb.append(nonTerminal);
        this.sb.append('\n');
        // indents
        this.spaces.append("  ");
    }

    @Override
    public void notifyEnd() {
        // deletes tab from tab builder
        this.spaces.delete(this.spaces.length() - 2, this.spaces.length());
    }

    @Override
    public void match(Token t) {
        this.sb.append(this.spaces.toString());
        this.sb.append(t.toString());
        this.sb.append('\n');
    }

    @Override
    public void output() {
        System.out.print(this.sb.toString());
    }
}
