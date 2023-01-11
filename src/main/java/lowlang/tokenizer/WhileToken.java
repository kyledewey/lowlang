package lowlang.tokenizer;

public class WhileToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof WhileToken;
    }
    @Override
    public int hashCode() { return 21; }
    @Override
    public String toString() { return "WhileToken"; }
}
