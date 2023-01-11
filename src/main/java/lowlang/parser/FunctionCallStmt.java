package lowlang.parser;

import java.util.List;

public class FunctionCallStmt implements Stmt {
    public final FunctionCallExp asExp;

    public FunctionCallStmt(final FunctionName name,
                            final List<Exp> parameters) {
        asExp = new FunctionCallExp(name, parameters);
    }

    public int hashCode() {
        return asExp.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof FunctionCallStmt &&
                ((FunctionCallStmt)other).asExp.equals(asExp));
    }

    public String toString() {
        return asExp.toString() + ";";
    }
}

    
