package codegen_expressions_example.codegen;

public class Add extends ThreeRegisterInstruction {
    public Add(final MIPSRegister rd,
               final MIPSRegister rs,
               final MIPSRegister rt) {
        super("add", rd, rs, rt);
    }
} // Add

