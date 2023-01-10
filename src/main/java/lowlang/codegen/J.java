package lowlang.codegen;

public class J extends SingleLabelInstruction {
    public J(final MIPSLabel label) {
        super("j", label);
    }
} // J

