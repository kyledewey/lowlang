package lowlang.parser;

public class Cast implements CastOrMemItem {
    public final Type type;
    public Cast(final Type type) {
        this.type = type;
    }

    public Exp toExp(final Exp base) {
        return new CastExp(type, base);
    }
}
