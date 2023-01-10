package lowlang.codegen;

public class Mflo extends SingleRegisterInstruction {
    public Mflo(final MIPSRegister rd) {
        super("mflo", rd);
    }
} // Mflo
