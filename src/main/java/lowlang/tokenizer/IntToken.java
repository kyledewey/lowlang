package lowlang.tokenizer;

public class IntToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof IntToken;
    }
    @Override
    public int hashCode() { return 1; }
    @Override
    public String toString() { return "IntToken"; }
}
