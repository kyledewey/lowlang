package lowlang.tokenizer;

public class LeftCurlyBraceToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof LeftCurlyBraceToken;
    }
    @Override
    public int hashCode() { return 29; }
    @Override
    public String toString() { return "LeftCurlyBraceToken"; }
}
