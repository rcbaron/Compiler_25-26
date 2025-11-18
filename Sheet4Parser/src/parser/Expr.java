package parser;

import java.util.List;

public sealed interface Expr permits
        Expr.Program,
        Expr.IntLiteral, Expr.StringLiteral, Expr.BoolLiteral, Expr.Variable,
        Expr.Def, Expr.Defn, Expr.Let, Expr.If, Expr.Do, Expr.Call {

    // Die Visitor-Methode
    <R> R accept(ExprVisitor<R> visitor);


    record Program(List<Expr> expressions) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitProgram(this); }
    }

    record IntLiteral(int value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitIntLiteral(this); }
    }

    record StringLiteral(String value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitStringLiteral(this); }
    }

    record BoolLiteral(boolean value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitBoolLiteral(this); }
    }

    record Variable(String name) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitVariable(this); }
    }

    record Def(String name, Expr value) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitDef(this); }
    }

    record Defn(String name, List<String> params, Expr body) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitDefn(this); }
    }

    record Binding(String name, Expr value) {}

    record Let(List<Binding> bindings, Expr body) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitLet(this); }
    }

    record If(Expr condition, Expr thenBranch, Expr elseBranch) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitIf(this); }
    }

    record Do(List<Expr> expressions) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitDo(this); }
    }

    record Call(String functionName, List<Expr> arguments) implements Expr {
        @Override public <R> R accept(ExprVisitor<R> visitor) { return visitor.visitCall(this); }
    }
}