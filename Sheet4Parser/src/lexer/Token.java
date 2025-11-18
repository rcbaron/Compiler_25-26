package lexer;

public record Token(TokenType type, String lexeme, Object literal) {

    public Token(TokenType type, String lexeme){
        this(type, lexeme, null);
    }

    @Override
    public String toString() {
            return literal == null
                    ? "<%s, '%s'>".formatted(type, lexeme)
                    : "<%s, '%s', %s>".formatted(type, lexeme, literal);

    }
}