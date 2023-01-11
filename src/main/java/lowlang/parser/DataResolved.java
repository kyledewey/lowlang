package lowlang.parser;

public class DataResolved implements AddressOfResolved {
    @Override
    public boolean equals(final Object other) {
        return other instanceof DataResolved;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "DataResolved";
    }
}
