package lowlang.tokenizer;

public class DoubleEqualsToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof DoubleEqualsToken;
    }
    @Override
    public int hashCode() { return 18; }
    @Override
    public String toString() { return "DoubleEqualsToken"; }
}
