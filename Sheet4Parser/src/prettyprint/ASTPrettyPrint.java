package prettyprint;

import parser.Expr;
import parser.Expr.*; // Importiert Expr, ExprVisitor und alle Records aus dem Parser-Package
import parser.ExprVisitor;

import java.util.stream.Collectors;

/**
 * Ein Visitor, der durch den AST läuft und den ursprünglichen Lisp-Code
 * (oder eine "hübschere" Version davon) rekonstruiert.
 */
public class ASTPrettyPrint implements ExprVisitor<String> {

    /**
     * Der Einstiegspunkt.
     * Nimmt einen beliebigen Ausdruck (oder Program) und gibt ihn als String zurück.
     */
    public String print(Expr expr) {
        if (expr == null) return "";
        return expr.accept(this);
    }

    // --- Implementierung der Visitor-Methoden ---

    @Override
    public String visitProgram(Program expr) {
        // Ein Programm ist eine Liste von Ausdrücken, getrennt durch Zeilenumbruch
        return expr.expressions().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String visitIntLiteral(IntLiteral expr) {
        return String.valueOf(expr.value());
    }

    @Override
    public String visitStringLiteral(StringLiteral expr) {
        // Strings wieder in Anführungszeichen packen
        return "\"" + expr.value() + "\"";
    }

    @Override
    public String visitBoolLiteral(BoolLiteral expr) {
        return String.valueOf(expr.value());
    }

    @Override
    public String visitVariable(Variable expr) {
        return expr.name();
    }

    @Override
    public String visitDef(Def expr) {
        // Format: (def name wert)
        return "(def " + expr.name() + " " + expr.value().accept(this) + ")";
    }

    @Override
    public String visitDefn(Defn expr) {
        // Format: (defn name (p1 p2) body)

        // Parameterliste zusammenbauen: (a b c)
        String params = expr.params().stream()
                .collect(Collectors.joining(" ", "(", ")"));

        return "(defn " + expr.name() + " " + params + " " + expr.body().accept(this) + ")";
    }

    @Override
    public String visitLet(Let expr) {
        // Format: (let (n1 v1 n2 v2) body)

        // Bindings zusammenbauen: n1 v1 n2 v2
        String bindings = expr.bindings().stream()
                .map(b -> b.name() + " " + b.value().accept(this))
                .collect(Collectors.joining(" "));

        return "(let (" + bindings + ") " + expr.body().accept(this) + ")";
    }

    @Override
    public String visitIf(If expr) {
        // Format: (if cond then [else])
        String s = "(if " + expr.condition().accept(this) + " " + expr.thenBranch().accept(this);

        if (expr.elseBranch() != null) {
            s += " " + expr.elseBranch().accept(this);
        }
        return s + ")";
    }

    @Override
    public String visitDo(Do expr) {
        // Format: (do expr1 expr2 ...)
        String body = expr.expressions().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(" "));

        return "(do " + body + ")";
    }

    @Override
    public String visitCall(Call expr) {
        // Format: (funcName arg1 arg2 ...)
        String args = expr.arguments().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(" "));

        if (args.isEmpty()) {
            return "(" + expr.functionName() + ")";
        }
        return "(" + expr.functionName() + " " + args + ")";
    }
}