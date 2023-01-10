package lowlang.codegen;

import lowlang.syntax.*;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;

public class MIPSCodeGeneratorStatementTest extends MIPSCodeGeneratorTestBase<Stmt> {
    // ---BEGIN CONSTANTS---
    public static final Map<StructureName, LinkedHashMap<FieldName, Type>> TWO_INTS =
        new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
            put(new StructureName("TwoInts"), new LinkedHashMap<FieldName, Type>() {{
                put(new FieldName("x"), new IntType());
                put(new FieldName("y"), new IntType());
            }});
        }};

    public static final Map<StructureName, LinkedHashMap<FieldName, Type>> DOUBLE_TWO_INTS =
        new HashMap<StructureName, LinkedHashMap<FieldName, Type>>() {{
            final StructureName twoInts = new StructureName("TwoInts");
            put(twoInts, TWO_INTS.get(twoInts));
            put(new StructureName("FourInts"), new LinkedHashMap<FieldName, Type>() {{
                put(new FieldName("first"), new StructureType(twoInts));
                put(new FieldName("second"), new StructureType(twoInts));
            }});
        }};
    // ---END CONSTANTS---
    
    protected void doCompile(final MIPSCodeGenerator gen, final Stmt stmt) {
        gen.setCurrentFunctionForTesting(new FunctionName("TEST"));
        gen.compileStatement(stmt);
    }

    public static VariableDeclarationInitializationStmt vardec(final String variableName,
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

    public static AssignmentStmt assign(final Lhs lhs, final Exp exp) {
        return new AssignmentStmt(lhs, exp);
    }
    
    public static AssignmentStmt assign(final String varName, final Exp exp) {
        return assign(new VariableLhs(new Variable(varName)), exp);
    }

    @Test
    public void testSingleIntVariableDeclaration() throws IOException {
        assertResult(1, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              printVar("x")));
    }

    @Test
    public void testDoubleIntVariableDeclarationGetFirst() throws IOException {
        assertResult(1, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              vardec("y", new IntType(), new IntegerLiteralExp(2)),
                              printVar("x")));
    }

    @Test
    public void testDoubleIntVariableDeclarationGetSecond() throws IOException {
        assertResult(2, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              vardec("y", new IntType(), new IntegerLiteralExp(2)),
                              printVar("y")));
    }

    @Test
    public void testSingleIntAssignment() throws IOException {
        assertResult(2, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              assign("x", new IntegerLiteralExp(2)),
                              printVar("x")));
    }

    @Test
    public void testTwoIntsAssignFirst() throws IOException {
        assertResult(3, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              vardec("y", new IntType(), new IntegerLiteralExp(2)),
                              assign("x", new IntegerLiteralExp(3)),
                              printVar("x")));
    }

    @Test
    public void testTwoIntsAssignSecond() throws IOException {
        assertResult(3, stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                              vardec("y", new IntType(), new IntegerLiteralExp(2)),
                              assign("y", new IntegerLiteralExp(3)),
                              printVar("y")));
    }

    @Test
    public void testDeclareStructureGetFirst() throws IOException {
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                         new FieldName("x"));
        final StructureName structName = new StructureName("TwoInts");
        access.expStructure = Optional.of(structName);
        assertResult(1,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           new PrintStmt(access)),
                     TWO_INTS);
    }

    @Test
    public void testDeclareStructureGetSecond() throws IOException {
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                         new FieldName("y"));
        final StructureName structName = new StructureName("TwoInts");
        access.expStructure = Optional.of(structName);
        assertResult(2,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           new PrintStmt(access)),
                     TWO_INTS);
    }

    @Test
    public void testAssignSingleStructureGetFirst() throws IOException {
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                         new FieldName("x"));
        final StructureName structName = new StructureName("TwoInts");
        access.expStructure = Optional.of(structName);
        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           assign("x",
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(3),
                                                                     new IntegerLiteralExp(4)))),
                           new PrintStmt(access)),
                     TWO_INTS);
    }
                                  
    @Test
    public void testAssignSingleStructureGetSecond() throws IOException {
        final FieldAccessExp access = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                         new FieldName("y"));
        final StructureName structName = new StructureName("TwoInts");
        access.expStructure = Optional.of(structName);
        assertResult(4,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           assign("x",
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(3),
                                                                     new IntegerLiteralExp(4)))),
                           new PrintStmt(access)),
                     TWO_INTS);
    }

    @Test
    public void testAssignStructureFieldFirst() throws IOException {
        final StructureName twoInts = new StructureName("TwoInts");

        final FieldAccessExp accessExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                            new FieldName("x"));
        accessExp.expStructure = Optional.of(twoInts);
        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("x"));
        accessLhs.lhsStructure = Optional.of(twoInts);
        
        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           assign(accessLhs,new IntegerLiteralExp(3)),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testAssignStructureFieldSecond() throws IOException {
        final StructureName twoInts = new StructureName("TwoInts");
        
        final FieldAccessExp accessExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                            new FieldName("y"));
        accessExp.expStructure = Optional.of(twoInts);
        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("y"));
        accessLhs.lhsStructure = Optional.of(twoInts);
        
        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           assign(accessLhs, new IntegerLiteralExp(3)),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testAssignNestedStructureFirst() throws IOException {
        final StructureName twoInts = new StructureName("TwoInts");
        final StructureName fourInts = new StructureName("FourInts");

        final FieldAccessExp accessFirst = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                              new FieldName("first"));
        accessFirst.expStructure = Optional.of(fourInts);

        final FieldAccessExp accessX = new FieldAccessExp(accessFirst,
                                                          new FieldName("x"));
        accessX.expStructure = Optional.of(twoInts);

        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("first"));
        accessLhs.lhsStructure = Optional.of(fourInts);
        
        assertResult(5,
                     stmts(
                           // first = TwoInts(1, 2)
                           vardec("first",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           // second = TwoInts(3, 4)
                           vardec("second",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(3),
                                                                     new IntegerLiteralExp(4)))),
                           // x = FourInts(first, second)
                           vardec("x",
                                  new StructureType(fourInts),
                                  new MakeStructureExp(fourInts,
                                                       Arrays.asList(new VariableExp(new Variable("first")),
                                                                     new VariableExp(new Variable("second"))))),
                           // x.first = TwoInts(5, 6)
                           assign(accessLhs,
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(5),
                                                                     new IntegerLiteralExp(6)))),
                           // print(x.first.x)
                           new PrintStmt(accessX)),
                     DOUBLE_TWO_INTS);
    }

    @Test
    public void testAssignNestedStructureSecond() throws IOException {
        final StructureName twoInts = new StructureName("TwoInts");
        final StructureName fourInts = new StructureName("FourInts");

        final FieldAccessExp accessSecond = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                               new FieldName("second"));
        accessSecond.expStructure = Optional.of(fourInts);

        final FieldAccessExp accessX = new FieldAccessExp(accessSecond,
                                                          new FieldName("x"));
        accessX.expStructure = Optional.of(twoInts);

        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("second"));
        accessLhs.lhsStructure = Optional.of(fourInts);
        
        assertResult(5,
                     stmts(
                           // first = TwoInts(1, 2)
                           vardec("first",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           // second = TwoInts(3, 4)
                           vardec("second",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(3),
                                                                     new IntegerLiteralExp(4)))),
                           // x = FourInts(first, second)
                           vardec("x",
                                  new StructureType(fourInts),
                                  new MakeStructureExp(fourInts,
                                                       Arrays.asList(new VariableExp(new Variable("first")),
                                                                     new VariableExp(new Variable("second"))))),
                           // x.second = TwoInts(5, 6)
                           assign(accessLhs,
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(5),
                                                                     new IntegerLiteralExp(6)))),
                           // print(x.second.x)
                           new PrintStmt(accessX)),
                     DOUBLE_TWO_INTS);
    }

    @Test
    public void testAssignIntThroughPointer() throws IOException {
        // int x = 5;
        // int* p = &x;
        // *p = 7;
        // print(x)

        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new IntType());
        
        assertResult(7,
                     stmts(vardec("x",
                                  new IntType(),
                                  new IntegerLiteralExp(5)),
                           vardec("p",
                                  new PointerType(new IntType()),
                                  new AddressOfExp(new VariableLhs(new Variable("x")))),
                           assign(deref,
                                  new IntegerLiteralExp(7)),
                           printVar("x")));
    }

    @Test
    public void testAssignFirstFieldThroughPointer() throws IOException {
        // x = TwoInts(1, 2);
        // p = &x.x;
        // *p = 3;
        // print(x.x);

        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new IntType());
        final StructureName structName = new StructureName("TwoInts");
        final FieldAccessExp accessExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                            new FieldName("x"));
        accessExp.expStructure = Optional.of(structName);
        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("x"));
        accessLhs.lhsStructure = Optional.of(structName);

        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           vardec("p",
                                  new PointerType(new IntType()),
                                  new AddressOfExp(accessLhs)),
                           assign(deref,
                                  new IntegerLiteralExp(3)),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testAssignSecondFieldThroughPointer() throws IOException {
        // x = TwoInts(1, 2);
        // p = &x.y;
        // *p = 3;
        // print(x.y);

        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new IntType());
        final StructureName structName = new StructureName("TwoInts");
        final FieldAccessExp accessExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                            new FieldName("y"));
        accessExp.expStructure = Optional.of(structName);
        final FieldAccessLhs accessLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                            new FieldName("y"));
        accessLhs.lhsStructure = Optional.of(structName);

        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           vardec("p",
                                  new PointerType(new IntType()),
                                  new AddressOfExp(accessLhs)),
                           assign(deref,
                                  new IntegerLiteralExp(3)),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testAssignStructureThroughPointer() throws IOException {
        // x = TwoInts(1, 2)
        // p = &x
        // *p = TwoInts(3, 4)
        // print(x.x)

        final StructureName structName = new StructureName("TwoInts");
        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new StructureType(structName));
        final FieldAccessExp accessExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                            new FieldName("x"));
        accessExp.expStructure = Optional.of(structName);

        assertResult(3,
                     stmts(vardec("x",
                                  new StructureType(structName),
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           vardec("p",
                                  new PointerType(new StructureType(structName)),
                                  new AddressOfExp(new VariableLhs(new Variable("x")))),
                           assign(deref,
                                  new MakeStructureExp(structName,
                                                       Arrays.asList(new IntegerLiteralExp(3),
                                                                     new IntegerLiteralExp(4)))),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testAssignNestedStructureThroughPointerFirst() throws IOException {
        // x = FourInts(TwoInts(1, 2), TwoInts(3, 4));
        // p = &x.first;
        // *p = TwoInts(5, 6);
        // print(x.first.y)

        final StructureName twoInts = new StructureName("TwoInts");
        final StructureName fourInts = new StructureName("FourInts");
        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new StructureType(twoInts));
        final FieldAccessExp accessFirstExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                                 new FieldName("first"));
        accessFirstExp.expStructure = Optional.of(fourInts);
        final FieldAccessExp accessYExp = new FieldAccessExp(accessFirstExp,
                                                             new FieldName("y"));
        accessYExp.expStructure = Optional.of(twoInts);
        final FieldAccessLhs accessFirstLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                                 new FieldName("first"));
        accessFirstLhs.lhsStructure = Optional.of(fourInts);

        assertResult(6,
                     stmts(vardec("x",
                                  new StructureType(fourInts),
                                  new MakeStructureExp(fourInts,
                                                       Arrays.asList(new MakeStructureExp(twoInts,
                                                                                          Arrays.asList(new IntegerLiteralExp(1),
                                                                                                        new IntegerLiteralExp(2))),
                                                                     new MakeStructureExp(twoInts,
                                                                                          Arrays.asList(new IntegerLiteralExp(3),
                                                                                                        new IntegerLiteralExp(4)))))),
                           vardec("p",
                                  new PointerType(new StructureType(twoInts)),
                                  new AddressOfExp(accessFirstLhs)),
                           assign(deref,
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(5),
                                                                     new IntegerLiteralExp(6)))),
                           new PrintStmt(accessYExp)),
                     DOUBLE_TWO_INTS);
    }

    @Test
    public void testAssignNestedStructureThroughPointerSecond() throws IOException {
        // x = FourInts(TwoInts(1, 2), TwoInts(3, 4));
        // p = &x.second;
        // *p = TwoInts(5, 6);
        // print(x.second.x)

        final StructureName twoInts = new StructureName("TwoInts");
        final StructureName fourInts = new StructureName("FourInts");
        final DereferenceLhs deref = new DereferenceLhs(new VariableLhs(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new StructureType(twoInts));
        final FieldAccessExp accessSecondExp = new FieldAccessExp(new VariableExp(new Variable("x")),
                                                                  new FieldName("second"));
        accessSecondExp.expStructure = Optional.of(fourInts);
        final FieldAccessExp accessYExp = new FieldAccessExp(accessSecondExp,
                                                             new FieldName("x"));
        accessYExp.expStructure = Optional.of(twoInts);
        final FieldAccessLhs accessSecondLhs = new FieldAccessLhs(new VariableLhs(new Variable("x")),
                                                                  new FieldName("second"));
        accessSecondLhs.lhsStructure = Optional.of(fourInts);

        assertResult(5,
                     stmts(vardec("x",
                                  new StructureType(fourInts),
                                  new MakeStructureExp(fourInts,
                                                       Arrays.asList(new MakeStructureExp(twoInts,
                                                                                          Arrays.asList(new IntegerLiteralExp(1),
                                                                                                        new IntegerLiteralExp(2))),
                                                                     new MakeStructureExp(twoInts,
                                                                                          Arrays.asList(new IntegerLiteralExp(3),
                                                                                                        new IntegerLiteralExp(4)))))),
                           vardec("p",
                                  new PointerType(new StructureType(twoInts)),
                                  new AddressOfExp(accessSecondLhs)),
                           assign(deref,
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(5),
                                                                     new IntegerLiteralExp(6)))),
                           new PrintStmt(accessYExp)),
                     DOUBLE_TWO_INTS);
    }

    @Test
    public void testDereferenceInt() throws IOException {
        // x = 5;
        // p = &x;
        // print(*p);

        final DereferenceExp deref =
            new DereferenceExp(new VariableExp(new Variable("p")));
        deref.typeAfterDereference = Optional.of(new IntType());

        assertResult(5,
                     stmts(vardec("x",
                                  new IntType(),
                                  new IntegerLiteralExp(5)),
                           vardec("p",
                                  new PointerType(new IntType()),
                                  new AddressOfExp(new VariableLhs(new Variable("x")))),
                           new PrintStmt(deref)));
    }

    @Test
    public void testDereferenceStructure() throws IOException {
        // x = TwoInts(1, 2);
        // p = &x;
        // print((*p).y);

        final StructureName twoInts = new StructureName("TwoInts");
        final DereferenceExp derefExp =
            new DereferenceExp(new VariableExp(new Variable("p")));
        derefExp.typeAfterDereference = Optional.of(new StructureType(twoInts));

        final FieldAccessExp accessExp =
            new FieldAccessExp(derefExp, new FieldName("y"));
        accessExp.expStructure = Optional.of(twoInts);

        assertResult(2,
                     stmts(vardec("x",
                                  new StructureType(twoInts),
                                  new MakeStructureExp(twoInts,
                                                       Arrays.asList(new IntegerLiteralExp(1),
                                                                     new IntegerLiteralExp(2)))),
                           vardec("p",
                                  new PointerType(new StructureType(twoInts)),
                                  new AddressOfExp(new VariableLhs(new Variable("x")))),
                           new PrintStmt(accessExp)),
                     TWO_INTS);
    }

    @Test
    public void testIfTrue() throws IOException {
        // if (true) {
        //   print(1);
        // } else {
        //   print(2);
        // }

        assertResult(1,
                     new IfStmt(new BooleanLiteralExp(true),
                                new PrintStmt(new IntegerLiteralExp(1)),
                                new PrintStmt(new IntegerLiteralExp(2))));
    }

    @Test
    public void testIfFalse() throws IOException {
        // if (false) {
        //   print(1);
        // } else {
        //   print(2);
        // }

        assertResult(2,
                     new IfStmt(new BooleanLiteralExp(false),
                                new PrintStmt(new IntegerLiteralExp(1)),
                                new PrintStmt(new IntegerLiteralExp(2))));
    }

    // ---BEGIN CODE FOR NESTED IF TESTS---
    private static Exp lt(final int num) {
        return new BinopExp(new IntegerLiteralExp(num),
                            new LessThanOp(),
                            new VariableExp(new Variable("x")));
    }
    
    public static final IfStmt NESTED_IF =
        new IfStmt(lt(89),
                   new PrintStmt(new IntegerLiteralExp(1)),
                   new IfStmt(lt(79),
                              new PrintStmt(new IntegerLiteralExp(2)),
                              new IfStmt(lt(69),
                                         new PrintStmt(new IntegerLiteralExp(3)),
                                         new IfStmt(lt(59),
                                                    new PrintStmt(new IntegerLiteralExp(4)),
                                                    new PrintStmt(new IntegerLiteralExp(5))))));

    @Test
    public void testNestedIfFirst() throws IOException {
        // int x = 90;
        // if (89 < x) {
        //   print(1);
        // } else if (79 < x) {
        //   print(2);
        // } else if (69 < x) {
        //   print(3);
        // } else if (59 < x) {
        //   print(4);
        // } else {
        //   print(5);
        // }

        assertResult(1,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(90)),
                           NESTED_IF));
    }

    @Test
    public void testNestedIfSecond() throws IOException {
        // int x = 80;
        // if (89 < x) {
        //   print(1);
        // } else if (79 < x) {
        //   print(2);
        // } else if (69 < x) {
        //   print(3);
        // } else if (59 < x) {
        //   print(4);
        // } else {
        //   print(5);
        // }

        assertResult(2,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(80)),
                           NESTED_IF));
    }

    @Test
    public void testNestedIfThird() throws IOException {
        // int x = 70;
        // if (89 < x) {
        //   print(1);
        // } else if (79 < x) {
        //   print(2);
        // } else if (69 < x) {
        //   print(3);
        // } else if (59 < x) {
        //   print(4);
        // } else {
        //   print(5);
        // }

        assertResult(3,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(70)),
                           NESTED_IF));
    }

    @Test
    public void testNestedIfFourth() throws IOException {
        // int x = 60;
        // if (89 < x) {
        //   print(1);
        // } else if (79 < x) {
        //   print(2);
        // } else if (69 < x) {
        //   print(3);
        // } else if (59 < x) {
        //   print(4);
        // } else {
        //   print(5);
        // }

        assertResult(4,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(60)),
                           NESTED_IF));
    }

    @Test
    public void testNestedIfFifth() throws IOException {
        // int x = 50;
        // if (89 < x) {
        //   print(1);
        // } else if (79 < x) {
        //   print(2);
        // } else if (69 < x) {
        //   print(3);
        // } else if (59 < x) {
        //   print(4);
        // } else {
        //   print(5);
        // }

        assertResult(5,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(50)),
                           NESTED_IF));
    }
    // ---END CODE FOR NESTED IF TESTS---

    @Test
    public void testIfComplexGuardTrue() throws IOException {
        // if (0 < TwoInts(1, 2).x) {
        //   print(1);
        // } else {
        //   print(2);
        // }

        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new MakeStructureExp(twoInts,
                                                                              Arrays.asList(new IntegerLiteralExp(1),
                                                                                            new IntegerLiteralExp(2))),
                                                         new FieldName("x"));
        access.expStructure = Optional.of(twoInts);
        
        final Exp guard = new BinopExp(new IntegerLiteralExp(0),
                                       new LessThanOp(),
                                       access);
        assertResult(1,
                     new IfStmt(guard,
                                new PrintStmt(new IntegerLiteralExp(1)),
                                new PrintStmt(new IntegerLiteralExp(2))),
                     TWO_INTS);
    }

    @Test
    public void testIfComplexGuardFalse() throws IOException {
        // if (TwoInts(1, 2).x < 0) {
        //   print(1);
        // } else {
        //   print(2);
        // }

        final StructureName twoInts = new StructureName("TwoInts");
        final FieldAccessExp access = new FieldAccessExp(new MakeStructureExp(twoInts,
                                                                              Arrays.asList(new IntegerLiteralExp(1),
                                                                                            new IntegerLiteralExp(2))),
                                                         new FieldName("x"));
        access.expStructure = Optional.of(twoInts);
        
        final Exp guard = new BinopExp(access,
                                       new LessThanOp(),
                                       new IntegerLiteralExp(0));
        assertResult(2,
                     new IfStmt(guard,
                                new PrintStmt(new IntegerLiteralExp(1)),
                                new PrintStmt(new IntegerLiteralExp(2))),
                     TWO_INTS);
    }

    @Test
    public void testWhileInitiallyFalse() throws IOException {
        // int x = 0;
        // while (x < 0) {
        //   x = x + 1;
        // }
        // print(x);

        final Variable x = new Variable("x");
        assertResult(0,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BinopExp(new VariableExp(x),
                                                      new LessThanOp(),
                                                      new IntegerLiteralExp(0)),
                                         assign("x", new BinopExp(new VariableExp(x),
                                                                  new PlusOp(),
                                                                  new IntegerLiteralExp(1)))),
                           printVar("x")));
    }

    @Test
    public void testWhileInitiallyTrue() throws IOException {
        // int x = 0;
        // while (x < 10) {
        //   x = x + 1;
        // }
        // print(x);

        final Variable x = new Variable("x");
        assertResult(10,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BinopExp(new VariableExp(x),
                                                      new LessThanOp(),
                                                      new IntegerLiteralExp(10)),
                                         assign("x", new BinopExp(new VariableExp(x),
                                                                  new PlusOp(),
                                                                  new IntegerLiteralExp(1)))),
                           printVar("x")));
    }

    @Test
    public void testUnconditionalBreak() throws IOException {
        // int x = 0;
        // while (x < 10) {
        //   x = x + 1;
        //   break;
        // }
        // print(x);

        final Variable x = new Variable("x");
        assertResult(1,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BinopExp(new VariableExp(x),
                                                      new LessThanOp(),
                                                      new IntegerLiteralExp(10)),
                                         stmts(assign("x", new BinopExp(new VariableExp(x),
                                                                        new PlusOp(),
                                                                        new IntegerLiteralExp(1))),
                                               new BreakStmt())),
                           printVar("x")));
    }

    @Test
    public void testUnconditionalContinue() throws IOException {
        // int x = 0;
        // int y = 0;
        // while (x < 10) {
        //   x = x + 1;
        //   continue;
        //   y = y + 1;
        // }
        // print(y);

        final Variable x = new Variable("x");
        final Variable y = new Variable("y");
        assertResult(0,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           vardec("y", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BinopExp(new VariableExp(x),
                                                      new LessThanOp(),
                                                      new IntegerLiteralExp(10)),
                                         stmts(assign("x", new BinopExp(new VariableExp(x),
                                                                        new PlusOp(),
                                                                        new IntegerLiteralExp(1))),
                                               new ContinueStmt(),
                                               assign("y", new BinopExp(new VariableExp(y),
                                                                        new PlusOp(),
                                                                        new IntegerLiteralExp(1))))),
                           printVar("y")));
    }

    @Test
    public void testConditionalBreakContinue() throws IOException {
        // int x = 0;
        // while (true) {
        //   x = x + 1;
        //   if (10 < x) {
        //     break;
        //   } else {
        //     continue;
        //   }
        // }
        // print(x);

        final Variable x = new Variable("x");
        assertResult(11,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BooleanLiteralExp(true),
                                         stmts(assign("x", new BinopExp(new VariableExp(x),
                                                                        new PlusOp(),
                                                                        new IntegerLiteralExp(1))),
                                               new IfStmt(new BinopExp(new IntegerLiteralExp(10),
                                                                       new LessThanOp(),
                                                                       new VariableExp(x)),
                                                          new BreakStmt(),
                                                          new ContinueStmt()))),
                           printVar("x")));
    }

    @Test
    public void testIfScope() throws IOException {
        // int x = 0;
        // if (true) {
        //   int x = 1;
        // } else {
        //   int x = 2;
        // }
        // print(x);

        assertResult(0,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new IfStmt(new BooleanLiteralExp(true),
                                      vardec("x", new IntType(), new IntegerLiteralExp(1)),
                                      vardec("x", new IntType(), new IntegerLiteralExp(2))),
                           printVar("x")));
    }

    @Test
    public void testWhileScope() throws IOException {
        // int x = 0;
        // while (true) {
        //   int x = 1;
        //   break;
        // }
        // print(x);

        assertResult(0,
                     stmts(vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BooleanLiteralExp(true),
                                         stmts(vardec("x", new IntType(), new IntegerLiteralExp(1)),
                                               new BreakStmt())),
                           printVar("x")));
    }

    @Test
    public void testNestedWhile() throws IOException {
        // int result = 0;
        // int x = 0;
        // while (x < 5) {
        //   int y = 0;
        //   while (y < 5) {
        //     result = result + 1;
        //     y = y + 1;
        //   }
        //   x = x + 1;
        // }
        // print(result);

        final Variable result = new Variable("result");
        final Variable x = new Variable("x");
        final Variable y = new Variable("y");

        assertResult(25,
                     stmts(vardec("result", new IntType(), new IntegerLiteralExp(0)),
                           vardec("x", new IntType(), new IntegerLiteralExp(0)),
                           new WhileStmt(new BinopExp(new VariableExp(x),
                                                      new LessThanOp(),
                                                      new IntegerLiteralExp(5)),
                                         stmts(vardec("y", new IntType(), new IntegerLiteralExp(0)),
                                               new WhileStmt(new BinopExp(new VariableExp(y),
                                                                          new LessThanOp(),
                                                                          new IntegerLiteralExp(5)),
                                                             stmts(assign("result",
                                                                          new BinopExp(new VariableExp(result),
                                                                                       new PlusOp(),
                                                                                       new IntegerLiteralExp(1))),
                                                                   assign("y",
                                                                          new BinopExp(new VariableExp(y),
                                                                                       new PlusOp(),
                                                                                       new IntegerLiteralExp(1))))),
                                               assign("x", new BinopExp(new VariableExp(x),
                                                                        new PlusOp(),
                                                                        new IntegerLiteralExp(1))))),
                           printVar("result")));
    }
}
