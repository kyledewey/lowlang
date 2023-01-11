package lowlang.parser;

public class FieldName extends Name {
    public FieldName(final String name) {
        super(name);
    }

    public boolean sameClass(final Name other) {
        return other instanceof FieldName;
    }
}
