package lowlang.tokenizer;

public class SizeofToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof SizeofToken;
    }
    @Override
    public int hashCode() { return 12; }
    @Override
    public String toString() { return "SizeofToken"; }
}
