package lowlang.parser;

import java.util.Optional;

public class ExpStmt implements Stmt {
    public final Exp exp;
    public Optional<Type> expType; // needed for codegen
    
    public ExpStmt(final Exp exp) {
        this.exp = exp;
        expType = Optional.empty();
    }

    public boolean equals(final Object other) {
        if (other instanceof ExpStmt) {
            final ExpStmt asExp = (ExpStmt)other;
            return (exp.equals(asExp.exp) &&
                    expType.equals(asExp.expType));
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return exp.hashCode() + expType.hashCode();
    }

    public String toString() {
        return ("ExpStmt(" +
                exp.toString() + ", " +
                expType.toString() + ")");
    }
}
