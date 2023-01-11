package lowlang.syntax;

public class CharacterLiteralExp implements Exp {
    public final char value;

    public CharacterLiteralExp(final char value) {
        this.value = value;
    }

    public int hashCode() { return (int)value; }
    public boolean equals(final Object other) {
        return (other instanceof CharacterLiteralExp &&
                ((CharacterLiteralExp)other).value == value);
    }
    public String toString() {
        return Character.toString(value);
    }
}
