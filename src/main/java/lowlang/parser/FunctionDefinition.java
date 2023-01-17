package lowlang.parser;

import java.util.List;

public class FunctionDefinition {
    public final Type returnType;
    public final FunctionName name;
    public final List<VariableDeclaration> parameters;
    public final List<Stmt> body;

    public FunctionDefinition(final Type returnType,
                              final FunctionName name,
                              final List<VariableDeclaration> parameters,
                              final List<Stmt> body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public int hashCode() {
        return (returnType.hashCode() +
                name.hashCode() +
                parameters.hashCode() +
                body.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionDefinition) {
            final FunctionDefinition otherDef =
                (FunctionDefinition)other;
            return (otherDef.returnType.equals(returnType) &&
                    otherDef.name.equals(name) &&
                    otherDef.parameters.equals(parameters) &&
                    otherDef.body.equals(body));
        } else {
            return false;
        }
    }

    public String toString() {
        return (returnType.toString() + " " +
                name.toString() + "(" +
                Join.join(", ", parameters) +
                ") { " + body.toString() + " }");
    }
}
