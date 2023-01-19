package lowlang.codegen;

import lowlang.tokenizer.Tokenizer;
import lowlang.tokenizer.TokenizerException;
import lowlang.parser.Parser;
import lowlang.parser.Join;
import lowlang.parser.ParseException;
import lowlang.parser.Program;
import lowlang.typechecker.Typechecker;
import lowlang.typechecker.TypeErrorException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;

public class CodegenTest {
    @Rule public TestName name = new TestName();

    public static int[] parseOutput(final String[] spimOutput) {
        final int[] retval = new int[spimOutput.length];
        for (int index = 0; index < retval.length; index++) {
            retval[index] = Integer.parseInt(spimOutput[index]);
        }
        return retval;
    } // parseOutput

    public void assertResult(final String programAsString,
                             final int... expected) throws TokenizerException, ParseException, TypeErrorException, IOException {
        boolean wantToSaveFile = true; // for debugging

        final Program program = Parser.parse(Tokenizer.tokenize(programAsString));
        Typechecker.typecheckProgram(program);
        final File file = File.createTempFile(name.getMethodName(),
                                              ".asm",
                                              new File("testPrograms"));
        boolean testPassed = false;
        try {
            MIPSCodeGenerator.compile(program, file);
            final String[] output = SPIMRunner.runFile(file);
            final int[] received = parseOutput(output);
            if (wantToSaveFile) {
                assertArrayEquals("Expected: [" +
                                  Join.join(", ", expected) +
                                  "]; Received: [" +
                                  Join.join(", ", received) +
                                  "; File: " +
                                  file.getAbsolutePath(),
                                  expected,
                                  received);
            } else {
                assertArrayEquals(expected, received);
            }
            testPassed = true;
        } finally {
            if (!wantToSaveFile || testPassed) {
                file.delete();
            }
        }
    }

    // ---BEGIN TESTS FOR EXPRESSIONS---
    public void assertExpResult(final String expString,
                                final int expected) throws TokenizerException, ParseException, TypeErrorException, IOException{
        assertResult("void main() { print(" + expString + "); }",
                     expected);
    }
    
    @Test
    public void testIntLiteral() throws Exception {
        assertExpResult("1", 1);
    }

    @Test
    public void testBoolLiteralTrue() throws Exception {
        assertExpResult("true", 1);
    }

    @Test
    public void testBoolLiteralFalse() throws Exception {
        assertExpResult("false", 0);
    }

    @Test
    public void testEqualsIntTrue() throws Exception {
        assertExpResult("42 == 42", 1);
    }

    @Test
    public void testEqualsIntFalse() throws Exception {
        assertExpResult("42 == 43", 0);
    }

    @Test
    public void testEqualsBoolTrue() throws Exception {
        assertExpResult("false == false", 1);
    }

    @Test
    public void testEqualsBoolFalse() throws Exception {
        assertExpResult("true == false", 0);
    }

    @Test
    public void testPlus() throws Exception {
        assertExpResult("2 + 3", 5);
    }

    @Test
    public void testMinus() throws Exception {
        assertExpResult("3 - 2", 1);
    }

    @Test
    public void testMult() throws Exception {
        assertExpResult("3 * 4", 12);
    }

    @Test
    public void testDiv() throws Exception {
        assertExpResult("6 / 2", 3);
    }

    @Test
    public void testSizeofInt() throws Exception {
        assertExpResult("sizeof(int)", 4);
    }

    @Test
    public void testSizeofBool() throws Exception {
        assertExpResult("sizeof(bool)", 4);
    }

    @Test
    public void testSizeofPointer() throws Exception {
        assertExpResult("sizeof(int*)", 4);
    }

    @Test
    public void testDereference() throws Exception {
        // TODO: this is very bad; assumes initial value of allocated memory
        assertExpResult("*((int*)malloc(4))", 0);
    }
    // ---END TESTS FOR EXPRESSIONS---

    @Test
    public void testStructureAccessSingleField() throws Exception {
        assertResult("struct Foo { int x; };" +
                     "void main() { print(Foo(7).x); }",
                     7);
    }

    @Test
    public void testStructureAccessTwoFieldFirst() throws Exception {
        assertResult("struct Foo { int x; int y; };" +
                     "void main() { print(Foo(1, 2).x); }",
                     1);
    }

    @Test
    public void testStructureAccessTwoFieldSecond() throws Exception {
        assertResult("struct Foo { int x; int y; };" +
                     "void main() { print(Foo(1, 2).y); }",
                     2);
    }

    @Test
    public void testStructureAccessNestedStructureFirst() throws Exception {
        assertResult("struct Bar { int x; int y; };" +
                     "struct Foo { Bar f; int z; };" +
                     "void main() { print(Foo(Bar(1, 2), 3).f.y); }",
                     2);
    }

    @Test
    public void testStructureAccessNestedStructureSecond() throws Exception {
        assertResult("struct Bar { int x; int y; };" +
                     "struct Foo { int z; Bar f; };" +
                     "void main() { print(Foo(1, Bar(2, 3)).f.x); }",
                     2);
    }
}
