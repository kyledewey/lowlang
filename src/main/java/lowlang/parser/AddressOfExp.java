package lowlang.parser;

import java.util.Optional;

public class AddressOfExp implements Exp {
    public final Lhs lhs;
    public Optional<AddressOfResolved> resolved; // needed for codegen
    
    public AddressOfExp(final Lhs lhs) {
        this.lhs = lhs;
        resolved = Optional.empty();
    }

    public int hashCode() {
        return lhs.hashCode() + resolved.hashCode();
    }
    
    public boolean equals(final Object other) {
        if (other instanceof AddressOfExp) {
            final AddressOfExp asExp = (AddressOfExp)other;
            return (lhs.equals(asExp.lhs) &&
                    resolved.equals(asExp.resolved));
        } else {
            return false;
        }
    }
    
    public String toString() {
        return ("AddressOfExp(" +
                lhs.toString() + ", " +
                resolved.toString());
    }
}
