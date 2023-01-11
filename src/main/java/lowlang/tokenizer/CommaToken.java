package lowlang.tokenizer;

public class CommaToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof CommaToken;
    }
    @Override
    public int hashCode() { return 0; }
    @Override
    public String toString() { return "CommaToken"; }
}
