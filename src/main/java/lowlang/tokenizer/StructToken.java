package lowlang.tokenizer;

public class StructToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof StructToken;
    }
    @Override
    public int hashCode() { return 27; }
    @Override
    public String toString() { return "StructToken"; }
}
