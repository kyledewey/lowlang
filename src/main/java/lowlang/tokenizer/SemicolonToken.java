package lowlang.tokenizer;

public class SemicolonToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof SemicolonToken;
    }
    @Override
    public int hashCode() { return 22; }
    @Override
    public String toString() { return "SemicolonToken"; }
}
