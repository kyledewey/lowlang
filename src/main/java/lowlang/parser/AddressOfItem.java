package lowlang.parser;

public class AddressOfItem implements CastOrMemItem {
    public Exp toExp(final Exp base) {
        return new AddressOfExp(base);
    }
}
