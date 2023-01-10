package lowlang.syntax;

public class FreeStmt implements Stmt {
    public final Exp exp;

    public FreeStmt(final Exp exp) {
        this.exp = exp;
    }

    public int hashCode() { return exp.hashCode(); }
    public boolean equals(final Object other) {
        return (other instanceof FreeStmt &&
                ((FreeStmt)other).exp.equals(exp));
    }
    public String toString() {
        return "free(" + exp.toString() + ")";
    }
}
