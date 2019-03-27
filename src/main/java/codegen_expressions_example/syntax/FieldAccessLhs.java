package codegen_expressions_example.syntax;

public class FieldAccessLhs implements Lhs {
    public final Lhs lhs;
    private StructureName lhsStructure; // Typechecker is expected to fill this in
    public final FieldName field;

    public FieldAccessLhs(final Lhs lhs,
                          final FieldName field) {
        this.lhs = lhs;
        lhsStructure = null;
        this.field = field;
    }

    public int hashCode() {
        return lhs.hashCode() + field.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccessLhs) {
            final FieldAccessLhs otherLhs = (FieldAccessLhs)other;
            return (otherLhs.lhs.equals(lhs) &&
                    otherLhs.field.equals(field));
        } else {
            return false;
        }
    }

    public String toString() {
        return lhs.toString() + "." + field.toString();
    }

    public StructureName getLhsStructure() {
        assert(lhsStructure != null);
        return lhsStructure;
    }

    public void setLhsStructure(final StructureName lhsStructure) {
        assert(lhsStructure != null);
        this.lhsStructure = lhsStructure;
    }
}
