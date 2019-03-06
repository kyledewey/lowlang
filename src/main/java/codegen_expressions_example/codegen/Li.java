package codegen_expressions_example.codegen;

public class Li implements MIPSInstruction {
    public final int immediate;

    public Li(final int immediate) {
        this.immediate = immediate;
    }

    public String toString() {
        return (MIPSInstruction.INDENT + "li " +
                Integer.toString(immediate));
    }

    public boolean equals(final Object other) {
        return (other instanceof Li &&
                ((Li)other).immediate == immediate);
    }

    public int hashCode() {
        return immediate;
    }
} // Li

