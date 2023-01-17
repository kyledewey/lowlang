package lowlang.parser;

import java.util.Optional;

public class AddressOfLhs implements Lhs {
    public final Lhs lhs;
    public Optional<AddressOfResolved> resolved; // needed for codegen
    
    public AddressOfLhs(final Lhs lhs) {
        this.lhs = lhs;
        resolved = Optional.empty();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof AddressOfLhs) {
            final AddressOfLhs asLhs = (AddressOfLhs)other;
            return (lhs.equals(asLhs.lhs) &&
                    resolved.equals(asLhs.resolved));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return lhs.hashCode() + resolved.hashCode();
    }

    @Override
    public String toString() {
        return ("AddressOfLhs(" +
                lhs.toString() + ", " +
                resolved.toString() + ")");
    }
}
