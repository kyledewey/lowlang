package codegen_expressions_example.codegen;

public class Jal extends SingleLabelInstruction {
    public Jal(final MIPSLabel label) {
        super("jal", label);
    }
} // Jal
