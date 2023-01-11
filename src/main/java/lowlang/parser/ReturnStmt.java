package lowlang.parser;

import java.util.Optional;

public class ReturnStmt implements Stmt {
    public final Optional<Exp> exp;

    public ReturnStmt(final Optional<Exp> exp) {
        this.exp = exp;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof ReturnStmt &&
                exp.equals(((ReturnStmt)other).exp));
    }

    @Override
    public int hashCode() {
        return exp.hashCode();
    }

    @Override
    public String toString() {
        return "ReturnStmt(" + exp.toString() + ")";
    }
}
