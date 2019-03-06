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

    @Test
    public void testBoolLiteralTrue() throws IOException {
        assertResult(1, new BoolExp(true));
    }

    @Test
    public void testBoolLiteralFalse() throws IOException {
        assertResult(0, new BoolExp(false));
    }

    @Test
    public void testEqualsIntTrue() throws IOException {
        assertResult(1, new BinopExp(new IntExp(42),
                                     new EqualsOp(),
                                     new IntExp(42)));
    }

    @Test
    public void testEqualsIntFalse() throws IOException {
        assertResult(0, new BinopExp(new IntExp(42),
                                     new EqualsOp(),
                                     new IntExp(43)));
    }

    @Test
    public void testEqualsBoolTrue() throws IOException {
        assertResult(1, new BinopExp(new BoolExp(false),
                                     new EqualsOp(),
                                     new BoolExp(false)));
    }

    @Test
    public void testEqualsBoolFalse() throws IOException {
        assertResult(0, new BinopExp(new BoolExp(true),
                                     new EqualsOp(),
                                     new BoolExp(false)));
    }

    @Test
    public void testPlus() throws IOException {
        // 2 + 3 = 5
        assertResult(5, new BinopExp(new IntExp(2),
                                     new PlusOp(),
                                     new IntExp(3)));
    }

    @Test
    public void testMinus() throws IOException {
        // 3 - 2 = 1
        assertResult(1, new BinopExp(new IntExp(3),
                                     new MinusOp(),
                                     new IntExp(2)));
    }

    @Test
    public void testMult() throws IOException {
        // 3 * 4 = 12
        assertResult(12, new BinopExp(new IntExp(3),
                                      new MultOp(),
                                      new IntExp(4)));
    }

    @Test
    public void testDiv() throws IOException {
        // 6 / 2 = 3
        assertResult(3, new BinopExp(new IntExp(6),
                                     new DivOp(),
                                     new IntExp(2)));
    }

    @Test
    public void testSizeofInt() throws IOException {
        assertResult(4, new SizeofExp(new IntType()));
    }

    @Test
    public void testSizeofBool() throws IOException {
        assertResult(4, new SizeofExp(new BoolType()));
    }

    @Test
    public void testSizeofChar() throws IOException {
        assertResult(4, new SizeofExp(new CharType()));
    }

    @Test
    public void testSizeofPointer() throws IOException {
        assertResult(4, new SizeofExp(new PointerType(new IntType())));
    }
} // MIPSCodeGeneratorTest

