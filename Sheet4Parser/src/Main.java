import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import parser.*;
import prettyprint.ASTPrettyPrint;
import prettyprint.ASTTreePrinter;

public class Main {
    public static void main(String[] args) {
        String example1 = "(def x 10) (print x)";

        String example2 = """
                ;; Wir definieren eine leere Liste zum Vergleich
                (def empty-list (list))
                
                ;; Definition der rekursiven Funktion zur Längenberechnung
                (defn list-len (lst)
                    ;; Basisfall: Wenn die Liste gleich der leeren Liste ist, Ende
                    (if (= lst empty-list)
                        0 \s
                        ;; Rekursiver Schritt: 1 + Länge des Restes der Liste (tail)
                        (+ 1 (list-len (tail lst)))
                    )
                )
                
                ;; Test und Ausgabe
                (def my-numbers (list 10 20 30 40 42))
                
                (print (str "Die Laenge der Liste ist: " (list-len my-numbers)))""";

        System.out.println("--- Original Code ---");
        System.out.println(example2);
        System.out.println("\n--- Lexing ---");

        Lexer demolexer = new Lexer(example1);
        boolean run = true;
        while (run){
            Token demotoken = demolexer.nextToken();
            if(demotoken.type() == TokenType.EOF){
                run = false;
            }
            System.out.println(demotoken);
        }

        try {
            Lexer lexer = new Lexer(example2);

            Parser parser = new Parser(lexer);
            Expr.Program ast = parser.parse();

            System.out.println("\n--- Parsing erfolgreich! ---");
            System.out.println("\n--- Pretty Printed Programm ---");

            ASTPrettyPrint printer = new ASTPrettyPrint();
            System.out.println(printer.print(ast));

            System.out.println("\n--- Pretty Printed AST ---");
            ASTTreePrinter treePrinter = new ASTTreePrinter();
            System.out.println(treePrinter.print(ast));


        } catch (RuntimeException e) {
            System.err.println("\n!!! FEHLER !!!");
            System.err.println(e.getMessage());
        }
    }
}