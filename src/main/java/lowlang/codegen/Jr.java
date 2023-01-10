package lowlang.codegen;

public class Jr extends SingleRegisterInstruction {
    public Jr(final MIPSRegister rd) {
        super("jr", rd);
    }
}
