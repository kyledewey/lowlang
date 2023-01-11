package lowlang.parser;

import java.util.Optional;

public class FieldAccessExp implements Exp {
    public final Exp exp;
    public Optional<StructureName> expStructure; // needed for codegen
    public final FieldName field;

    public FieldAccessExp(final Exp exp,
                          final FieldName field) {
        this.exp = exp;
        expStructure = Optional.empty();
        this.field = field;
    }

    public int hashCode() {
        return (exp.hashCode() +
                expStructure.hashCode() +
                field.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccessExp) {
            final FieldAccessExp otherExp = (FieldAccessExp)other;
            return (exp.equals(otherExp.exp) &&
                    expStructure.equals(otherExp.expStructure) &&
                    field.equals(otherExp.field));
        } else {
            return false;
        }
    }

    public String toString() {
        return exp.toString() + "." + field.toString();
    }
}
