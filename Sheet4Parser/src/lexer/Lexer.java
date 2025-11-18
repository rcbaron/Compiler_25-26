package lexer;
import java.lang.StringBuilder;

public class Lexer {
    private String input;
    private int index = 0;
    private char peek;
    private static final char EOF_CHAR = '\0';

    // Constructor, sets 'peek' via 'cosume()'
    public Lexer(String input) {
        this.input = input;
        if (!input.isEmpty()){
            consume();
        }
    }

    // put peek to current Char and consume(), Handling of TokenType
    public Token nextToken(){
        while (peek != EOF_CHAR){
            switch (peek){
                case ' ', '\t', '\r', '\n' -> {WS();continue;}
                // Einfache Token (consume() aufrufen und Token zurückgeben)
                case '(' -> { consume(); return new Token(TokenType.LEFT_PAREN, "("); }
                case ')' -> { consume(); return new Token(TokenType.RIGHT_PAREN, ")"); }
                case '+' -> { consume(); return new Token(TokenType.PLUS, "+"); }
                case '*' -> { consume(); return new Token(TokenType.MUL, "*"); }
                case '/' -> { consume(); return new Token(TokenType.DIV, "/"); }
                case '=' -> { consume(); return new Token(TokenType.EQUAL, "="); }
                case '<' -> { consume(); return new Token(TokenType.LESS, "<"); }
                case '>' -> { consume(); return new Token(TokenType.GREATER, ">"); }

                // Minus: Sonderfall wegen negativen Zahlen oder Operator
                case '-' -> { return NEG_NUMBER(); }

                // Strings
                case '"' -> { return STRING(); }

                // Semikolon (Kommentarstart prüfen)
                case ';' -> {
                    consume(); // Erstes ; weg
                    if (match(';')) {
                        COMMENT(); // Kommentar bis Zeilenende konsumieren
                        continue;
                    } else {
                        throw error("Erwartetes Zeichen ';' nach ';'");
                    }
                }

                // Default: Check for Digit or Letter
                default -> {
                    if (Character.isDigit(peek)) {
                        return NUMBER();
                    }
                    if (isLetter(peek)) {
                        return NAME();
                    }
                    throw error("Ungueltiges Zeichen: " + peek);
                }
            }
        }
        return new Token(TokenType.EOF, "<EOF>");
    }

    // Whitespace Handling
    private void WS(){
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r'){
            consume();
        }
    }

    // Identifier Handling
    private Token NAME(){
        StringBuilder buff = new StringBuilder();

        while (isLetter(peek) || Character.isDigit(peek) || peek == '-') {
            buff.append(peek);
            consume();
        }

        String text = buff.toString();

        return switch (text){
            case "def" -> new Token(TokenType.DEF, text);
            case "defn" -> new Token(TokenType.DEFN, text);
            case "let" -> new Token(TokenType.LET, text);
            case "if" -> new Token(TokenType.IF, text);
            case "do" -> new Token(TokenType.DO, text);
            case "true" -> new Token(TokenType.BOOLEAN, text, true);
            case "false" -> new Token(TokenType.BOOLEAN, text, false);
            default -> new Token(TokenType.IDENTIFIER, text);
        };
    }

    // Number Handling
    private Token NUMBER(){
        StringBuilder buff = new StringBuilder();

        while (Character.isDigit(peek)){
            buff.append(peek);
            consume();
        }
        return new Token(TokenType.INTEGER, buff.toString(), Integer.parseInt(buff.toString()));
    }

    // Negative Number Handling
    private Token NEG_NUMBER(){
        consume();

        if (Character.isDigit(peek)){
            Token numToken = NUMBER();
            int value = - (int) numToken.literal();
            return new Token(TokenType.INTEGER, "-" + numToken.lexeme(), value);
        }
        return new Token(TokenType.MINUS, "-");
    }

    // String Handling
    private Token STRING(){
        consume();
        StringBuilder buff = new StringBuilder();

        while (peek != '"' && peek != EOF_CHAR){
            buff.append(peek);
            consume();
        }

        if (peek == '"'){
            consume();
            return new Token(TokenType.STRING, buff.toString(), buff.toString());
        }
        else {
            throw error("String not closed");
        }
    }

    // Comment Handling
    private void COMMENT(){
        while (peek != '\n' && peek != EOF_CHAR){
            consume();
        }
    }

    // Setting 'index' up, puts 'peek' to current Char or EOF
    private void consume(){
        if (index < input.length()){
            peek = input.charAt(index);
            index++;
        }
        else {
            peek = EOF_CHAR;
        }
    }

    // match current Char with peek and consume() it
    private boolean match(char c){
        if (peek == c){
            consume();
            return true;
        }
        return false;
    }

    // checks if current Char is 'Letter'
    private boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    // Exception Handling + Message
    private RuntimeException error(String msg) {
        return new RuntimeException("Lexer Error: " + msg);
    }
}
