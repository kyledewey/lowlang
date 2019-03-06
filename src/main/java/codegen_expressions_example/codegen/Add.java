package codegen_expressions_example.codegen;

public class Add implements MIPSInstruction {
    public final MIPSRegister rd;
    public final MIPSRegister rs;
    public final MIPSRegister rt;

    public Add(final MIPSRegister rd,
               final MIPSRegister rs,
               final MIPSRegister rt) {
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    public String toString() {
        return MIPSInstruction.toString("add", rd, rs, rt);
    }

    public boolean equals(final Object other) {
        if (other instanceof Add) {
            final Add otherAdd = (Add)other;
            return (rd.equals(otherAdd.rd) &&
                    rs.equals(otherAdd.rs) &&
                    rt.equals(otherAdd.rt));
        } else {
            return false;
        }
    } // equals

    public int hashCode() {
        return rd.hashCode() + rs.hashCode() + rt.hashCode();
    }
} // Add

