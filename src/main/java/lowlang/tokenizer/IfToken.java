package lowlang.tokenizer;

public class IfToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof IfToken;
    }
    @Override
    public int hashCode() { return 19; }
    @Override
    public String toString() { return "IfToken"; }
}
