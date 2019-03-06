package codegen_expressions_example.syntax;

public class DereferenceExp implements Exp {
    public final Exp exp;
    private Type expType; // Typechecker is expected to fill this in
    
    public DereferenceExp(final Exp exp) {
        this.exp = exp;
        expType = null;
    }

    public int hashCode() { return exp.hashCode(); }
    public boolean equals(final Object other) {
        return (other instanceof DereferenceExp &&
                ((DereferenceExp)other).exp.equals(exp));
    }
    public String toString() {
        return "*" + exp.toString();
    }

    public Type getExpType() {
        assert(expType != null);
        return expType;
    }

    public void setExpType(final Type expType) {
        assert(expType != null);
        this.expType = expType;
    }
} // DereferenceExp
