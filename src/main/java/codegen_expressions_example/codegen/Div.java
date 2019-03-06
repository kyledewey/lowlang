package codegen_expressions_example.codegen;

public class Div extends TwoRegisterInstruction {
    public Div(final MIPSRegister rs,
               final MIPSRegister rt) {
        super("div", rs, rt);
    }
} // Div
