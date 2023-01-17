package lowlang.codegen;

public class Jalr extends SingleRegisterInstruction {
    public Jalr(final MIPSRegister rd) {
        super("jalr", rd);
    }
} // Jalr
