package lowlang.syntax;

import java.util.List;

public class StructureDeclaration {
    public final StructureName name;
    public final List<VariableDeclaration> fields;

    public StructureDeclaration(final StructureName name,
                                final List<VariableDeclaration> fields) {
        this.name = name;
        this.fields = fields;
    }

    public int hashCode() {
        return name.hashCode() + fields.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof StructureDeclaration) {
            final StructureDeclaration otherDec =
                (StructureDeclaration)other;
            return (otherDec.name.equals(name) &&
                    fields.equals(otherDec.fields));
        } else {
            return false;
        }
    }

    public String toString() {
        return (name.toString() + " { " +
                Join.join("; ", fields) + " }");
    }
}

