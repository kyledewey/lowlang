package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MIPSCodeGeneratorTest {
    public int parseOutput(final String[] spimOutput) {
        assert(spimOutput.length == 2);
        return Integer.parseInt(spimOutput[1]);
    } // parseOutput
    
    public void assertResult(final int expected, final Exp exp) throws IOException {
        final File file = File.createTempFile("test", ".asm");
        try {
            MIPSCodeGenerator.writeExpressionToFile(exp, file);
            final String[] output = SPIMRunner.runFile(file);
            assertEquals(expected, parseOutput(output));
        } finally {
            file.delete();
        }
    } // assertResult

    @Test
    public void testIntLiteral() throws IOException {
        assertResult(1, new IntExp(1));
    }
} // MIPSCodeGeneratorTest

