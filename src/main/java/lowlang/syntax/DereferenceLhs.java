package lowlang.syntax;

import java.util.Optional;

public class DereferenceLhs implements Lhs {
    public final Lhs lhs;
    public Optional<Type> typeAfterDereference; // needed for codegen
    
    public DereferenceLhs(final Lhs lhs) {
        this.lhs = lhs;
        typeAfterDereference = Optional.empty();
    }

    public int hashCode() {
        return lhs.hashCode() + typeAfterDereference.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof DereferenceLhs) {
            final DereferenceLhs otherLhs = (DereferenceLhs)other;
            return (lhs.equals(otherLhs.lhs) &&
                    typeAfterDereference.equals(otherLhs.typeAfterDereference));
        } else {
            return false;
        }
    }

    public String toString() {
        return "*" + lhs.toString();
    }
}
