package lowlang.tokenizer;

public class StarToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof StarToken;
    }
    @Override
    public int hashCode() { return 6; }
    @Override
    public String toString() { return "StarToken"; }
}
