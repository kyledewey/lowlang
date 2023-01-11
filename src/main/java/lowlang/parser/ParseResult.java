package lowlang.parser;

public class ParseResult<A> {
    public final A result;
    public final int nextPosition;

    public ParseResult(final A result,
                       final int nextPosition) {
        this.result = result;
        this.nextPosition = nextPosition;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof ParseResult) {
            final ParseResult<A> asResult = (ParseResult)other;
            return (result.equals(asResult.result) &&
                    nextPosition == asResult.nextPosition);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return result.hashCode() + nextPosition;
    }

    @Override
    public String toString() {
        return ("ParseResult(" +
                result.toString() + ", " +
                nextPosition + ")");
    }
}
