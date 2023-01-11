package lowlang.tokenizer;

public class BreakToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof BreakToken;
    }
    @Override
    public int hashCode() { return 23; }
    @Override
    public String toString() { return "BreakToken"; }
}
