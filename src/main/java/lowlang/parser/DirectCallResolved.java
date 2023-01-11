package lowlang.parser;

public class DirectCallResolved implements CallLikeResolved {
    public final FunctionName functionName;

    public DirectCallResolved(final FunctionName functionName) {
        this.functionName = functionName;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof DirectCallResolved &&
                functionName.equals(((DirectCallResolved)other).functionName));
    }

    @Override
    public int hashCode() {
        return functionName.hashCode();
    }

    @Override
    public String toString() {
        return "DirectCallResolved(" + functionName.toString() + ")";
    }
}
