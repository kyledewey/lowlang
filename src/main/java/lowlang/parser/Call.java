package lowlang.parser;

import java.util.List;

public class Call implements DotOrCall {
    public final List<Exp> exps;

    public Call(final List<Exp> exps) {
        this.exps = exps;
    }
    
    public Exp toExp(final Exp base) {
        return new CallLikeExp(base, exps);
    }
}
