package lowlang.tokenizer;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(final A first, final B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Pair) {
            final Pair<A, B> asPair = (Pair<A, B>)other;
            return (first.equals(asPair.first) &&
                    second.equals(asPair.second));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return ("Pair(" +
                first.toString() + ", " +
                second.toString() + ")");
    }
}
