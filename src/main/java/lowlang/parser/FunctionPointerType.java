package lowlang.parser;

import java.util.List;

public class FunctionPointerType implements Type {
    public final List<Type> paramTypes;
    public final Type returnType;

    public FunctionPointerType(final List<Type> paramTypes,
                               final Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof FunctionPointerType) {
            final FunctionPointerType asFunc = (FunctionPointerType)other;
            return (paramTypes.equals(asFunc.paramTypes) &&
                    returnType.equals(asFunc.returnType));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return paramTypes.hashCode() + returnType.hashCode();
    }

    @Override
    public String toString() {
        return ("FunctionPointerType(" +
                paramTypes.toString() + ", " +
                returnType.toString() + ")");
    }
}
