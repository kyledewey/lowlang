package lowlang.syntax;

import java.util.List;

public class FunctionCallExp implements Exp {
    public final FunctionName name;
    public final List<Exp> parameters;

    public FunctionCallExp(final FunctionName name,
                           final List<Exp> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public int hashCode() {
        return name.hashCode() + parameters.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionCallExp) {
            final FunctionCallExp otherExp = (FunctionCallExp)other;
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
