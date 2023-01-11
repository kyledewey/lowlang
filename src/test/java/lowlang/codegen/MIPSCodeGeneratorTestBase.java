package lowlang.codegen;

import lowlang.parser.*;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestName;

public abstract class MIPSCodeGeneratorTestBase<A> {
    @Rule public TestName name = new TestName();

    public int parseOutput(final String[] spimOutput) {
        assert(spimOutput.length == 1);
        return Integer.parseInt(spimOutput[0]);
    } // parseOutput

    public void assertResult(final int expected,
                             final A compileMe,
                             final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs,
                             final Map<FunctionName, FunctionDefinition> functionDefs) throws IOException {
        boolean wantToSaveFile = true; // for debugging

        final File file = File.createTempFile(name.getMethodName(),
                                              ".asm",
                                              new File("testPrograms"));
        boolean testPassed = false;
        try {
            final MIPSCodeGenerator gen = new MIPSCodeGenerator(structDecs, functionDefs);
            doCompile(gen, compileMe);
            gen.writeCompleteFile(file);
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
            if (!wantToSaveFile || testPassed) {
                file.delete();
            }
        }
    }

    public void assertResult(final int expected,
                             final A compileMe,
                             final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs) throws IOException {
        assertResult(expected, compileMe, structDecs, new HashMap<FunctionName, FunctionDefinition>());
    }

    public void assertResult(final int expected,
                             final A compileMe) throws IOException {
        assertResult(expected, compileMe, new HashMap<StructureName, LinkedHashMap<FieldName, Type>>());
    }
    
    // ---BEGIN ABSTRACT METHODS---
    protected abstract void doCompile(MIPSCodeGenerator gen, A input);
    // ---END ABSTRACT METHODS---
} // MIPSCodeGeneratorTestBase
