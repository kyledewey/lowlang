package lowlang.tokenizer;

public class ContinueToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof ContinueToken;
    }
    @Override
    public int hashCode() { return 24; }
    @Override
    public String toString() { return "ContinueToken"; }
}
