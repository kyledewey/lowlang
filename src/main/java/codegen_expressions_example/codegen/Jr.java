package codegen_expressions_example.codegen;

public class Jr extends SingleRegisterInstruction {
    public Jr(final MIPSRegister rd) {
        super("jr", rd);
    }
}
