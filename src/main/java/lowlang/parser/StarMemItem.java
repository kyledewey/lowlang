package lowlang.parser;

public class StarMemItem implements CastOrMemItem {
    public Exp toExp(final Exp base) {
        return new DereferenceExp(base);
    }
}
