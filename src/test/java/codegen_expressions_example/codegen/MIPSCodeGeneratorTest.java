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
import org.junit.Rule;
import org.junit.rules.TestName;

public class MIPSCodeGeneratorTest extends MIPSCodeGeneratorTestBase<Exp> {
    protected void doCompile(final MIPSCodeGenerator gen, final Exp exp) {
        gen.compilePrintStmt(new PrintStmt(exp));
    }

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

    @Test
    public void testMalloc() throws IOException {
        // TODO: this is very bad; assumes memory address
        assertResult(268566528, new MallocExp(new IntExp(4)));
    }

    @Test
    public void testCast() throws IOException {
        assertResult((int)'a', new CastExp(new IntType(), new CharExp('a')));
    }

    @Test
    public void testDereference() throws IOException {
        // TODO: this is very bad; assumes initial value of allocated memory
        final DereferenceExp exp =
            new DereferenceExp(new MallocExp(new IntExp(4)));
        exp.setTypeAfterDereference(new IntType());
        assertResult(0, exp);
    }

    @Test
    public void testStructureAccessSingleField() throws IOException {
        // struct Foo {
        //   int x;
        // };
        // Foo(7).x
        final StructureName structName = new StructureName("Foo");
        final FieldName fieldName = new FieldName("x");
        final MakeStructureExp makeStruct =
            new MakeStructureExp(structName,
                                 new Exp[] { new IntExp(7) });
        final FieldAccessExp access = new FieldAccessExp(makeStruct, fieldName);
        final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs =
            new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
                put(structName, new LinkedHashMap<FieldName, Type>() {{
                    put(fieldName, new IntType());
                }});
            }};
        access.setExpStructure(structName);
        assertResult(7, access, structDecs);
    }

    @Test
    public void testStructureAccessTwoFieldFirst() throws IOException {
        // struct Foo {
        //   int x;
        //   int y;
        // };
        // Foo(1, 2).x
        final StructureName structName = new StructureName("Foo");
        final FieldName fieldName = new FieldName("x");
        final MakeStructureExp makeStruct =
            new MakeStructureExp(structName,
                                 new Exp[] {
                                     new IntExp(1),
                                     new IntExp(2) });
        final FieldAccessExp access = new FieldAccessExp(makeStruct, fieldName);
        final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs =
            new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
                put(structName, new LinkedHashMap<FieldName, Type>() {{
                    put(fieldName, new IntType());
                    put(new FieldName("y"), new IntType());
                }});
            }};
        access.setExpStructure(structName);
        assertResult(1, access, structDecs);
    }

    @Test
    public void testStructureAccessTwoFieldSecond() throws IOException {
        // struct Foo {
        //   int x;
        //   int y;
        // };
        // Foo(1, 2).y
        final StructureName structName = new StructureName("Foo");
        final FieldName fieldName = new FieldName("y");
        final MakeStructureExp makeStruct =
            new MakeStructureExp(structName,
                                 new Exp[] {
                                     new IntExp(1),
                                     new IntExp(2) });
        final FieldAccessExp access = new FieldAccessExp(makeStruct, fieldName);
        final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs =
            new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
                put(structName, new LinkedHashMap<FieldName, Type>() {{
                    put(new FieldName("x"), new IntType());
                    put(fieldName, new IntType());
                }});
            }};
        access.setExpStructure(structName);
        assertResult(2, access, structDecs);
    }

    @Test
    public void testStructureAccessNestedStructureFirst() throws IOException {
        // struct Bar {
        //   int x;
        //   int y;
        // };
        // struct Foo {
        //   struct Bar f;
        //   int z;
        // };
        // Foo(Bar(1, 2), 3).f.y

        final Exp baseExp =
            new MakeStructureExp(new StructureName("Foo"),
                                 new Exp[] {
                                     new MakeStructureExp(new StructureName("Bar"),
                                                          new Exp[] {
                                                              new IntExp(1),
                                                              new IntExp(2)
                                                          }),
                                     new IntExp(3)
                                 });
        final FieldAccessExp accessF =
            new FieldAccessExp(baseExp, new FieldName("f"));
        accessF.setExpStructure(new StructureName("Foo"));
        final FieldAccessExp accessY =
            new FieldAccessExp(accessF, new FieldName("y"));
        accessY.setExpStructure(new StructureName("Bar"));

        final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs =
            new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
                put(new StructureName("Bar"), new LinkedHashMap<FieldName, Type>() {{
                    put(new FieldName("x"), new IntType());
                    put(new FieldName("y"), new IntType());
                }});
                put(new StructureName("Foo"), new LinkedHashMap<FieldName, Type>() {{
                    put(new FieldName("f"), new StructureType(new StructureName("Bar")));
                    put(new FieldName("z"), new IntType());
                }});
            }};

        assertResult(2, accessY, structDecs);
    }

    @Test
    public void testStructureAccessNestedStructureSecond() throws IOException {
        // struct Bar {
        //   int x;
        //   int y;
        // };
        // struct Foo {
        //   int z;
        //   struct Bar f;
        // };
        // Foo(1, Bar(2, 3)).f.x

        final Exp baseExp =
            new MakeStructureExp(new StructureName("Foo"),
                                 new Exp[] {
                                     new IntExp(1),
                                     new MakeStructureExp(new StructureName("Bar"),
                                                          new Exp[] {
                                                              new IntExp(2),
                                                              new IntExp(3)
                                                          })
                                 });
        final FieldAccessExp accessF =
            new FieldAccessExp(baseExp, new FieldName("f"));
        accessF.setExpStructure(new StructureName("Foo"));
        final FieldAccessExp accessX =
            new FieldAccessExp(accessF, new FieldName("x"));
        accessX.setExpStructure(new StructureName("Bar"));

        final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs =
            new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
                put(new StructureName("Bar"), new LinkedHashMap<FieldName, Type>() {{
                    put(new FieldName("x"), new IntType());
                    put(new FieldName("y"), new IntType());
                }});
                put(new StructureName("Foo"), new LinkedHashMap<FieldName, Type>() {{
                    put(new FieldName("z"), new IntType());
                    put(new FieldName("f"), new StructureType(new StructureName("Bar")));
                }});
            }};

        assertResult(2, accessX, structDecs);
    }
} // MIPSCodeGeneratorTest

