/*
 * Zach Silver.
 * zsilver1@jhu.edu
 */

import java.util.ArrayList;

/**
 * Parses the tokens into a concrete syntax tree.
 */
public class Parser {
    /**
     * True if parser should output.
     */
    public  boolean parserOutput;

    /**
     * True if symbol table should output.
     */
    public  boolean symbolTableOutput;

    /**
     * True if ast should output.
     */
    public boolean astOutput;

    /**
     * True if program interpreter should run.
     */
    public boolean runInterpreter;

    /**
     * True if code generator should run.
     */
    public boolean runCodeGenerator;


    private ASTVisitor astVisitor;
    private Scanner scanner;
    private Token currentToken;
    private Token prev;
    private ParserObserver parserObserver;
    private Scope curScope;
    private STVisitor stVisitor;



    /**
     * Creates a parser object.
     * @param s the scanner used for parsing
     * @param graphical whether or not to produce graphical output
     */
    public Parser(Scanner s, boolean graphical) {

        this.scanner = s;
        // Check what kind of output is needed
        if (graphical) {
            this.parserObserver = new GraphicalOutputObserver();
            this.stVisitor = new GraphicalSTVisitor();
            this.astVisitor = new GraphicalASTVisitor();
        } else {
            this.parserObserver = new TerminalOutputObserver();
            this.stVisitor = new TerminalSTVisitor();
            this.astVisitor = new TerminalASTVisitor();
        }

        Scope universe = new Scope(null);
        // Insert INTEGER type into universe scope
        universe.insert("INTEGER", Integer.getInstance());
        this.curScope = new Scope(universe);
    }

    /**
     * Parse the contents of the given scanner.
     * @return machine code when running code generator
     */
    public String parse() {
        // get the first token
        this.currentToken = this.scanner.next();
        Instruction i = this.program();
        //System.out.println(this.curScope.getEnvironment().toString());
        if (this.parserOutput) {
            this.parserObserver.output();
        } else if (this.symbolTableOutput) {
            this.stVisitor.visit(this.curScope);
            this.stVisitor.output();
        } else if (this.astOutput) {
            this.astVisitor.output();
        } else if (this.runInterpreter) {
            Interpreter interpreter = new Interpreter(
                    i, this.curScope.getEnvironment(), this.curScope);
            interpreter.run();
        } else if (this.runCodeGenerator) {
            // CHANGE CODE GENERATOR TYPE HERE
            CodeGenerator codeGenerator = new StupidCodeGenerator(
                    this.curScope, i);
            return codeGenerator.run();
        }
        // only return a string when generating code
        return "";
    }

    // gets the next token
    private void next() {
        this.prev = this.currentToken;
        this.currentToken = this.scanner.next();
    }

    // match a single string
    private Token match(String expectedKind) {
        if (expectedKind.equals(this.currentToken.kind)) {
            // increment prev and next
            this.parserObserver.match(this.currentToken);
            this.next();
            return this.prev;
        }
        throw this.parserError("incorrect token " + this.currentToken.kind);
    }

    // used to match a list of tokens
    private Token match(String[] expectedKinds) {
        for (String k : expectedKinds) {
            if (k.equals(this.currentToken.kind)) {
                // increment prev and next
                this.next();
                return this.prev;
            }
        }
        throw this.parserError("incorrect token " + this.currentToken.kind);
    }

    // NON-TERMINAL FUNCTIONS BEGIN HERE

    private Instruction program() {
        Instruction i = null;
        this.parserObserver.notifyStart("Program");
        this.match("PROGRAM");
        Token t = this.match("identifier");
        String programName = t.str;
        this.match(";");
        this.declarations();
        if ("BEGIN".equals(this.currentToken.kind)) {
            this.match("BEGIN");
            i = this.instructions();
            this.astVisitor.visitInstructions(
                    i, this.curScope);
        }
        this.match("END");
        t = this.match("identifier");
        if (!programName.equals(t.str)) {
            throw this.parserError("program name error");
        }
        this.match(".");
        this.parserObserver.notifyEnd();
        return i;
    }

    private void declarations() {
        this.parserObserver.notifyStart("Declarations");
        // look while there are declaration keywords
        while ("VAR".equals(this.currentToken.kind)
                || "CONST".equals(this.currentToken.kind)
                || "TYPE".equals(this.currentToken.kind)) {
            if ("VAR".equals(this.currentToken.kind)) {
                this.varDecl();
            } else if ("CONST".equals(this.currentToken.kind)) {
                this.constDecl();
            } else {
                this.typeDecl();
            }
        }
        this.parserObserver.notifyEnd();
    }

    private void constDecl() {
        this.parserObserver.notifyStart("ConstDecl");
        this.match("CONST");
        while ("identifier".equals(this.currentToken.kind)) {
            Token t = this.match("identifier");

            // declaration
            String name = t.str;
            this.match("=");
            Expression e = this.expression();
            this.match(";");

            // check for duplicate declaration
            if (this.curScope.local(name)) {
                throw this.parserError("duplicate declaration of " + name);
            }

            // check for valid expression type
            if (!(e instanceof NumberExpression)
                    || e.type != Integer.getInstance()) {
                throw this.parserError("invalid constant declaration");
            }
            NumberExpression n = (NumberExpression) e;
            // Value of 5 will change in future assignments
            Constant c = new Constant(Integer.getInstance(), n.constant.value);
            this.curScope.insert(name, c);
        }
        this.parserObserver.notifyEnd();
    }

    private void typeDecl() {
        this.parserObserver.notifyStart("TypeDecl");
        this.match("TYPE");
        while ("identifier".equals(this.currentToken.kind)) {
            // declaration
            Token tok = this.match("identifier");
            this.match("=");
            Type t = this.type();
            this.match(";");

            // Create new entry in symbol table
            String name = tok.str;
            // check for duplicate declaration
            if (this.curScope.local(name)) {
                throw this.parserError("duplicate declaration of " + name);
            }
            this.curScope.insert(name, t);
        }
        this.parserObserver.notifyEnd();
    }

    private void varDecl() {
        this.parserObserver.notifyStart("VarDecl");
        this.match("VAR");
        while ("identifier".equals(this.currentToken.kind)) {
            ArrayList<Token> a = this.identifierList();
            // declaration
            this.match(":");
            Type type = this.type();
            this.match(";");

            for (Token t : a) {
                String name = t.str;
                // check for duplicate declaration
                if (this.curScope.local(name)) {
                    throw this.parserError("duplicate declaration of " + name);
                }
                // insert into current scope
                Variable v = new Variable(type);
                this.curScope.insert(name, v);
            }
        }
        this.parserObserver.notifyEnd();
    }

    private Type type() {
        this.parserObserver.notifyStart("Type");
        Type t;
        if ("identifier".equals(this.currentToken.kind)) {
            // IDENTIFIER
            Token token = this.match("identifier");

            // Check for type in symbol table
            Entry e = this.curScope.find(token.str);
            if (e == null) {
                throw this.parserError("undefined type " + token.str);
            }
            // Ensure that the entry is a type and not a constant or variable
            if (e instanceof Type) {
                t = e.getType();
            } else {
                throw this.parserError(
                        token.str + " is " + e.toString() + " not Type");
            }

        } else if ("ARRAY".equals(this.currentToken.kind)) {
            // ARRAY
            this.match("ARRAY");
            Expression e = this.expression();
            this.match("OF");

            this.checkType(
                    e.type, Integer.class, "invalid array declaration");
            // Check that the expression is allowed
            if (!(e instanceof NumberExpression)) {
                throw this.parserError("invalid array declaration");
            }
            // get the array's declared length
            int len = (java.lang.Integer) ((NumberExpression) e).constant.value;
            if (len <= 0) {
                throw this.parserError("array must have length at least 1");
            }
            // Check for type in symbol table
            Type elemType = this.type();
            // value of 5 will change in future assignments
            t = new Array(elemType, len);

        } else {
            // RECORD
            this.match("RECORD");
            this.curScope = new Scope(this.curScope);
            while ("identifier".equals(this.currentToken.kind)) {
                ArrayList<Token> a = this.identifierList();
                this.match(":");
                Type type = this.type();
                for (Token token : a) {
                    String name = token.str;
                    // check for duplicate declaration
                    if (this.curScope.local(name)) {
                        throw this.parserError(
                                "duplicate declaration of " + name);
                    }
                    Variable v = new Variable(type);
                    this.curScope.insert(name, v);
                }
                this.match(";");
            }
            this.match("END");
            t = new Record(this.curScope);
            // Reset scope and sever connection
            Scope tmp = this.curScope;
            this.curScope = this.curScope.outer;
            tmp.outer = null;
        }
        this.parserObserver.notifyEnd();
        return t;
    }

    private Expression expression() {
        boolean negative = false;
        this.parserObserver.notifyStart("Expression");
        if ("+".equals(this.currentToken.kind)) {
            this.match("+");
        } else if ("-".equals(this.currentToken.kind)) {
            this.match("-");
            negative = true;
        }
        Expression t1 = this.term();
        // subtract from zero to create negative numbers
        if (negative) {
            // check if we can fold constants
            if (t1 instanceof NumberExpression) {
                NumberExpression n1 = (NumberExpression) t1;
                ((NumberExpression) t1).constant.value =
                        0 - (int) n1.constant.value;
            } else {
                // create zero constant
                Constant zero = new Constant(Integer.getInstance(), 0);
                NumberExpression n = new NumberExpression(zero);
                t1 = new BinaryExpression("-", n, t1);
            }
        }

        while ("+".equals(this.currentToken.kind)
                || "-".equals(this.currentToken.kind)) {
            // create binary expression
            String op = this.currentToken.kind;
            this.parserObserver.match(this.currentToken);
            this.next();
            Expression t2 = this.term();

            // CONSTANT FOLDING CHECK
            t1 = this.checkFoldConstants(t1, t2, op);
        }
        this.parserObserver.notifyEnd();
        return t1;
    }

    private Expression checkFoldConstants(
            Expression t1, Expression t2, String op) {
        if (t1 instanceof NumberExpression
                && t2 instanceof NumberExpression) {
            NumberExpression n1 = (NumberExpression) t1;
            NumberExpression n2 = (NumberExpression) t2;
            int val;
            switch (op) {
                case "+":
                    val = (java.lang.Integer) n1.constant.value
                            + (java.lang.Integer) n2.constant.value;
                    break;
                case "-":
                    val = (java.lang.Integer) n1.constant.value
                            - (java.lang.Integer) n2.constant.value;
                    break;
                default:
                    // never get here
                    val = 0;
            }
            t1 = new NumberExpression(new Constant(n1.type, val));
        } else {
            t1 = new BinaryExpression(op, t1, t2);
        }
        return t1;
    }

    private Expression term() {
        this.parserObserver.notifyStart("Term");
        Expression e1 = this.factor();
        while ("*".equals(this.currentToken.kind)
                || "DIV".equals(this.currentToken.kind)
                || "MOD".equals(this.currentToken.kind)) {
            // create binary expression
            String op = this.currentToken.kind;
            this.parserObserver.match(this.currentToken);
            this.next();
            Expression e2 = this.factor();

            // CONSTANT FOLDING CHECK
            if (e1 instanceof NumberExpression
                    && e2 instanceof NumberExpression) {
                NumberExpression n1 = (NumberExpression) e1;
                NumberExpression n2 = (NumberExpression) e2;
                int val;
                switch (op) {
                    case "*":
                        val = (java.lang.Integer) n1.constant.value
                                * (java.lang.Integer) n2.constant.value;
                        break;
                    case "DIV":
                        if ((java.lang.Integer) n2.constant.value == 0) {
                            throw this.parserError("divide by 0");
                        }
                        val = (java.lang.Integer) n1.constant.value
                                / (java.lang.Integer) n2.constant.value;
                        break;
                    case "MOD":
                        if ((java.lang.Integer) n2.constant.value == 0) {
                            throw this.parserError("divide by 0");
                        }
                        val = (java.lang.Integer) n1.constant.value
                                % (java.lang.Integer) n2.constant.value;
                        break;
                    default:
                        // never get here
                        val = 0;
                }
                e1 = new NumberExpression(new Constant(n1.type, val));
            } else {
                e1 = new BinaryExpression(op, e1, e2);
            }
        }
        this.parserObserver.notifyEnd();
        return e1;
    }

    private Expression factor() {
        this.parserObserver.notifyStart("Factor");
        Expression e;
        if ("(".equals(this.currentToken.kind)) {
            this.match("(");
            // return expression in parenthesis
            e = this.expression();
            this.match(")");
        } else if ("integer".equals(this.currentToken.kind)) {
            // return integer constant expression
            e = new NumberExpression(new Constant(Integer.getInstance(),
                    this.currentToken.val));
            this.match("integer");
        } else {
            // return designator expression
            e = this.designator();
        }
        this.parserObserver.notifyEnd();
        return e;
    }

    private Instruction instructions() {
        this.parserObserver.notifyStart("Instructions");
        Instruction firstInstruction = this.instruction();
        Instruction i = firstInstruction;
        while (";".equals(this.currentToken.kind)) {
            this.parserObserver.match(this.currentToken);
            this.next();
            i.next = this.instruction();
            i = i.next;
        }
        this.parserObserver.notifyEnd();
        // return the first instruction in the tree
        return firstInstruction;
    }

    private Instruction instruction() {
        this.parserObserver.notifyStart("Instruction");
        Instruction i;
        switch (this.currentToken.kind) {
            case "identifier":
                i = this.assign();
                break;
            case "IF":
                i = this.matchIf();
                break;
            case "REPEAT":
                i = this.repeat();
                break;
            case "WHILE":
                i = this.matchWhile();
                break;
            case "READ":
                i = this.read();
                break;
            case "WRITE":
                i = this.write();
                break;
            default:
                throw this.parserError("invalid instruction");
        }
        this.parserObserver.notifyEnd();
        return i;
    }

    private Assign assign() {
        this.parserObserver.notifyStart("Assign");
        Expression des = this.designator();
        if (!(des instanceof Location)) {
            throw this.parserError("invalid assignment location");
        }
        String s = this.match(":=").kind;
        Expression exp = this.expression();
        // check of the two types are of the same class
        if (des.type != exp.type) {
            throw this.parserError(
                    "assignment type mismatch, should be "
                            + des.type.toString());
        }
        this.parserObserver.notifyEnd();
        return new Assign((Location) des, exp);
    }

    private If matchIf() {
        this.parserObserver.notifyStart("If");
        If returnIf;
        this.match("IF");
        Condition c = this.condition();
        this.match("THEN");
        Instruction t = this.instructions();
        if ("ELSE".equals(this.currentToken.kind)) {
            this.parserObserver.match(this.currentToken);
            this.next();
            Instruction f = this.instructions();
            // return an if with a true and false instruction
            returnIf = new If(c, t, f);
        } else {
            returnIf = new If(c, t, null);
        }
        this.match("END");
        this.parserObserver.notifyEnd();
        return returnIf;
    }

    private Repeat repeat() {
        this.parserObserver.notifyStart("Repeat");
        this.match("REPEAT");
        Instruction i = this.instructions();
        this.match("UNTIL");
        Condition c = this.condition();
        this.match("END");
        this.parserObserver.notifyEnd();
        return new Repeat(i, c);
    }

    private If matchWhile() {
        // split while loop into "if" and "repeat"
        this.parserObserver.notifyStart("While");
        this.match("WHILE");
        Condition ifCondition = this.condition();
        String untilRelation;
        switch (ifCondition.relation) {
            case "=":
                untilRelation = "#";
                break;
            case "#":
                untilRelation = "=";
                break;
            case "<":
                untilRelation = ">=";
                break;
            case ">":
                untilRelation = "<=";
                break;
            case ">=":
                untilRelation = "<";
                break;
            case "<=":
                untilRelation = ">";
                break;
            default:
                // never get here
                untilRelation = "";
        }
        // create new condition for the "until"
        Condition untilCondition = new Condition(
                ifCondition.left.cloneExp(),
                ifCondition.right.cloneExp(),
                untilRelation);
        this.match("DO");
        Instruction i = this.instructions();
        this.match("END");

        // create new repeat
        Repeat r = new Repeat(i, untilCondition);
        // create new if and wrap
        If newIf = new If(ifCondition, r, null);
        this.parserObserver.notifyEnd();
        return newIf;
    }

    private Condition condition() {
        this.parserObserver.notifyStart("Condition");
        Expression left = this.expression();
        Token t = this.match(new String[] {"#", "<", ">", "<=", ">=", "="});
        this.parserObserver.match(t);
        Expression right = this.expression();
        // Expressions must be integers
        if (left.type != Integer.getInstance()
                || right.type != Integer.getInstance()) {
            throw this.parserError(
                    "Condition expressions must be of type Integer");
        }
        this.parserObserver.notifyEnd();
        return new Condition(left, right, t.kind);
    }

    private Write write() {
        this.parserObserver.notifyStart("Write");
        this.match("WRITE");
        Expression e = this.expression();
        // Can only write integers
        if (e.type != Integer.getInstance()) {
            throw this.parserError("Invalid type to write");
        }
        this.parserObserver.notifyEnd();
        return new Write(e);
    }

    private Read read() {
        this.parserObserver.notifyStart("Read");
        this.match("READ");
        Expression exp = this.designator();
        // must be integer variable
        if (!(exp instanceof Location)
                || !(exp.type.equals(Integer.getInstance()))) {
            throw this.parserError("Invalid read");
        }
        this.parserObserver.notifyEnd();
        return new Read((Location) exp);
    }

    private Expression designator() {
        this.parserObserver.notifyStart("Designator");
        Token t = this.match("identifier");
        Expression returnExpression;

        // Find the variable object from the identifier in the symbol table
        Entry e = this.curScope.find(t.str);
        // If this identifier is not in the symbol table, or if is not
        // a constant or variable, throw an error
        if (e == null) {
            throw this.parserError("designator not defined");
        } else if (e instanceof Type) {
            throw this.parserError(
                    "invalid designator, must be constant or variable");
        } else if (e instanceof Constant) {
            // constant designator
            returnExpression =  new NumberExpression((Constant) e);
        } else {
            VariableLocation vl = new VariableLocation((Variable) e);
            returnExpression = this.selector(vl);
        }
        this.parserObserver.notifyEnd();
        return returnExpression;
    }

    private Location selector(VariableLocation l) {
        this.parserObserver.notifyStart("Selector");
        Location returnLocation = l;
        while ("[".equals(this.currentToken.kind)
                || ".".equals(this.currentToken.kind)) {
            if ("[".equals(this.currentToken.kind)) {
                this.parserObserver.match(this.currentToken);
                this.next();

                ArrayList<Expression> expressions = this.expressionList();
                Expression firstExpression = expressions.get(0);

                if (!(firstExpression.type == Integer.getInstance())) {
                    throw this.parserError("expression must be integer");
                }

                // location must be array type
                returnLocation = new IndexLocation(
                        returnLocation, firstExpression);
                Type t = (((IndexLocation) returnLocation).location.type);
                // ensure location is an IndexLocation
                this.checkType(t, Array.class, "invalid selector");
                returnLocation.type = ((Array) t).elementType;

                // loop through list of expressions
                int s = expressions.size();
                for (Expression e : expressions.subList(1, s)) {
                    // ensure index is integer
                    if (!(e.type == Integer.getInstance())) {
                        throw this.parserError("expression must be integer");
                    }

                    returnLocation = new IndexLocation(returnLocation, e);
                    t = (((IndexLocation) returnLocation).location.type);
                    // ensure location is an IndexLocation
                    this.checkType(t, Array.class, "invalid selector");
                    returnLocation.type = ((Array) t).elementType;
                }

                this.match("]");
            } else {
                // check if the given location is a record
                this.checkType(
                        returnLocation.type, Record.class, "invalid selector");
                Record r = (Record) returnLocation.type;
                this.parserObserver.match(this.currentToken);
                this.next();
                Token t = this.match("identifier");
                // check record scope for identifier
                Entry entry = r.scope.find(t.str);
                if (entry == null) {
                    throw this.parserError("Invalid record field");
                }
                returnLocation = new FieldLocation(
                        returnLocation, new VariableLocation((Variable) entry));
            }
        }
        this.parserObserver.notifyEnd();
        return returnLocation;
    }

    // Primarily used to reduce cyclomatic complexity in above method
    private void checkType(Type t, Class<?> c, String errorText) {
        if (!(t.getClass() == c)) {
            throw this.parserError(errorText);
        }
    }

    private ArrayList<Token> identifierList() {
        this.parserObserver.notifyStart("IdentifierList");
        ArrayList<Token> a = new ArrayList<Token>();
        Token t = this.match("identifier");
        a.add(t);
        while (",".equals(this.currentToken.kind)) {
            this.parserObserver.match(this.currentToken);
            this.next();
            t = this.match("identifier");
            a.add(t);
        }
        this.parserObserver.notifyEnd();
        return a;
    }

    private ArrayList<Expression> expressionList() {
        this.parserObserver.notifyStart("ExpressionList");
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        expressions.add(this.expression());
        while (",".equals(this.currentToken.kind)) {
            this.parserObserver.match(this.currentToken);
            this.next();
            expressions.add(this.expression());
        }
        this.parserObserver.notifyEnd();
        return expressions;
    }

    private UnsupportedOperationException parserError(String text) {
        return new UnsupportedOperationException(
                text + " at line " + this.scanner.lineNumber);
    }
}