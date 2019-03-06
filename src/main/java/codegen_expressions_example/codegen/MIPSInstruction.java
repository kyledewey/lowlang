package codegen_expressions_example.codegen;

public interface MIPSInstruction {
    public static final String INDENT = "    ";
    
    public static String toString(final String instructionName,
                                  final MIPSRegister destination,
                                  final MIPSRegister source1,
                                  final MIPSRegister source2) {
        return (INDENT + instructionName + " " +
                destination.toString() + ", " +
                source1.toString() + ", " +
                source2.toString());
    } // toString
} // MIPSInstruction
