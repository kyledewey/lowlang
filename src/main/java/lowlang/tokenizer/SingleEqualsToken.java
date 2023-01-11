package lowlang.tokenizer;

public class SingleEqualsToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof SingleEqualsToken;
    }
    @Override
    public int hashCode() { return 25; }
    @Override
    public String toString() { return "SingleEqualsToken"; }
}
