package lowlang.tokenizer;

public class VoidToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof VoidToken;
    }
    @Override
    public int hashCode() { return 2; }
    @Override
    public String toString() { return "VoidToken"; }
}
