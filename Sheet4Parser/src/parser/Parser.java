package parser;
import lexer.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    /**
     * Einstiegspunkt: Program ::= Expr { Expr }
     */
    public Expr.Program parse() {
        List<Expr> expressions = new ArrayList<>();

        while (currentToken.type() != TokenType.EOF) {
            expressions.add(parseExpr());
        }

        return new Expr.Program(expressions);
    }

    /**
     * Expr ::= Atom | SExpr
     */
    private Expr parseExpr() {
        return switch (currentToken.type()) {
            case INTEGER -> parseInt();
            case STRING -> parseString();
            case BOOLEAN -> parseBool();
            case IDENTIFIER -> parseVariable();
            case LEFT_PAREN -> parseSExpr();
            default -> throw error("Erwartet: Ausdruck (Atom oder Liste), Gefunden: " + currentToken);
        };
    }

    /**
     * SExpr ::= LPAREN Form RPAREN
     * Hier entscheidet sich anhand des ersten Elements der Liste, was es ist.
     */
    private Expr parseSExpr() {
        consume(TokenType.LEFT_PAREN);

        // Wir schauen auf das Token NACH der Klammer, um zu entscheiden
        Expr result = switch (currentToken.type()) {
            case DEF -> parseDef();
            case DEFN -> parseDefn();
            case LET -> parseLet();
            case IF -> parseIf();
            case DO -> parseDo();

            // Funktionsaufruf oder Operator (+ 1 2)
            case IDENTIFIER, PLUS, MINUS, MUL, DIV, EQUAL, LESS, GREATER
                    -> parseCall();

            default -> throw error("Erwartet: Funktionsname, Operator oder Keyword nach '(', Gefunden: " + currentToken);
        };

        consume(TokenType.RIGHT_PAREN);
        return result;
    }

    // --- Spezifische Parsing-Methoden (Grammatik-Regeln) ---

    // DefForm ::= "def" ID Expr
    private Expr parseDef() {
        consume(TokenType.DEF);
        String name = consume(TokenType.IDENTIFIER).lexeme();
        Expr value = parseExpr();
        return new Expr.Def(name, value);
    }

    // DefnForm ::= "defn" ID LPAREN { ID } RPAREN Expr
    private Expr parseDefn() {
        consume(TokenType.DEFN);
        String name = consume(TokenType.IDENTIFIER).lexeme();

        consume(TokenType.LEFT_PAREN);
        List<String> params = new ArrayList<>();
        while (currentToken.type() == TokenType.IDENTIFIER) {
            params.add(consume(TokenType.IDENTIFIER).lexeme());
        }
        consume(TokenType.RIGHT_PAREN);

        Expr body = parseExpr();
        return new Expr.Defn(name, params, body);
    }

    // LetForm ::= "let" LPAREN { ID Expr } RPAREN Expr
    private Expr parseLet() {
        consume(TokenType.LET);
        consume(TokenType.LEFT_PAREN);

        List<Expr.Binding> bindings = new ArrayList<>();
        // Solange wir Identifier sehen, kommen Bindings (Paare aus Name + Wert)
        while (currentToken.type() == TokenType.IDENTIFIER) {
            String varName = consume(TokenType.IDENTIFIER).lexeme();
            Expr varValue = parseExpr();
            bindings.add(new Expr.Binding(varName, varValue));
        }
        consume(TokenType.RIGHT_PAREN);

        Expr body = parseExpr();
        return new Expr.Let(bindings, body);
    }

    // IfForm ::= "if" Expr Expr [ Expr ]
    private Expr parseIf() {
        consume(TokenType.IF);
        Expr cond = parseExpr();
        Expr thenBranch = parseExpr();

        // Check optionaler Else-Zweig: Wenn noch keine Klammer zu geht, kommt noch was
        Expr elseBranch = null;
        if (currentToken.type() != TokenType.RIGHT_PAREN) {
            elseBranch = parseExpr();
        }

        return new Expr.If(cond, thenBranch, elseBranch);
    }

    // DoForm ::= "do" { Expr }
    private Expr parseDo() {
        consume(TokenType.DO);
        List<Expr> exprs = new ArrayList<>();
        while (currentToken.type() != TokenType.RIGHT_PAREN && currentToken.type() != TokenType.EOF) {
            exprs.add(parseExpr());
        }
        return new Expr.Do(exprs);
    }

    // CallForm ::= ( ID | OP ) { Expr }
    private Expr parseCall() {
        // Der Name kann ein Identifier (foo) oder ein Operator (+) sein
        String funcName = currentToken.lexeme();
        consume(currentToken.type()); // Wir konsumieren was auch immer es war

        List<Expr> args = new ArrayList<>();
        while (currentToken.type() != TokenType.RIGHT_PAREN && currentToken.type() != TokenType.EOF) {
            args.add(parseExpr());
        }
        return new Expr.Call(funcName, args);
    }

    // --- Atome ---

    private Expr parseInt() {
        int val = (int) currentToken.literal();
        consume(TokenType.INTEGER);
        return new Expr.IntLiteral(val);
    }

    private Expr parseString() {
        String val = (String) currentToken.literal();
        consume(TokenType.STRING);
        return new Expr.StringLiteral(val);
    }

    private Expr parseBool() {
        boolean val = (boolean) currentToken.literal();
        consume(TokenType.BOOLEAN);
        return new Expr.BoolLiteral(val);
    }

    private Expr parseVariable() {
        String name = currentToken.lexeme();
        consume(TokenType.IDENTIFIER);
        return new Expr.Variable(name);
    }

    // --- Hilfsmethoden für Navigation & Fehler ---

    /**
     * Prüft, ob das aktuelle Token den erwarteten Typ hat.
     * Wenn ja: Gehe zum nächsten Token und gib das alte zurück.
     * Wenn nein: Fehler (Abbruch).
     */
    private Token consume(TokenType expected) {
        if (currentToken.type() == expected) {
            Token old = currentToken;
            currentToken = lexer.nextToken();
            return old;
        } else {
            throw error("Erwartetes Token: " + expected + ", Tatsächlich gefunden: " + currentToken.type());
        }
    }

    private RuntimeException error(String message) {
        return new RuntimeException("Parse Error " + ": " + message);
    }
}

