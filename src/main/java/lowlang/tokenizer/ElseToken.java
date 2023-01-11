package lowlang.tokenizer;

public class ElseToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof ElseToken;
    }
    @Override
    public int hashCode() { return 20; }
    @Override
    public String toString() { return "ElseToken"; }
}
