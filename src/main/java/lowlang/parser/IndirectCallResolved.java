package lowlang.parser;

public class IndirectCallResolved implements CallLikeResolved {
    public final FunctionPointerType functionPointer;

    public IndirectCallResolved(final FunctionPointerType functionPointer) {
        this.functionPointer = functionPointer;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof IndirectCallResolved &&
                functionPointer.equals(((IndirectCallResolved)other).functionPointer));
    }

    @Override
    public int hashCode() {
        return functionPointer.hashCode();
    }

    @Override
    public String toString() {
        return "IndirectCallResolved(" + functionPointer.toString() + ")";
    }
}
