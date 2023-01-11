package lowlang.parser;

import java.util.List;
import java.util.Optional;

public class CallLikeExp implements Exp {
    public final Exp base;
    public final List<Exp> params;
    public Optional<CallLikeResolved> resolution; // needed for codegen
    
    public CallLikeExp(final Exp base,
                       final List<Exp> params) {
        this.base = base;
        this.params = params;
        resolution = Optional.empty();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof CallLikeExp) {
            final CallLikeExp asCall = (CallLikeExp)other;
            return (base.equals(asCall.base) &&
                    params.equals(asCall.params) &&
                    resolution.equals(asCall.resolution));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (base.hashCode() +
                params.hashCode() +
                resolution.hashCode());
    }

    @Override
    public String toString() {
        return ("CallLikeExp(" +
                base.toString() + ", " +
                params.toString() + ", " +
                resolution.toString() + ")");
    }
}
