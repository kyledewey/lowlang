package lowlang.tokenizer;

public class RightParenToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof RightParenToken;
    }
    @Override
    public int hashCode() { return 5; }
    @Override
    public String toString() { return "RightParenToken"; }
}
