package lexer;

public enum TokenType {
    // Separatoren
    LEFT_PAREN,         // '('
    RIGHT_PAREN,        // ')'

    // Arithmetische Operationen
    PLUS,               // '+'
    MINUS,              // '-'
    MUL,                // '*'
    DIV,                // '/'
    EQUAL,              // '='
    GREATER,            // '>'
    LESS,               // '<'

    // Literale
    INTEGER,            // z.B.: 42
    STRING,             // z.B.: "42", "Hello"
    BOOLEAN,            // 'false', 'true'
    IDENTIFIER,         // Variablenname, Funktionsname z.B.: foo, list, string

    // Keywords
    DEF,                // Scope(Global) Variablenzuweisung 'def'
    DEFN,               // Funktionsaufruf 'defn'
    LET,                // Scope(Local) Variablenzuweisung 'let'
    IF,                 // Bedingung 'if'
    DO,                 // 'do'

    // Hilfstoken (EndOfFile)
    EOF
}
