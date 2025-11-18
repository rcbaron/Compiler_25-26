package prettyprint;

import parser.Expr;
import parser.Expr.*;
import parser.ExprVisitor;

public class ASTTreePrinter implements ExprVisitor<String> {

    // Wir merken uns die aktuelle Einrückungstiefe
    private int depth = 0;

    public String print(Expr expr) {
        depth = 0;
        return expr.accept(this);
    }

    // Hilfsmethode für die Einrückung (2 Leerzeichen pro Tiefe)
    private String indent() {
        return "  ".repeat(depth);
    }

    // Hilfsmethode, um Rekursion sauber zu kapseln
    private String printChild(Expr expr) {
        depth++;
        String result = expr.accept(this);
        depth--;
        return result;
    }

    // --- Visitor Implementierungen ---

    @Override
    public String visitProgram(Program expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Program\n");

        for (Expr e : expr.expressions()) {
            // Wir erhöhen die Tiefe manuell für jedes Kind
            sb.append(printChild(e));
        }
        return sb.toString();
    }

    @Override
    public String visitIntLiteral(IntLiteral expr) {
        return indent() + "Int: " + expr.value() + "\n";
    }

    @Override
    public String visitStringLiteral(StringLiteral expr) {
        return indent() + "String: \"" + expr.value() + "\"\n";
    }

    @Override
    public String visitBoolLiteral(BoolLiteral expr) {
        return indent() + "Bool: " + expr.value() + "\n";
    }

    @Override
    public String visitVariable(Variable expr) {
        return indent() + "Var: " + expr.name() + "\n";
    }

    @Override
    public String visitDef(Def expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Def (").append(expr.name()).append(")\n");
        sb.append(printChild(expr.value()));
        return sb.toString();
    }

    @Override
    public String visitDefn(Defn expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Function (").append(expr.name()).append(")\n");

        depth++;
        sb.append(indent()).append("Params: ").append(expr.params()).append("\n");
        depth--;

        sb.append(printChild(expr.body()));
        return sb.toString();
    }

    @Override
    public String visitLet(Let expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Let Scope\n");

        depth++;
        for (Expr.Binding b : expr.bindings()) {
            sb.append(indent()).append("Binding: ").append(b.name()).append("\n");
            sb.append(printChild(b.value()));
        }
        depth--;

        sb.append(indent()).append("Body:\n");
        sb.append(printChild(expr.body()));
        return sb.toString();
    }

    @Override
    public String visitIf(If expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("If\n");

        sb.append(indent()).append("  Condition:\n");
        sb.append(printChild(expr.condition()));

        sb.append(indent()).append("  Then:\n");
        sb.append(printChild(expr.thenBranch()));

        if (expr.elseBranch() != null) {
            sb.append(indent()).append("  Else:\n");
            sb.append(printChild(expr.elseBranch()));
        }
        return sb.toString();
    }

    @Override
    public String visitDo(Do expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Do Block\n");
        for (Expr e : expr.expressions()) {
            sb.append(printChild(e));
        }
        return sb.toString();
    }

    @Override
    public String visitCall(Call expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Call: ").append(expr.functionName()).append("\n");

        depth++; // Einrückung erhöhen für Argumente
        for (Expr arg : expr.arguments()) {
            sb.append(arg.accept(this)); // Hier accept direkt, da wir in depth++ sind
        }
        depth--;
        return sb.toString();
    }
}