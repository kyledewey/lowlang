package lowlang.parser;

// DotOrCall ::=
//  Dot(FieldName) |
//  Call(List<Exp>)

public interface DotOrCall {
    public Exp toExp(final Exp base);
}
