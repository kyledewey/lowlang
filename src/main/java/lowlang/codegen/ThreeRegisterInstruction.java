package lowlang.codegen;

public class ThreeRegisterInstruction implements MIPSInstruction {
    public final String instructionName;
    public final MIPSRegister rd;
    public final MIPSRegister rs;
    public final MIPSRegister rt;

    public ThreeRegisterInstruction(final String instructionName,
                                    final MIPSRegister rd,
                                    final MIPSRegister rs,
                                    final MIPSRegister rt) {
        this.instructionName = instructionName;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    public String toString() {
        return (MIPSInstruction.INDENT +
                instructionName + " " +
                rd.toString() + ", " +
                rs.toString() + ", " +
                rt.toString());
    } // toString

    public boolean equals(final Object other) {
        if (other instanceof ThreeRegisterInstruction) {
            final ThreeRegisterInstruction otherI =
                (ThreeRegisterInstruction)other;
            return (instructionName.equals(otherI.instructionName) &&
                    rd.equals(otherI.rd) &&
                    rs.equals(otherI.rs) &&
                    rt.equals(otherI.rt));
        } else {
            return false;
        }
    } // equals
    
    public int hashCode() {
        return (instructionName.hashCode() +
                rd.hashCode() +
                rs.hashCode() +
                rt.hashCode());
    } // hashCode
} // MIPSInstruction

