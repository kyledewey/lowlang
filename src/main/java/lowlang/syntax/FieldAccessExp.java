package lowlang.syntax;

public class FieldAccessExp implements Exp {
    public final Exp exp;
    private StructureName expStructure; // Typechecker is expected to fill this in
    public final FieldName field;

    public FieldAccessExp(final Exp exp,
                          final FieldName field) {
        this.exp = exp;
        expStructure = null;
        this.field = field;
    }

    public int hashCode() {
        return exp.hashCode() + field.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccessExp) {
            final FieldAccessExp otherExp = (FieldAccessExp)other;
            return (otherExp.exp.equals(exp) &&
                    otherExp.field.equals(field));
        } else {
            return false;
        }
    }

    public String toString() {
        return exp.toString() + "." + field.toString();
    }

    public StructureName getExpStructure() {
        assert(expStructure != null);
        return expStructure;
    }

    public void setExpStructure(final StructureName expStructure) {
        assert(expStructure != null);
        this.expStructure = expStructure;
    }
}
