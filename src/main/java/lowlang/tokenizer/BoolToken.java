package lowlang.tokenizer;

public class BoolToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof BoolToken;
    }
    @Override
    public int hashCode() { return 3; }
    @Override
    public String toString() { return "BoolToken"; }
}
