# Worksheet 4 Lexer und Parser selbst implementiert

## 4.1
Gegeben sei die Grammatik $G=(\{S,A\},\{1,2,3\},P,S)$ mit den Produktionsregeln:
$$P = \{ S \to 1AS \mid 3, \quad A \to 2AS \mid \epsilon \}$$

### First-Mengen

Die First-Menge $FIRST(X)$ enthält alle Terminale, mit denen ein String beginnen kann, der aus $X$ abgeleitet wird.

| Nicht-Terminal | FIRST-Menge | Herleitung |
| :--- | :--- | :--- |
| **A** | $\{ 2, \epsilon \}$ | Aus $A \to 2AS$ ergibt sich **2**. Aus $A \to \epsilon$ ergibt sich **$\epsilon$**. |
| **S** | $\{ 1, 3 \}$ | Aus $S \to 1AS$ ergibt sich **1**. Aus $S \to 3$ ergibt sich **3**. |

### Follow-Mengen

Die Follow-Menge $FOLLOW(X)$ enthält alle Terminale, die in einer Satzform unmittelbar rechts von $X$ stehen können. Das End-Symbol wird mit $\$$ bezeichnet.

| Nicht-Terminal | FOLLOW-Menge | Herleitung |
| :--- | :--- | :--- |
| **S** | $\{ 1, 3, \$ \}$ | 1. Startsymbol erhält $\$$. <br> 2. Aus $A \to 2A\mathbf{S}$ erbt $S$ von $FOLLOW(A)$ ($\{1, 3\}$). <br> 3. Aus $S \to 1A\mathbf{S}$ erbt $S$ von sich selbst (keine Änderung). |
| **A** | $\{ 1, 3 \}$ | 1. Aus $S \to 1\mathbf{A}S$ folgt $FIRST(S)$ auf $A$. Da $FIRST(S)=\{1,3\}$ (ohne $\epsilon$), fügen wir **1, 3** hinzu. |

## 4.2
Die folgende kontextfreie Grammatik beschreibt eine Lisp-artige Sprache. Sie ist in EBNF notiert, um Rekursion und Listenstrukturen kompakt darzustellen.

```ebnf
/* Ein Programm besteht aus einer Sequenz von Ausdrücken */
Program     ::= Expr { Expr }

/* Ein Ausdruck ist entweder ein atomarer Wert oder eine komplexe Liste */
Expr        ::= Atom
              | SExpr

/* Basiswerte (Literale und Bezeichner) */
Atom        ::= INT
              | STRING
              | BOOL
              | ID

/* S-Expressions: Listen, die immer geklammert sind */
SExpr       ::= LPAREN Form RPAREN

/* Unterscheidung der Listeninhalte anhand des ersten Elements */
Form        ::= DefForm       /* Globale Variablen */
              | DefnForm      /* Funktionsdefinitionen */
              | LetForm       /* Lokale Scopes */
              | IfForm        /* Kontrollfluss */
              | DoForm        /* Sequenzen */
              | CallForm      /* Funktions- und Operatoraufrufe */

/* --- Spezialformen (Special Forms) --- */

DefForm     ::= "def" ID Expr
DefnForm    ::= "defn" ID LPAREN { ID } RPAREN Expr
LetForm     ::= "let" LPAREN { ID Expr } RPAREN Expr
IfForm      ::= "if" Expr Expr [ Expr ]
DoForm      ::= "do" { Expr }

/* --- Funktionsaufrufe --- */
/* Deckt sowohl Operatoren (+ 1 2) als auch Funktionen (print "hi") ab */
CallForm    ::= ( ID | OP ) { Expr }
```
## 4.6 
Obwohl Parser-Generatoren (wie Yacc, Bison, ANTLR) in der Theorie oft gelehrt werden, 
setzen viele große, moderne Compiler-Projekte auf manuell implementierte Recursive Descent Parser.

1. GCC (GNU Compiler Collection) - C++ Frontend
2. Clang / LLVM (C/C++/Objective-C)
3. V8 (Google Chrome JavaScript Engine) & TypeScript

Vorteile handgeschriebener Parser
1. Bessere Fehlermeldungen: Man kann genau prüfen, warum eine Regel fehlschlug und dem Nutzer helfen.

2. Fehler-Erholung (Error Recovery): Der Parser kann trotz Fehler weitermachen, um weitere Fehler zu finden (wichtig für IDEs).

3. Kontextsensitivität: Einfacherer Umgang mit Sprachen, bei denen die Bedeutung eines Tokens vom Kontext abhängt.

4. Keine externen Abhängigkeiten: Kein Build-Schritt durch Tools wie Yacc/ANTLR nötig.
