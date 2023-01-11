package lowlang.parser;

public class FunctionResolved implements AddressOfResolved {
    public final FunctionName functionName;

    public FunctionResolved(final FunctionName functionName) {
        this.functionName = functionName;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof FunctionResolved &&
                functionName.equals(((FunctionResolved)other).functionName));
    }

    @Override
    public String toString() {
        return "FunctionResolved(" + functionName.toString() + ")";
    }
}
