package lowlang.tokenizer;

public class IntLiteralToken implements Token {
    public final int value;

    public IntLiteralToken(final int value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof IntLiteralToken &&
                value == ((IntLiteralToken)other).value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "IntLiteralToken(" + value + ")";
    }
}
