package lowlang.codegen;

public class TwoRegisterInstruction implements MIPSInstruction {
    public final String instructionName;
    public final MIPSRegister rs;
    public final MIPSRegister rt;

    public TwoRegisterInstruction(final String instructionName,
                                  final MIPSRegister rs,
                                  final MIPSRegister rt) {
        this.instructionName = instructionName;
        this.rs = rs;
        this.rt = rt;
    }

    public String toString() {
        return (MIPSInstruction.INDENT +
                instructionName + " " +
                rs.toString() + ", " +
                rt.toString());
    } // toString

    public boolean equals(final Object other) {
        if (other instanceof TwoRegisterInstruction) {
            final TwoRegisterInstruction otherI =
                (TwoRegisterInstruction)other;
            return (instructionName.equals(otherI.instructionName) &&
                    rs.equals(otherI.rs) &&
                    rt.equals(otherI.rt));
        } else {
            return false;
        }
    } // equals

    public int hashCode() {
        return (instructionName.hashCode() +
                rs.hashCode() +
                rt.hashCode());
    } // hashCode
} // TwoRegisterInstruction
