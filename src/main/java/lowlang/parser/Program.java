package lowlang.parser;

import java.util.List;

public class Program {
    public final List<StructureDeclaration> structDecs;
    public final List<FunctionDefinition> functionDefs;

    public Program(final List<StructureDeclaration> structDecs,
                   final List<FunctionDefinition> functionDefs) {
        this.structDecs = structDecs;
        this.functionDefs = functionDefs;
    }

    public int hashCode() {
        return structDecs.hashCode() + functionDefs.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof Program) {
            final Program otherProgram = (Program)other;
            return (structDecs.equals(otherProgram.structDecs) &&
                    functionDefs.equals(otherProgram.functionDefs));
        } else {
            return false;
        }
    }

    public String toString() {
        return (Join.join("\n", structDecs) + "\n\n" +
                Join.join("\n", functionDefs) + "\n");
    }
}
