package syntatic;

import java.security.cert.CertPathValidatorException.BasicReason;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.jar.Attributes.Name;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.PrintCommand;
import interpreter.command.AssignCommand.Op;
import interpreter.expr.BinaryExpr;
import interpreter.expr.Expr;
import interpreter.expr.UnaryExpr;
import interpreter.value.Value;
import interpreter.expr.ConstExpr;
import interpreter.expr.Variable;
import interpreter.value.*;
import interpreter.command.*;
import interpreter.expr.*;
import interpreter.util.*;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;
    private Stack<Lexeme> history;
    private Stack<Lexeme> queued;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.history = new Stack<Lexeme>();
        this.queued = new Stack<Lexeme>();
    }

    private void rollback() {
        assert !history.isEmpty();

        System.out.println("Rollback (\"" + current.token + "\", " +
                current.type + ")");
        queued.push(current);
        current = history.pop();
    }

    private void advance() {
        System.out.println("Advanced (\"" + current.token + "\", " +
                current.type + ")");
        history.add(current);
        current = queued.isEmpty() ? lex.nextToken() : queued.pop();
    }

    private void eat(TokenType type) {
        System.out.println("Expected (..., " + type + "), found (\"" +
                current.token + "\", " + current.type + ")");
        if (type == current.type) {
            history.add(current);
            current = queued.isEmpty() ? lex.nextToken() : queued.pop();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    public Command start() {
        Command cmd = procCode();
        eat(TokenType.END_OF_FILE);
        return cmd;

    }

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<Command>();
        while (current.type == TokenType.DEF ||
                current.type == TokenType.PRINT ||
                current.type == TokenType.PRINTLN ||
                current.type == TokenType.IF ||
                current.type == TokenType.WHILE ||
                current.type == TokenType.FOR ||
                current.type == TokenType.FOREACH ||
                current.type == TokenType.NOT ||
                current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PARENTHESES ||
                current.type == TokenType.NULL ||
                current.type == TokenType.FALSE ||
                current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER ||
                current.type == TokenType.TEXT ||
                current.type == TokenType.READ ||
                current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE ||
                current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES ||
                current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRACKETS ||
                current.type == TokenType.VAR) {
            Command c = procCmd();
            cmds.add(c);
        }
        BlocksCommand bc = new BlocksCommand(line, cmds);
        return bc;
    }

    // <cmd> ::= <decl> | <print> | <if> | <while> | <for> | <foreach> | <assign>
    private Command procCmd() {
        Command cmd = null;
        switch (current.type) {
            case DEF:
                DeclarationCommand dc = procDecl();
                cmd = dc;
                break;
            case PRINT:
                PrintCommand pc = procPrint();
                cmd = pc;
                break;
            case PRINTLN:
                procPrint();
                break;
            case IF:
                procIf();
                break;
            case WHILE:
                WhileCommand wc = procWhile();
                cmd = wc;
                break;
            case FOR:
                procFor();
                break;
            case FOREACH:
                procForeach();
                break;
            case NOT:
                AssignCommand acnot = procAssign();
                cmd = acnot;
                break;
            case SUB:
                AssignCommand acsub = procAssign();
                cmd = acsub;
                break;
            case OPEN_PARENTHESES:
                AssignCommand acopenp = procAssign();
                cmd = acopenp;
                break;
            case NULL:
                AssignCommand acnull = procAssign();
                cmd = acnull;
                break;
            case FALSE:
                AssignCommand acfalse = procAssign();
                cmd = acfalse;
                break;
            case TRUE:
                AssignCommand actrue = procAssign();
                cmd = actrue;
                break;
            case NUMBER:
                AssignCommand acNumber = procAssign();
                cmd = acNumber;
                break;
            case TEXT:
                AssignCommand actext = procAssign();
                cmd = actext;
                break;
            case READ:
                AssignCommand acread = procAssign();
                cmd = acread;
                break;
            case EMPTY:
                AssignCommand acempty = procAssign();
                cmd = acempty;
                break;
            case SIZE:
                AssignCommand acsize = procAssign();
                cmd = acsize;
                break;
            case KEYS:
                AssignCommand ackeys = procAssign();
                cmd = ackeys;
                break;
            case VALUES:
                AssignCommand acvalue = procAssign();
                cmd = acvalue;
                break;
            case SWITCH:
                AssignCommand acswitch = procAssign();
                cmd = acswitch;
                break;
            case OPEN_BRACKETS:
                AssignCommand acopenb = procAssign();
                cmd = acopenb;
                break;
            case VAR:
                AssignCommand acvar = procAssign();
                cmd = acvar;
                break;
            default:
                showError();
        }
        return cmd;
    }

    // <decl> ::= def ( <decl-type1> | <decl-type2> )
    private DeclarationCommand procDecl() {
        eat(TokenType.DEF);

        DeclarationCommand dc = null;
        if (current.type == TokenType.VAR) {
            dc = procDeclType1();
        } else {
            dc = procDeclType2();
        }

        return dc;
    }

    // <decl-type1> ::= <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] }
    private DeclarationType1Command procDeclType1() {
        Variable lhs = procName();
        int line = lex.getLine();
        Expr rhs = null;
        if (current.type == TokenType.ASSIGN) {
            advance();
            rhs = procExpr();
        }

        while (current.type == TokenType.COMMA) {
            advance();
            lhs = procName();
            if (current.type == TokenType.ASSIGN) {
                advance();
                rhs = procExpr();
            }

        }
        DeclarationType1Command dt1c = new DeclarationType1Command(line, lhs, rhs);
        return dt1c;
    }

    // <decl-type2> ::= '(' <name> { ',' <name> } ')' '=' <expr>
    private DeclarationType2Command procDeclType2() {
        ArrayList<Variable> vlist = new ArrayList<>();
        eat(TokenType.OPEN_PARENTHESES);
        Variable lhs = procName();
        vlist.add(lhs);
        int line = lex.getLine();
        while (current.type == TokenType.COMMA) {
            advance();
            lhs = procName();
            vlist.add(lhs);
        }
        eat(TokenType.CLOSE_PARENTHESES);
        eat(TokenType.ASSIGN);
        Expr rhs = procExpr();

        DeclarationType2Command dt2c = new DeclarationType2Command(line, vlist, rhs);
        return dt2c;

    }

    // <print> ::= (print | println) '(' <expr> ')'
    private PrintCommand procPrint() {
        boolean newline = false;
        if (current.type == TokenType.PRINT) {
            advance();
        } else if (current.type == TokenType.PRINTLN) {
            newline = true;
            advance();
        } else {
            showError();
        }
        int line = lex.getLine();
        eat(TokenType.OPEN_PARENTHESES);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PARENTHESES);

        PrintCommand pc = new PrintCommand(line, newline, expr);
        return pc;
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private void procIf() {
        eat(TokenType.IF);
        eat(TokenType.OPEN_PARENTHESES);
        procExpr();
        eat(TokenType.CLOSE_PARENTHESES);
        procBody();
        if (current.type == TokenType.ELSE) {
            advance();
            procBody();
        }
    }

    // <while> ::= while '(' <expr> ')' <body>
    private WhileCommand procWhile() {
        eat(TokenType.WHILE);
        int line = lex.getLine();
        eat(TokenType.OPEN_PARENTHESES);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PARENTHESES);
        Command cmds = procBody();

        WhileCommand wc = new WhileCommand(line, expr, cmds);
        return wc;

    }

    // <for> ::= for '(' [ ( <decl> | <assign> ) { ',' ( <decl> | <assign> ) } ]
    // ';'
    // [ <expr> ] ';' [ <assign> { ',' <assign> } ] ')' <body>
    private void procFor() {
        eat(TokenType.FOR);
        eat(TokenType.OPEN_PARENTHESES);
        if (current.type == TokenType.DEF || current.type == TokenType.NOT || current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PARENTHESES || current.type == TokenType.NULL ||
                current.type == TokenType.FALSE || current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER || current.type == TokenType.TEXT ||
                current.type == TokenType.READ || current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE || current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES || current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRACKETS || current.type == TokenType.VAR) {

            if (current.type == TokenType.DEF) {
                procDecl();
            } else {
                procArith();
            }
            while (current.type == TokenType.COMMA) {
                advance();
                if (current.type == TokenType.DEF || current.type == TokenType.NOT || current.type == TokenType.SUB ||
                        current.type == TokenType.OPEN_PARENTHESES || current.type == TokenType.NULL ||
                        current.type == TokenType.FALSE || current.type == TokenType.TRUE ||
                        current.type == TokenType.NUMBER || current.type == TokenType.TEXT ||
                        current.type == TokenType.READ || current.type == TokenType.EMPTY ||
                        current.type == TokenType.SIZE || current.type == TokenType.KEYS ||
                        current.type == TokenType.VALUES || current.type == TokenType.SWITCH ||
                        current.type == TokenType.OPEN_BRACKETS || current.type == TokenType.VAR) {
                    if (current.type == TokenType.DEF) {
                        procDecl();
                    } else {
                        procAssign();
                    }

                }

            }

        }

        eat(TokenType.SEMICOLON);
        if (current.type == TokenType.NOT || current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PARENTHESES || current.type == TokenType.NULL ||
                current.type == TokenType.FALSE || current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER || current.type == TokenType.TEXT ||
                current.type == TokenType.READ || current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE || current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES || current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRACKETS || current.type == TokenType.VAR) {
            procExpr();
        }
        eat(TokenType.SEMICOLON);
        if (current.type == TokenType.NOT || current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PARENTHESES || current.type == TokenType.NULL ||
                current.type == TokenType.FALSE || current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER || current.type == TokenType.TEXT ||
                current.type == TokenType.READ || current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE || current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES || current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRACKETS || current.type == TokenType.VAR) {
            procAssign();

            while (current.type == TokenType.COMMA) {
                advance();
                procAssign();
            }
        }
        eat(TokenType.CLOSE_PARENTHESES);
        procBody();

    }

    // <foreach> ::= foreach '(' [ def ] <name> in <expr> ')' <body>
    private void procForeach() {
        eat(TokenType.FOREACH);
        eat(TokenType.OPEN_PARENTHESES);

        if (current.type == TokenType.DEF) {
            advance();
        }
        procName();
        eat(TokenType.IN);
        procExpr();
        eat(TokenType.CLOSE_PARENTHESES);
        procBody();

    }

    // <body> ::= <cmd> | '{' <code> '}'
    private Command procBody() {

        Command cmd;
        if (current.type == TokenType.OPEN_CURLY_BRACES) {
            advance();
            cmd = procCode();
            eat(TokenType.CLOSE_CURLY_BRACES);
        } else {
            cmd = procCmd();
        }

        return cmd;

    }

    // <assign> ::= <expr> ( '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '**=') <expr>
    private AssignCommand procAssign() {
        Expr left = procExpr();
        if (!(left instanceof SetExpr)) {
            Utils.abort(lex.getLine());
        }
        AssignCommand.Op op = null;
        switch (current.type) {
            case ASSIGN:
                op = AssignCommand.Op.StdOp;
                advance();
                break;
            case ASSING_ADD:
                op = AssignCommand.Op.AddOp;
                advance();
                break;
            case ASSIGN_SUB:
                op = AssignCommand.Op.SubOp;
                advance();
                break;
            case ASSING_MUL:
                op = AssignCommand.Op.MulOp;
                advance();
                break;
            case ASSING_DIV:
                op = AssignCommand.Op.DivOp;
                advance();
                break;
            case ASSING_MOD:
                op = AssignCommand.Op.ModOp;
                advance();
                break;
            default:
                showError();
        }
        int line = lex.getLine();
        Expr right = procExpr();

        AssignCommand ac = new AssignCommand(line, (SetExpr) left, op, right);
        return ac;
    }

    // <expr> ::= <rel> { ('&&' | '||') <rel> }
    private Expr procExpr() {
        Expr left = procRel();

        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {
            BinaryExpr.Op op = null;
            if (current.type == TokenType.AND) {
                advance();
                op = BinaryExpr.Op.AndOp;
            } else {
                advance();
                op = BinaryExpr.Op.OrOp;
            }
            int line = lex.getLine();
            Expr right = procRel();
            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }
        return left;
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in')
    // <cast> ]
    private Expr procRel() {
        Expr left = procCast();
        if (current.type == TokenType.LOWER || current.type == TokenType.GREATER ||
                current.type == TokenType.LOWER_EQUAL || current.type == TokenType.GREATER_EQUAL ||
                current.type == TokenType.EQUAL || current.type == TokenType.NOT_EQUAL ||
                current.type == TokenType.CONTAIN || current.type == TokenType.NOT_CONTAIN) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case LOWER:
                    advance();
                    op = BinaryExpr.Op.LowerThanOp;

                    break;
                case GREATER:
                    advance();
                    op = BinaryExpr.Op.GreaterThanOp;

                    break;
                case LOWER_EQUAL:
                    advance();
                    op = BinaryExpr.Op.LowerEqualOp;

                    break;
                case GREATER_EQUAL:
                    advance();
                    op = BinaryExpr.Op.GreaterThanOp;

                    break;
                case EQUAL:
                    advance();
                    op = BinaryExpr.Op.EqualOp;

                    break;
                case NOT_EQUAL:
                    advance();
                    op = BinaryExpr.Op.NotEqualOp;

                    break;
                case CONTAIN:
                    advance();
                    op = BinaryExpr.Op.ContainsOp;

                    break;
                case NOT_CONTAIN:
                    advance();
                    op = BinaryExpr.Op.NotContainsOp;

                    break;
                default:

            }
            int line = lex.getLine();
            Expr right = procCast();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }

        return left;
    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private Expr procCast() {

        Expr expr = procArith();
        if (current.type == TokenType.AS) {
            advance();
            if (current.type == TokenType.BOOLEAN ||
                    current.type == TokenType.INTEGER ||
                    current.type == TokenType.STRING) {
                advance();
            }
        }
        return expr;
    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private Expr procArith() {
        Expr left = procTerm();

        while (current.type == TokenType.ADD ||
                current.type == TokenType.SUB) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case ADD:
                    advance();
                    op = BinaryExpr.Op.AddOp;
                    break;
                case SUB:
                    advance();
                    op = BinaryExpr.Op.SubOp;
                    break;
                default:
                    showError();
            }
            int line = lex.getLine();
            Expr rigth = procTerm();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, rigth);
            left = bexpr;
        }
        return left;
    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() {
        Expr left = procPower();
        while (current.type == TokenType.MUL || current.type == TokenType.DIV || current.type == TokenType.MOD) {
            BinaryExpr.Op op = null;
            switch (current.type) {
                case MUL:
                    advance();
                    op = BinaryExpr.Op.MulOp;
                    break;
                case DIV:
                    advance();
                    op = BinaryExpr.Op.DivOp;
                    break;
                case MOD:
                    advance();
                    op = BinaryExpr.Op.ModOp;
                    break;
                default:
                    showError();
            }
            int line = lex.getLine();

            Expr right = procPower();

            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;

        }

        return left;
    }

    // <power> ::= <factor> { '**' <factor> }
    private Expr procPower() {
        Expr left = procFactor();
        while (current.type == TokenType.POWER) {
            BinaryExpr.Op op = null;
            advance();
            op = BinaryExpr.Op.PowerOp;
            int line = lex.getLine();
            Expr right = procFactor();
            BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }
        return left;
    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private Expr procFactor() {
        Expr expr = null;

        UnaryExpr.Op op = null;
        if (current.type == TokenType.NOT) {
            advance();
            op = UnaryExpr.Op.NotOp;
        } else if (current.type == TokenType.SUB) {
            advance();
            op = UnaryExpr.Op.NegOp;
        }
        int line = lex.getLine();

        if (current.type == TokenType.OPEN_PARENTHESES) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_PARENTHESES);
        } else {
            expr = procRValue();
        }

        if (op != null) {
            UnaryExpr uexpr = new UnaryExpr(line, expr, op);
            expr = uexpr;
        }

        return expr;

    }

    // <lvalue> ::= <name> { '.' <name> | '[' <expr> ']' }
    private Variable procLValue() {
        Variable var = procName();

        while (current.type == TokenType.DOT || current.type == TokenType.OPEN_BRACKETS) {
            if (current.type == TokenType.DOT) {
                advance();
                procName();
            } else {
                advance();
                procExpr();
                eat(TokenType.CLOSE_BRACKETS);
            }
        }
        return var;
    }

    // <rvalue> ::= <const> | <function> | <switch> | <struct> | <lvalue>
    private Expr procRValue() {
        Expr expr = null;
        int line = lex.getLine();
        switch (current.type) {
            case NULL:
                procConst();
                break;
            case FALSE:
                procConst();
                break;
            case TRUE:
                procConst();
                break;
            case NUMBER:
                Value<?> n = procConst();
                line = lex.getLine();
                ConstExpr ncexpr = new ConstExpr(line, n);
                expr = ncexpr;
                break;
            case TEXT:
                Value<?> v = procConst();
                line = lex.getLine();
                ConstExpr tcexpr = new ConstExpr(line, v);
                expr = tcexpr;
                break;
            case READ:
                procFunction();
                break;
            case EMPTY:
                procFunction();
                break;
            case SIZE:
                procFunction();
                break;
            case KEYS:
                procFunction();
                break;
            case VALUES:
                UnaryExpr uexpr = procFunction();
                expr = uexpr;
                break;
            case SWITCH:
                procSwitch();
                break;
            case OPEN_BRACKETS:
                procStruct();
                break;
            case VAR:
                Variable var = procLValue();
                expr = var;
                break;
            default:
                showError();
        }
        return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private Value<?> procConst() {
        Value<?> v = null;
        if (current.type == TokenType.NULL) {
            advance();
        } else if (current.type == TokenType.FALSE) {
            advance();
            BooleanValue bv = new BooleanValue(false);
            v = bv;
        } else if (current.type == TokenType.TRUE) {
            advance();
            BooleanValue bv = new BooleanValue(true);
            v = bv;
        } else if (current.type == TokenType.NUMBER) {
            NumberValue nv = procNumber();
            v = nv;
        } else if (current.type == TokenType.TEXT) {
            TextValue tv = procText();
            v = tv;
        } else {
            showError();
        }

        return v;

    }

    // <function> ::= (read | empty | size | keys | values) '(' <expr> ')'
    private UnaryExpr procFunction() {
        UnaryExpr.Op op = null;
        switch (current.type) {
            case READ:
                advance();
                op = UnaryExpr.Op.ReadOp;
                break;
            case EMPTY:
                advance();
                op = UnaryExpr.Op.EmptyOp;
                break;
            case SIZE:
                advance();
                op = UnaryExpr.Op.SizeOp;
                break;
            case KEYS:
                advance();
                op = UnaryExpr.Op.KeysOp;
                break;
            case VALUES:
                advance();
                op = UnaryExpr.Op.ValuesOp;
                break;
            default:
                showError();
        }

        int line = lex.getLine();

        eat(TokenType.OPEN_PARENTHESES);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PARENTHESES);

        UnaryExpr uexpr = new UnaryExpr(line, expr, op);
        return uexpr;

    }

    // <switch> ::= switch '(' <expr> ')' '{' { case <expr> '->' <expr> } [ default
    // '->' <expr> ] '}'
    private void procSwitch() {
        eat(TokenType.SWITCH);
        eat(TokenType.OPEN_PARENTHESES);
        procExpr();
        eat(TokenType.CLOSE_PARENTHESES);
        eat(TokenType.OPEN_CURLY_BRACES);
        while (current.type == TokenType.CASE) {
            advance();
            procExpr();
            eat(TokenType.RIGTH_ARROW);
            procExpr();
        }

        if (current.type == TokenType.DEFAULT) {
            advance();
            eat(TokenType.RIGTH_ARROW);
            procExpr();
        }

        eat(TokenType.CLOSE_CURLY_BRACES);
    }

    // <struct> ::= '[' [ ':' | <expr> { ',' <expr> } | <name> ':' <expr> { ','
    // <name> ':' <expr> } ] ']'
    private void procStruct() {
        eat(TokenType.OPEN_BRACKETS);

        if (current.type == TokenType.COLON) {
            advance();
        } else if (current.type == TokenType.CLOSE_BRACKETS) {
            // Do nothing.
        } else {
            Lexeme prev = current;
            advance();

            if (prev.type == TokenType.VAR &&
                    current.type == TokenType.COLON) {
                rollback();

                procName();
                eat(TokenType.COLON);
                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();

                    procName();
                    eat(TokenType.COLON);
                    procExpr();
                }
            } else {
                rollback();

                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();
                    procExpr();
                }
            }
        }

        eat(TokenType.CLOSE_BRACKETS);
    }

    private Variable procName() {
        String tmp = current.token;
        eat(TokenType.VAR);
        int line = lex.getLine();

        Variable var = new Variable(line, tmp);
        return var;

    }

    private NumberValue procNumber() {
        String tmp = current.token;
        eat(TokenType.NUMBER);

        int v;
        try {
            v = Integer.parseInt(tmp);
        } catch (Exception e) {
            v = 0;
        }

        NumberValue nv = new NumberValue(v);
        return nv;
    }

    private TextValue procText() {

        String tmp = current.token;

        eat(TokenType.TEXT);

        TextValue tv = new TextValue(tmp);
        return tv;

    }

}
