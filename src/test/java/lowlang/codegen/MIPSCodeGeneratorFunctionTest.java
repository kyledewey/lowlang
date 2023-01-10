package lowlang.codegen;

import lowlang.syntax.*;
import static lowlang.codegen.MIPSCodeGeneratorStatementTest.vardec;
import static lowlang.codegen.MIPSCodeGeneratorStatementTest.stmts;
import static lowlang.codegen.MIPSCodeGeneratorStatementTest.printVar;
import static lowlang.codegen.MIPSCodeGeneratorStatementTest.assign;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

// last function is main, which is assumed a void return type
public class MIPSCodeGeneratorFunctionTest extends MIPSCodeGeneratorTestBase<FunctionDefinition[]> {
    protected void doCompile(final MIPSCodeGenerator gen, final FunctionDefinition[] functions) {
        assert(functions.length > 0);

        // main needs to be first so we fall into it
        final FunctionDefinition main = functions[functions.length - 1];
        gen.compileMainFunctionDefinition(main);
        for (int index = 0; index < functions.length - 1; index++) {
            gen.compileFunctionDefinition(functions[index]);
        }
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

    public static FunctionDefinition mkMain(final Stmt stmt) {
        return new FunctionDefinition(new VoidType(),
                                      new FunctionName("main"),
                                      new VariableDeclaration[0],
                                      stmt);
    }
    @Test
    public void testPrintConstantExplicitReturn() throws IOException {
        assertResultF(1,
                      mkMain(stmts(new PrintStmt(new IntegerLiteralExp(1)),
                                   new ReturnVoidStmt())));
    }

    @Test
    public void testPrintConstantImplicitReturn() throws IOException {
        assertResultF(1,
                      mkMain(new PrintStmt(new IntegerLiteralExp(1))));
    }

    @Test
    public void testCallFunctionReturnsConstantInt() throws IOException {
        final FunctionName foo = new FunctionName("foo");
        assertResultF(1,
                      new FunctionDefinition(new IntType(),
                                             foo,
                                             new VariableDeclaration[0],
                                             new ReturnExpStmt(new IntegerLiteralExp(1))),
                      mkMain(new PrintStmt(new FunctionCallExp(foo, new Exp[0]))));
    }

    @Test
    public void testCallFunctionAddsParams() throws IOException {
        final FunctionName foo = new FunctionName("foo");
        final Variable x = new Variable("x");
        final Variable y = new Variable("y");
        assertResultF(3,
                      new FunctionDefinition(new IntType(),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new IntType(), x),
                                                 new VariableDeclaration(new IntType(), y)
                                             },
                                             new ReturnExpStmt(new BinopExp(new VariableExp(x),
                                                                            new PlusOp(),
                                                                            new VariableExp(y)))),
                      mkMain(new PrintStmt(new FunctionCallExp(foo,
                                                               new Exp[] {
                                                                   new IntegerLiteralExp(1),
                                                                   new IntegerLiteralExp(2)
                                                               }))));
    }

    @Test
    public void testReturnStructureConstantGetFirst() throws IOException {
        // TwoInts foo() {
        //   return TwoInts(1, 2);
        // }
        // void main() {
        //   print(foo().x);
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new FunctionCallExp(foo, new Exp[0]),
                                                         new FieldName("x"));
        access.setExpStructure(twoInts);
        
        assertResultF(1,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new StructureType(twoInts),
                                             foo,
                                             new VariableDeclaration[0],
                                             new ReturnExpStmt(new MakeStructureExp(twoInts,
                                                                                    new Exp[] {
                                                                                        new IntegerLiteralExp(1),
                                                                                        new IntegerLiteralExp(2)
                                                                                    }))),
                      mkMain(new PrintStmt(access)));
    }

    @Test
    public void testReturnStructureConstantGetSecond() throws IOException {
        // TwoInts foo() {
        //   return TwoInts(1, 2);
        // }
        // void main() {
        //   print(foo().y);
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new FunctionCallExp(foo, new Exp[0]),
                                                         new FieldName("y"));
        access.setExpStructure(twoInts);
        
        assertResultF(2,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new StructureType(twoInts),
                                             foo,
                                             new VariableDeclaration[0],
                                             new ReturnExpStmt(new MakeStructureExp(twoInts,
                                                                                    new Exp[] {
                                                                                        new IntegerLiteralExp(1),
                                                                                        new IntegerLiteralExp(2)
                                                                                    }))),
                      mkMain(new PrintStmt(access)));
    }

    @Test
    public void testReturnStructureParamsGetFirst() throws IOException {
        // TwoInts foo(int a, int b) {
        //   return TwoInts(a, b);
        // }
        // void main() {
        //   print(foo(1, 2).x);
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new FunctionCallExp(foo,
                                                                             new Exp[] {
                                                                                 new IntegerLiteralExp(1),
                                                                                 new IntegerLiteralExp(2)
                                                                             }),
                                                         new FieldName("x"));
        access.setExpStructure(twoInts);

        final Variable a = new Variable("a");
        final Variable b = new Variable("b");
        
        assertResultF(1,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new StructureType(twoInts),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new IntType(), a),
                                                 new VariableDeclaration(new IntType(), b)
                                             },
                                             new ReturnExpStmt(new MakeStructureExp(twoInts,
                                                                                    new Exp[] {
                                                                                        new VariableExp(a),
                                                                                        new VariableExp(b)
                                                                                    }))),
                      mkMain(new PrintStmt(access)));
    }

    @Test
    public void testReturnStructureParamsGetSecond() throws IOException {
        // TwoInts foo(int a, int b) {
        //   return TwoInts(a, b);
        // }
        // void main() {
        //   print(foo(1, 2).y);
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new FunctionCallExp(foo,
                                                                             new Exp[] {
                                                                                 new IntegerLiteralExp(1),
                                                                                 new IntegerLiteralExp(2)
                                                                             }),
                                                         new FieldName("y"));
        access.setExpStructure(twoInts);

        final Variable a = new Variable("a");
        final Variable b = new Variable("b");
        
        assertResultF(2,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new StructureType(twoInts),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new IntType(), a),
                                                 new VariableDeclaration(new IntType(), b)
                                             },
                                             new ReturnExpStmt(new MakeStructureExp(twoInts,
                                                                                    new Exp[] {
                                                                                        new VariableExp(a),
                                                                                        new VariableExp(b)
                                                                                    }))),
                      mkMain(new PrintStmt(access)));
    }

    @Test
    public void testCanTakeStructureGetFirst() throws IOException {
        // void foo(TwoInts s) {
        //   print(s.x);
        // }
        // void main() {
        //   foo(TwoInts(1, 2));
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final Variable s = new Variable("s");
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(s),
                                                         new FieldName("x"));
        access.setExpStructure(twoInts);

        assertResultF(1,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new VoidType(),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new StructureType(twoInts), s)
                                             },
                                             new PrintStmt(access)),
                      mkMain(new FunctionCallStmt(foo,
                                                  new Exp[] {
                                                      new MakeStructureExp(twoInts,
                                                                           new Exp[] {
                                                                               new IntegerLiteralExp(1),
                                                                               new IntegerLiteralExp(2)
                                                                           })
                                                  })));
    }

    @Test
    public void testCanTakeStructureGetSecond() throws IOException {
        // void foo(TwoInts s) {
        //   print(s.y);
        // }
        // void main() {
        //   foo(TwoInts(1, 2));
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final Variable s = new Variable("s");
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(s),
                                                         new FieldName("y"));
        access.setExpStructure(twoInts);

        assertResultF(2,
                      MIPSCodeGeneratorStatementTest.TWO_INTS,
                      new FunctionDefinition(new VoidType(),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new StructureType(twoInts), s)
                                             },
                                             new PrintStmt(access)),
                      mkMain(new FunctionCallStmt(foo,
                                                  new Exp[] {
                                                      new MakeStructureExp(twoInts,
                                                                           new Exp[] {
                                                                               new IntegerLiteralExp(1),
                                                                               new IntegerLiteralExp(2)
                                                                           })
                                                  })));
    }

    @Test
    public void testCanTakeMultipleStructures() throws IOException {
        // FourInts foo(TwoInts x, TwoInts y) {
        //   return FourInts(x, y);
        // }
        // void main() {
        //   FourInts f = foo(TwoInts(1, 2), TwoInts(3, 4));
        //   print(f.second.x);
        // }

        final FunctionName foo = new FunctionName("foo");
        final StructureName twoInts = new StructureName("TwoInts");
        final StructureName fourInts = new StructureName("FourInts");
        final Variable x = new Variable("x");
        final Variable y = new Variable("y");
        final Variable f = new Variable("f");

        final FieldAccessExp accessSecond = new FieldAccessExp(new VariableExp(f),
                                                               new FieldName("second"));
        accessSecond.setExpStructure(fourInts);
        final FieldAccessExp accessX = new FieldAccessExp(accessSecond,
                                                          new FieldName("x"));
        accessX.setExpStructure(twoInts);
        
        assertResultF(3,
                      MIPSCodeGeneratorStatementTest.DOUBLE_TWO_INTS,
                      new FunctionDefinition(new StructureType(fourInts),
                                             foo,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new StructureType(twoInts), x),
                                                 new VariableDeclaration(new StructureType(twoInts), y)
                                             },
                                             new ReturnExpStmt(new MakeStructureExp(fourInts,
                                                                                    new Exp[] {
                                                                                        new VariableExp(x),
                                                                                        new VariableExp(y)
                                                                                    }))),
                      mkMain(stmts(vardec("f",
                                          new StructureType(fourInts),
                                          new FunctionCallExp(foo,
                                                              new Exp[] {
                                                                  new MakeStructureExp(twoInts,
                                                                                       new Exp[] {
                                                                                           new IntegerLiteralExp(1),
                                                                                           new IntegerLiteralExp(2)
                                                                                       }),
                                                                  new MakeStructureExp(twoInts,
                                                                                       new Exp[] {
                                                                                           new IntegerLiteralExp(3),
                                                                                           new IntegerLiteralExp(4)
                                                                                       })
                                                              })),
                                   new PrintStmt(accessX))));
    }

    @Test
    public void testFibonacci() throws IOException {
        // int fib(int x) {
        //   if (x == 0) {
        //     return 0;
        //   } else if (x == 1) {
        //     return 1;
        //   } else {
        //     return fib(x - 1) + fib(x - 2);
        //   }
        // }
        // void main() {
        //   print(fib(7));
        // }

        final FunctionName fib = new FunctionName("fib");
        final Variable x = new Variable("x");
        final Exp recursiveCase = new BinopExp(new FunctionCallExp(fib,
                                                                   new Exp[] {
                                                                       new BinopExp(new VariableExp(x),
                                                                                    new MinusOp(),
                                                                                    new IntegerLiteralExp(1))
                                                                   }),
                                               new PlusOp(),
                                               new FunctionCallExp(fib,
                                                                   new Exp[] {
                                                                       new BinopExp(new VariableExp(x),
                                                                                    new MinusOp(),
                                                                                    new IntegerLiteralExp(2))
                                                                   }));
        assertResultF(13,
                      new FunctionDefinition(new IntType(),
                                             fib,
                                             new VariableDeclaration[] {
                                                 new VariableDeclaration(new IntType(), x)
                                             },
                                             new IfStmt(new BinopExp(new VariableExp(x),
                                                                     new EqualsOp(),
                                                                     new IntegerLiteralExp(0)),
                                                        new ReturnExpStmt(new IntegerLiteralExp(0)),
                                                        new IfStmt(new BinopExp(new VariableExp(x),
                                                                                new EqualsOp(),
                                                                                new IntegerLiteralExp(1)),
                                                                   new ReturnExpStmt(new IntegerLiteralExp(1)),
                                                                   new ReturnExpStmt(recursiveCase)))),
                      mkMain(new PrintStmt(new FunctionCallExp(fib,
                                                               new Exp[] {
                                                                   new IntegerLiteralExp(7)
                                                               }))));
    }
}

