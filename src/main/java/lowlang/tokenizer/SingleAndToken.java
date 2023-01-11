package lowlang.tokenizer;

public class SingleAndToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof SingleAndToken;
    }
    @Override
    public int hashCode() { return 9; }
    @Override
    public String toString() { return "SingleAndToken"; }
}
