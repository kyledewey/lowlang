package lowlang.tokenizer;

public class MallocToken implements Token {
    @Override
    public boolean equals(final Object other) {
        return other instanceof MallocToken;
    }
    @Override
    public int hashCode() { return 13; }
    @Override
    public String toString() { return "MallocToken"; }
}
