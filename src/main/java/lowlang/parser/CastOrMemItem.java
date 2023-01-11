package lowlang.parser;

// CastOrMemItem ::=
//   Cast(Type) |
//   StarMemItem |
//   AddressOfItem

public interface CastOrMemItem {
    public Exp toExp(final Exp base);
}
