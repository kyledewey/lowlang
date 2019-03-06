package codegen_expressions_example.codegen;

public class Mult extends TwoRegisterInstruction {
    public Mult(final MIPSRegister rs,
                final MIPSRegister rt) {
        super("mult", rs, rt);
    }
} // Mult
