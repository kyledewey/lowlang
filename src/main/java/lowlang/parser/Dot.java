package lowlang.parser;

public class Dot implements DotOrCall {
    public final FieldName fieldName;
    public class Dot(final FieldName fieldName) {
        this.fieldName = fieldName;
    }

    public Exp toExp(final Exp base) {
        return new FieldAccessExp(base, fieldName);
    }
}
