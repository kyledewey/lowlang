package lowlang.syntax;

public class ExpStmt implements Stmt {
    public final Exp exp;

    public ExpStmt(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(final Object other) {
        return (other instanceof ExpStmt &&
                exp.equals(((ExpStmt)other).exp));
    }
    
    public int hashCode() {
        return exp.hashCode();
    }

    public String toString() {
        return exp.toString() + ";";
    }
}
