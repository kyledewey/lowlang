package codegen_expressions_example.codegen;

public class Jal implements MIPSInstruction {
    public final MIPSLabel jumpTo;

    public Jal(final MIPSLabel jumpTo) {
        this.jumpTo = jumpTo;
    }

    public String toString() {
        return (MIPSInstruction.INDENT + "jal " +
                jumpTo.getName());
    }

    public boolean equals(final Object other) {
        return (other instanceof Jal &&
                ((Jal)other).jumpTo.equals(jumpTo));
    }

    public int hashCode() {
        return jumpTo.hashCode();
    }
} // Jal
