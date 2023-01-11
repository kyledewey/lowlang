package lowlang.parser;

import java.util.List;

public class MakeStructureExp implements Exp {
    public final StructureName name;
    public final List<Exp> parameters;

    public MakeStructureExp(final StructureName name,
                            final List<Exp> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public int hashCode() {
        return name.hashCode() + parameters.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof MakeStructureExp) {
            final MakeStructureExp otherExp = (MakeStructureExp)other;
            return (name.equals(otherExp.name) &&
                    parameters.equals(otherExp.parameters));
        } else {
            return false;
        }
    }
    
    public String toString() {
        return (name.toString() + "(" +
                Join.join(", ", parameters) + ")");
    }
}
