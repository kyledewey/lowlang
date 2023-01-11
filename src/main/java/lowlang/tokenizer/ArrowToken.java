package lowlang.tokenizer;

public class ArrowToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof ArrowToken;
    }
    @Override
    public int hashCode() { return 7; }
    @Override
    public String toString() { return "ArrowToken"; }
}
