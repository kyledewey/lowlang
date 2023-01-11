package lowlang.parser;

public class AddressOfLhs implements Lhs {
    public final Lhs lhs;

    public AddressOfLhs(final Lhs lhs) {
        this.lhs = lhs;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof AddressOfLhs &&
                lhs.equals(((AddressOfLhs)other).lhs));
    }

    @Override
    public int hashCode() {
        return lhs.hashCode();
    }

    @Override
    public String toString() {
        return "AddressOfLhs(" + lhs.toString() + ")";
    }
}
