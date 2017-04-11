/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileWriter;

/**
 * This class is used to run the main simple compiler.
 */
public final class Sc {
    private Sc() {
        // Checkstyle makes me do this.
    }

    /**
     * Runs the compiler.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            parseArguments(args);
        } catch (UnsupportedOperationException
                | IOException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void parseArguments(String[] args)
            throws UnsupportedOperationException,
            IOException {
        ArgumentParser a = new ArgumentParser(args);
        a.parse();
        if (a.hasInputFile) {
            // ensure input file is .sim file
            checkFileName(a.inputFilename);
        }
        String src;
        char op;
        if (a.numArguments == 0) {
            src = readInputIntoString(System.in);
            System.out.print(runCodeGenerator(src));
        } else if (a.numArguments == 1) {
            // one argument
            if (!a.hasInputFile) {
                src = readInputIntoString(System.in);
                performOperation(a.firstFlag, src, false);
            } else {
                // generate code
                FileInputStream f = new FileInputStream(a.inputFilename);
                src = readInputIntoString(f);
                String code = runCodeGenerator(src);
                String outfileName = a.inputFilename.substring(
                        0, a.inputFilename.length() - 2);
                //File outFile = new File(outfileName);
                FileWriter fw = new FileWriter(outfileName);
                fw.write(code);
                fw.close();
            }

        } else if (a.numArguments == 2) {
            // two arguments
            if (!a.hasInputFile) {
                src = readInputIntoString(System.in);
                performOperation(a.firstFlag, src, a.isGraphical);
            } else {
                FileInputStream f = new FileInputStream(a.inputFilename);
                src = readInputIntoString(f);
                performOperation(a.firstFlag, src, a.isGraphical);
            }
        } else if (a.numArguments == 3) {
            FileInputStream f = new FileInputStream(a.inputFilename);
            src = readInputIntoString(f);
            performOperation(a.firstFlag, src, a.isGraphical);
        } else {
            // wrong number of arguments
            throw new UnsupportedOperationException("wrong num of arguments");
        }
    }

    // reads the given input stream into a string
    private static String readInputIntoString(InputStream in)
            throws IOException {
        BufferedInputStream i = new BufferedInputStream(in);
        StringBuilder sb = new StringBuilder();
        int c = i.read();
        while (c != -1) {
            sb.append((char) c);
            c = i.read();
        }
        i.close();
        return sb.toString();
    }

    // performs the specified operation on the given source string
    private static void performOperation(char op,
                                         String src,
                                         boolean graphical) {
        switch (op) {
            case 's':
                runScanner(src);
                break;
            case 'c':
                runConcreteSyntaxTree(src, graphical);
                break;
            case 't':
                runSymbolTableOutput(src, graphical);
                break;
            case 'a':
                runAbstractSyntaxTree(src, graphical);
                break;
            case 'i':
                runInterpreter(src, graphical);
                break;
            default:
                throw new UnsupportedOperationException("invalid operation");
        }
    }

    private static void runScanner(String src) {
        Scanner s = new Scanner(src);
        Token t;
        do {
            t = s.next();
            System.out.println(t);
        } while (!"eof".equals(t.kind));
    }

    private static void runConcreteSyntaxTree(String src, boolean graphical) {
        Scanner s = new Scanner(src);
        Parser p = new Parser(s, graphical);
        p.parserOutput = true;
        p.parse();

    }

    private static void runSymbolTableOutput(String src, boolean graphical) {
        Scanner s = new Scanner(src);
        Parser p = new Parser(s, graphical);
        p.symbolTableOutput = true;
        p.parse();
    }

    private static void runAbstractSyntaxTree(String src, boolean graphical) {
        Scanner s = new Scanner(src);
        Parser p = new Parser(s, graphical);
        p.astOutput = true;
        p.parse();
    }

    private static void runInterpreter(String src, boolean graphical) {
        if (graphical) {
            throw new UnsupportedOperationException(
                    "invalid flag for interpreter");
        }
        Scanner s = new Scanner(src);
        Parser p = new Parser(s, false);
        p.runInterpreter = true;
        p.parse();
    }

    // returns the assembly code as a string to write to the file
    private static String runCodeGenerator(String src) {
        Scanner s = new Scanner(src);
        Parser p = new Parser(s, false);
        p.runCodeGenerator = true;
        return p.parse();
    }

    private static void checkFileName(String fileName) {
        if (!(fileName.endsWith(".sim"))) {
            throw new UnsupportedOperationException(
                    "invalid input file, must be .sim file");
        }
    }

    private static void runOther() {
        throw new UnsupportedOperationException("functionality not supported");
    }
}
