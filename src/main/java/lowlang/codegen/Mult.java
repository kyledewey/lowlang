package lowlang.codegen;

public class Mult extends TwoRegisterInstruction {
    public Mult(final MIPSRegister rs,
                final MIPSRegister rt) {
        super("mult", rs, rt);
    }
} // Mult
