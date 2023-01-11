package lowlang.parser;

public class FunctionName extends Name {
    public FunctionName(final String name) {
        super(name);
    }

    public boolean sameClass(final Name other) {
        return other instanceof FunctionName;
    }
}

    
