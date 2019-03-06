package codegen_expressions_example.codegen;

public class Lw extends MemoryInstruction {
    public Lw(final MIPSRegister left,
              final int offset,
              final MIPSRegister right) {
        super("lw", left, offset, right);
    }
} // Lw
