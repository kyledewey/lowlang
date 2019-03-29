package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.*;
import static codegen_expressions_example.codegen.MIPSCodeGeneratorStatementTest.vardec;
import static codegen_expressions_example.codegen.MIPSCodeGeneratorStatementTest.stmts;
import static codegen_expressions_example.codegen.MIPSCodeGeneratorStatementTest.printVar;
import static codegen_expressions_example.codegen.MIPSCodeGeneratorStatementTest.assign;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

// last function is main
public class MIPSCodeGeneratorFunctionTest extends MIPSCodeGeneratorTestBase<FunctionDefinition[]> {
    protected void doCompile(final MIPSCodeGenerator gen, final FunctionDefinition[] functions) {
        assert(functions.length > 0);
        for (final FunctionDefinition def : functions) {
            gen.compileFunctionDefinition(def);
        }
        final FunctionDefinition main = functions[functions.length - 1];
        final FunctionCallExp callMain = new FunctionCallExp(main.name, new Exp[0]);
        gen.compilePrintStmt(new PrintStmt(callMain));
    }

    public static Map<FunctionName, FunctionDefinition> functionMap(final FunctionDefinition[] functions) {
        final Map<FunctionName, FunctionDefinition> result = new HashMap<FunctionName, FunctionDefinition>();
        for (final FunctionDefinition def : functions) {
            assert(!result.containsKey(def.name));
            result.put(def.name, def);
        }
        return result;
    }
    
    public void assertResultF(final int expected,
                              final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs,
                              final FunctionDefinition... functions) throws IOException {
        assertResult(expected,
                     functions,
                     structDecs,
                     functionMap(functions));
    }

    public void assertResultF(final int expected,
                              final FunctionDefinition... functions) throws IOException {
        assertResultF(expected,
                      new HashMap<StructureName, LinkedHashMap<FieldName, Type>>(),
                      functions);
    }

    @Test
    public void testPrintConstantExplicitReturn() throws IOException {
        assertResultF(1,
                      new FunctionDefinition(new VoidType(),
                                             new FunctionName("main"),
                                             new VariableDeclaration[0],
                                             stmts(new PrintStmt(new IntExp(1)),
                                                   new ReturnVoidStmt())));
    }

    @Test
    public void testPrintConstantImplicitReturn() throws IOException {
        assertResultF(1,
                      new FunctionDefinition(new VoidType(),
                                             new FunctionName("main"),
                                             new VariableDeclaration[0],
                                             new PrintStmt(new IntExp(1))));
    }
}

