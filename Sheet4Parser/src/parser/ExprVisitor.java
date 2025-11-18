package parser;

public interface ExprVisitor<R> {
    R visitProgram(Expr.Program expr);
    R visitIntLiteral(Expr.IntLiteral expr);
    R visitStringLiteral(Expr.StringLiteral expr);
    R visitBoolLiteral(Expr.BoolLiteral expr);
    R visitVariable(Expr.Variable expr);
    R visitDef(Expr.Def expr);
    R visitDefn(Expr.Defn expr);
    R visitLet(Expr.Let expr);
    R visitIf(Expr.If expr);
    R visitDo(Expr.Do expr);
    R visitCall(Expr.Call expr);
}
