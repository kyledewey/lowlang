package lowlang.tokenizer;

public class DotToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof DotToken;
    }
    @Override
    public int hashCode() { return 8; }
    @Override
    public String toString() { return "DotToken"; }
}
