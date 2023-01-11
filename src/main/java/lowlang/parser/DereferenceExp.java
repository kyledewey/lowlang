package lowlang.parser;

import java.util.Optional;

public class DereferenceExp implements Exp {
    public final Exp exp;
    public Optional<Type> typeAfterDereference; // needed for codegen
    
    public DereferenceExp(final Exp exp) {
        this.exp = exp;
        typeAfterDereference = Optional.empty();
    }

    public int hashCode() {
        return exp.hashCode() + typeAfterDereference.hashCode();
    }
    
    public boolean equals(final Object other) {
        if (other instanceof DereferenceExp) {
            final DereferenceExp asDeref = (DereferenceExp)other;
            return (exp.equals(asDeref.exp) &&
                    typeAfterDereference.equals(asDeref.typeAfterDereference));
        } else {
            return false;
        }
    }
    public String toString() {
        return "*" + exp.toString();
    }
} // DereferenceExp
