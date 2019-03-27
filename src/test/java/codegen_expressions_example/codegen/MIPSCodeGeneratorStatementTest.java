package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.*;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MIPSCodeGeneratorStatementTest {
    // TODO: code duplication with MIPSCodeGeneratorTest
    public int parseOutput(final String[] spimOutput) {
        assert(spimOutput.length == 2);
        return Integer.parseInt(spimOutput[1]);
    } // parseOutput

    public void assertResult(final int expected, final Stmt stmt) throws IOException {
        assertResult(expected, stmt, new HashMap<StructureName, LinkedHashMap<FieldName, Type>>());
    }
        
    public void assertResult(final int expected,
                             final Stmt stmt,
                             final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs) throws IOException {
        boolean wantToSaveFile = false; // for debugging

        final File file = File.createTempFile("test", ".asm");
        boolean testPassed = false;
        try {
            final MIPSCodeGenerator gen = new MIPSCodeGenerator(structDecs);
            gen.compileStatement(stmt);
            gen.writeCompleteFile(file, false);
            final String[] output = SPIMRunner.runFile(file);
            final int received = parseOutput(output);
            if (wantToSaveFile) {
                assertEquals("Expected: " + expected + " Received: " + received + " File: " +
                             file.getAbsolutePath(),
                             expected,
                             received);
            } else {
                assertEquals(expected, received);
            }
            testPassed = true;
        } finally {
            if (wantToSaveFile && testPassed) {
                file.delete();
            }
        }
    } // assertResult

    public VariableDeclarationInitializationStmt vardec(final String variableName,
                                                        final Type type,
                                                        final Exp exp) {
        return new VariableDeclarationInitializationStmt(new VariableDeclaration(type,
                                                                                 new Variable(variableName)),
                                                         exp);
    }

    public static Stmt stmts(final Stmt... stmts) {
        assert(stmts.length > 0);
        Stmt result = stmts[stmts.length - 1];

        for (int index = stmts.length - 2; index >= 0; index--) {
            result = new SequenceStmt(stmts[index], result);
        }
        return result;
    }

    public static PrintStmt printVar(final String varName) {
        return new PrintStmt(new VariableExp(new Variable(varName)));
    }

    public static AssignmentStmt assign(final String varName, final Exp exp) {
        return new AssignmentStmt(new VariableLhs(new Variable(varName)), exp);
    }

    @Test
    public void testSingleIntVariableDeclaration() throws IOException {
        assertResult(1, stmts(vardec("x", new IntType(), new IntExp(1)),
                              printVar("x")));
    }

    @Test
    public void testDoubleIntVariableDeclarationGetFirst() throws IOException {
        assertResult(1, stmts(vardec("x", new IntType(), new IntExp(1)),
                              vardec("y", new IntType(), new IntExp(2)),
                              printVar("x")));
    }

    @Test
    public void testDoubleIntVariableDeclarationGetSecond() throws IOException {
        assertResult(2, stmts(vardec("x", new IntType(), new IntExp(1)),
                              vardec("y", new IntType(), new IntExp(2)),
                              printVar("y")));
    }

    @Test
    public void testSingleIntAssignment() throws IOException {
        assertResult(2, stmts(vardec("x", new IntType(), new IntExp(1)),
                              assign("x", new IntExp(2)),
                              printVar("x")));
    }

    @Test
    public void testTwoIntsAssignFirst() throws IOException {
        assertResult(3, stmts(vardec("x", new IntType(), new IntExp(1)),
                              vardec("y", new IntType(), new IntExp(2)),
                              assign("x", new IntExp(3)),
                              printVar("x")));
    }

    @Test
    public void testTwoIntsAssignSecond() throws IOException {
        assertResult(3, stmts(vardec("x", new IntType(), new IntExp(1)),
                              vardec("y", new IntType(), new IntExp(2)),
                              assign("y", new IntExp(3)),
                              printVar("y")));
    }
}
