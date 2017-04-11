// Zach Silver
// zsilver1@jhu.edu

/**
 * Parses command line arguments array.
 */
public class ArgumentParser {
    /**
     * The first command line flag.
     */
    public char firstFlag;

    /**
     * The second command line flag.
     */
    public char secondFlag;

    /**
     * The input file name.
     */
    public String inputFilename;

    /**
     * Is there an input file specified.
     */
    public boolean hasInputFile;

    /**
     * Is the output graphical.
     */
    public boolean isGraphical;

    /**
     * The number of arguments.
     */
    public int numArguments;

    private String[] args;

    /**
     * Create an argument parser.
     * @param args arguments.
     */
    public ArgumentParser(String[] args) {
        this.numArguments = args.length;
        this.args = args;
    }

    /**
     * Parse arguments.
     */
    public void parse() {
        if (this.numArguments == 1) {
            this.handleOneArgument();
        } else if (this.numArguments == 2) {
            this.handleTwoArguments();
        } else if (this.numArguments == 3) {
            this.handleThreeArguments();
        }
    }

    private void handleOneArgument() {
        if (this.args[0].charAt(0) == '-') {
            this.firstFlag = this.args[0].charAt(1);
        } else {
            this.inputFilename = this.args[0];
            this.hasInputFile = true;
        }
    }

    private void handleTwoArguments() {
        this.firstFlag = this.args[0].charAt(1);
        if (this.args[1].charAt(0) == '-') {
            this.secondFlag = this.args[1].charAt(1);
            this.isGraphical = true;
        } else {
            this.inputFilename = this.args[1];
            this.hasInputFile = true;
        }

    }

    private void handleThreeArguments() {
        this.firstFlag = this.args[0].charAt(1);
        this.secondFlag = this.args[1].charAt(1);
        this.inputFilename = this.args[2];
        this.hasInputFile = true;
        this.isGraphical = true;
    }
}
