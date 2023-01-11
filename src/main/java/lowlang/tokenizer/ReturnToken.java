package lowlang.tokenizer;

public class ReturnToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof ReturnToken;
    }
    @Override
    public int hashCode() { return 26; }
    @Override
    public String toString() { return "ReturnToken"; }
}
