package lowlang.syntax;

import java.util.Optional;

public class FieldAccessLhs implements Lhs {
    public final Lhs lhs;
    public Optional<StructureName> lhsStructure; // needed for codegen
    public final FieldName field;

    public FieldAccessLhs(final Lhs lhs,
                          final FieldName field) {
        this.lhs = lhs;
        lhsStructure = Optional.empty();
        this.field = field;
    }

    public int hashCode() {
        return (lhs.hashCode() +
                lhsStructure.hashCode() +
                field.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccessLhs) {
            final FieldAccessLhs otherLhs = (FieldAccessLhs)other;
            return (lhs.equals(otherLhs.lhs) &&
                    lhsStructure.equals(otherLhs.lhsStructure) &&
                    field.equals(otherLhs.field));
        } else {
            return false;
        }
    }

    public String toString() {
        return lhs.toString() + "." + field.toString();
    }
}
