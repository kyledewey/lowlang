package lowlang.tokenizer;

public class RightCurlyBraceToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof RightCurlyBraceToken;
    }
    @Override
    public int hashCode() { return 30; }
    @Override
    public String toString() { return "RightCurlyBraceToken"; }
}
