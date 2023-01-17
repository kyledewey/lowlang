package lowlang.typechecker;

import lowlang.parser.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

public class TypecheckerScopeTest {
    public static final List<StructureDeclaration> EMPTY_STRUCTURES =
        new ArrayList<StructureDeclaration>();
    public static final List<FunctionDefinition> EMPTY_FUNCTIONS =
        new ArrayList<FunctionDefinition>();
    public static final List<VariableDeclaration> EMPTY_VARDECS =
        new ArrayList<VariableDeclaration>();
    
    public static Stmt stmts(final Stmt... input) {
        return new BlockStmt(Arrays.asList(input));
    }

    public static VariableDeclarationInitializationStmt def(final Type type, final String name, final Exp exp) {
        return new VariableDeclarationInitializationStmt(new VariableDeclaration(type, new Variable(name)), exp);
    }

    // void foo() {
    //   body
    // }
    public static FunctionDefinition voidFunction(final Stmt body) {
        return new FunctionDefinition(new VoidType(),
                                      new FunctionName("foo"),
                                      EMPTY_VARDECS,
                                      Arrays.asList(body));
    }
    
    @Test
    public void testVariableDefinitionAndUse() throws TypeErrorException {
        // void foo() {
        //   int x = 0;
        //   int y = x;
        // }
        final Stmt body = stmts(def(new IntType(), "x", new IntegerLiteralExp(0)),
                                def(new IntType(), "y", new VariableExp(new Variable("x"))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testAccessUndeclaredVariable() throws TypeErrorException {
        // void foo() {
        //   int x = x;
        // }
        final Stmt body = stmts(def(new IntType(), "x", new VariableExp(new Variable("x"))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testPointerToVariable() throws TypeErrorException {
        // void foo() {
        //   int x = 0;
        //   int* y = &x;
        // }
        final Stmt body = stmts(def(new IntType(), "x", new IntegerLiteralExp(0)),
                                def(new PointerType(new IntType()),
                                    "y",
                                    new AddressOfExp(new VariableLhs(new Variable("x")))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testPointerDereferenceRhs() throws TypeErrorException {
        // void foo() {
        //   int x = 0;
        //   int* p = &x;
        //   int y = *p;
        // }

        final Stmt body = stmts(def(new IntType(), "x", new IntegerLiteralExp(0)),
                                def(new PointerType(new IntType()),
                                    "p",
                                    new AddressOfExp(new VariableLhs(new Variable("x")))),
                                def(new IntType(),
                                    "y",
                                    new DereferenceExp(new VariableExp(new Variable("p")))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testPointerDereferenceLhs() throws TypeErrorException {
        // void foo() {
        //   int x = 0;
        //   int* p = &x;
        //   *p = 7;
        // }

        final Stmt body = stmts(def(new IntType(), "x", new IntegerLiteralExp(0)),
                                def(new PointerType(new IntType()),
                                    "p",
                                    new AddressOfExp(new VariableLhs(new Variable("x")))),
                                new AssignmentStmt(new DereferenceLhs(new VariableLhs(new Variable("p"))),
                                                   new IntegerLiteralExp(7)));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testAddPointer() throws TypeErrorException {
        // void foo() {
        //   int x = 0;
        //   int* y = &x;
        //   int* z = y + 3;
        // }
        final Stmt body = stmts(def(new IntType(), "x", new IntegerLiteralExp(0)),
                                def(new PointerType(new IntType()),
                                    "y",
                                    new AddressOfExp(new VariableLhs(new Variable("x")))),
                                def(new PointerType(new IntType()),
                                    "z",
                                    new BinopExp(new VariableExp(new Variable("y")),
                                                 new PlusOp(),
                                                 new IntegerLiteralExp(3))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testNormalStructureCreation() throws TypeErrorException {
        // Foo {
        //   int x;
        //   int y;
        // };
        // void foo() {
        //   Foo f = Foo(7, 8);
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new IntType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new IntegerLiteralExp(8)))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureDuplicateFields() throws TypeErrorException {
        // Foo {
        //   int x;
        //   int x;
        // };
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new IntType(), new Variable("x"))));
        final Program prog = new Program(Arrays.asList(sdef),
                                         EMPTY_FUNCTIONS);
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureDuplicateStructureNames() throws TypeErrorException {
        // Foo { int x; };
        // Foo { int x; };

        final StructureDeclaration sdef =
            new StructureDeclaration(new StructureName("Foo"),
                                     Arrays.asList(new VariableDeclaration(new IntType(),
                                                                           new Variable("x"))));
        final Program prog = new Program(Arrays.asList(sdef, sdef),
                                         EMPTY_FUNCTIONS);
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureNonExistentStructureField() throws TypeErrorException {
        // Foo { Bar x; }

        final StructureDeclaration sdef =
            new StructureDeclaration(new StructureName("Foo"),
                                     Arrays.asList(new VariableDeclaration(new StructureType(new StructureName("Bar")),
                                                                           new Variable("x"))));
        final Program prog = new Program(Arrays.asList(sdef),
                                         EMPTY_FUNCTIONS);
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureVoidField() throws TypeErrorException {
        // Foo {
        //   int x;
        //   void y;
        // };
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new VoidType(), new Variable("y"))));
        final Program prog = new Program(Arrays.asList(sdef),
                                         EMPTY_FUNCTIONS);
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureCreationTooManyParams() throws TypeErrorException {
        // Foo {
        //   int x;
        //   int y;
        // };
        // void foo() {
        //   Foo f = Foo(7, 8, 9);
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new IntType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new IntegerLiteralExp(8),
                                                                       new IntegerLiteralExp(9)))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureCreationWrongParamTypes() throws TypeErrorException {
        // Foo {
        //   int x;
        //   bool y;
        // };
        // void foo() {
        //   Foo f = Foo(true, 7);
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new BoolType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new BooleanLiteralExp(true),
                                                                       new IntegerLiteralExp(7)))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testNormalStructureAccess() throws TypeErrorException {
        // Foo {
        //   int x;
        //   bool y;
        // };
        // void foo() {
        //   Foo f = Foo(7, true);
        //   int g = f.x;
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new BoolType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))),
                                def(new IntType(),
                                    "g",
                                    new FieldAccessExp(new VariableExp(new Variable("f")),
                                                       new FieldName("x"))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testNormalStructureFieldAssignment() throws TypeErrorException {
        // Foo {
        //   int x;
        //   bool y;
        // };
        // void foo() {
        //   Foo f = Foo(7, true);
        //   f.x = 8;
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new BoolType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))),
                                new AssignmentStmt(new FieldAccessLhs(new VariableLhs(new Variable("f")),
                                                                      new FieldName("x")),
                                                   new IntegerLiteralExp(8)));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testCreateNonexistentStructure() throws TypeErrorException {
        // void foo() {
        //   Foo f = Foo(1);
        // }

        final StructureName sname = new StructureName("Foo");
        final FunctionDefinition foo =
            voidFunction(def(new StructureType(sname),
                             "f",
                             new MakeStructureExp(sname,
                                                  Arrays.asList(new IntegerLiteralExp(1)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureAccessNonexistentField() throws TypeErrorException {
        // Foo {
        //   int x;
        //   bool y;
        // };
        // void foo() {
        //   Foo f = Foo(7, true);
        //   int g = f.z;
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new BoolType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))),
                                def(new IntType(),
                                    "g",
                                    new FieldAccessExp(new VariableExp(new Variable("f")),
                                                       new FieldName("z"))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureAccessNonStructure() throws TypeErrorException {
        // void foo() {
        //   1.bar;
        // }

        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FieldAccessExp(new IntegerLiteralExp(1),
                                                        new FieldName("bar"))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test
    public void testNormalStructurePointerToField() throws TypeErrorException {
        // Foo {
        //   int x;
        //   bool y;
        // };
        // void foo() {
        //   Foo f = Foo(7, true);
        //   int* g = &f.x;
        // }
        final StructureName sname = new StructureName("Foo");
        final StructureDeclaration sdef =
            new StructureDeclaration(sname,
                                     Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                   new VariableDeclaration(new BoolType(), new Variable("y"))));
        final Stmt body = stmts(def(new StructureType(sname),
                                    "f",
                                    new MakeStructureExp(sname,
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))),
                                def(new PointerType(new IntType()),
                                    "g",
                                    new AddressOfExp(new FieldAccessLhs(new VariableLhs(new Variable("f")),
                                                                        new FieldName("x")))));
        final FunctionDefinition fdef = voidFunction(body);
        final Program prog = new Program(Arrays.asList(sdef),
                                         Arrays.asList(fdef));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testNormalFunctionCall() throws TypeErrorException {
        // int blah(int x, bool y) {
        //   return 7;
        // }
        // void foo() {
        //   blah(7, true);
        // }

        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                 new VariableDeclaration(new BoolType(), new Variable("y"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FunctionCallExp(new FunctionName("blah"),
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah, foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionVoidParam() throws TypeErrorException {
        // int blah(void x) {
        //   return 7;
        // }

        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new VoidType(), new Variable("x"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionParameterNonexistentStructure() throws TypeErrorException {
        // void foo(Foo f) { return; }

        final FunctionDefinition foo =
            new FunctionDefinition(new VoidType(),
                                   new FunctionName("foo"),
                                   Arrays.asList(new VariableDeclaration(new StructureType(new StructureName("Foo")),
                                                                         new Variable("f"))),
                                   Arrays.asList(new ReturnStmt(Optional.empty())));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test
    public void testFunctionDefinitionVoidPointerParam() throws TypeErrorException {
        // int blah(void* x) {
        //   return 7;
        // }

        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new PointerType(new VoidType()),
                                                                         new Variable("x"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionDuplicateFunctionNames() throws TypeErrorException {
        // void foo() { return; }
        // void foo() { return; }

        final FunctionDefinition foo =
            voidFunction(new ReturnStmt(Optional.empty()));
        
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo, foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionDuplicateParameterNames() throws TypeErrorException {
        // void foo(int x, bool x) { return; }

        final FunctionDefinition foo =
            new FunctionDefinition(new VoidType(),
                                   new FunctionName("foo"),
                                   Arrays.asList(new VariableDeclaration(new IntType(),
                                                                         new Variable("x")),
                                                 new VariableDeclaration(new BoolType(),
                                                                         new Variable("x"))),
                                   Arrays.asList(new ReturnStmt(Optional.empty())));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test(expected = TypeErrorException.class)
    public void testFunctionCallNotEnoughParams() throws TypeErrorException {
        // int blah(int x, bool y) {
        //   return 7;
        // }
        // void foo() {
        //   blah(7);
        // }
        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                 new VariableDeclaration(new BoolType(), new Variable("y"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FunctionCallExp(new FunctionName("blah"),
                                                         Arrays.asList(new IntegerLiteralExp(7)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah, foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test(expected = TypeErrorException.class)
    public void testFunctionCallTooManyParams() throws TypeErrorException {
        // int blah(int x, bool y) {
        //   return 7;
        // }
        // void foo() {
        //   blah(7, true, true);
        // }

        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                 new VariableDeclaration(new BoolType(), new Variable("y"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FunctionCallExp(new FunctionName("blah"),
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true),
                                                                       new BooleanLiteralExp(true)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah, foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionCallWrongTypes() throws TypeErrorException {
        // int blah(int x, bool y) {
        //   return 7;
        // }
        // void foo() {
        //   blah(true, 5);
        // }

        final FunctionDefinition blah =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("blah"),
                                   Arrays.asList(new VariableDeclaration(new IntType(), new Variable("x")),
                                                 new VariableDeclaration(new BoolType(), new Variable("y"))),
                                   Arrays.asList(new ReturnStmt(Optional.of(new IntegerLiteralExp(7)))));
        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FunctionCallExp(new FunctionName("blah"),
                                                         Arrays.asList(new BooleanLiteralExp(true),
                                                                       new IntegerLiteralExp(5)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(blah, foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionCallNonexistent() throws TypeErrorException {
        // void foo() {
        //   blah(7, true);
        // }

        final FunctionDefinition foo =
            voidFunction(new ExpStmt(new FunctionCallExp(new FunctionName("blah"),
                                                         Arrays.asList(new IntegerLiteralExp(7),
                                                                       new BooleanLiteralExp(true)))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testIfNormal() throws TypeErrorException {
        // void foo() {
        //   if (true) {
        //     return;
        //   } else {
        //     return;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new IfStmt(new BooleanLiteralExp(true),
                                    new ReturnStmt(Optional.empty()),
                                    Optional.of(new ReturnStmt(Optional.empty()))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testIfErrorGuard() throws TypeErrorException {
        // void foo() {
        //   if (1) {
        //     return;
        //   } else {
        //     return;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new IfStmt(new IntegerLiteralExp(1),
                                    new ReturnStmt(Optional.empty()),
                                    Optional.of(new ReturnStmt(Optional.empty()))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test(expected = TypeErrorException.class)
    public void testIfErrorTrueBranch() throws TypeErrorException {
        // void foo() {
        //   if (true) {
        //     return 1;
        //   } else {
        //     return;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new IfStmt(new BooleanLiteralExp(true),
                                    new ReturnStmt(Optional.of(new IntegerLiteralExp(1))),
                                    Optional.of(new ReturnStmt(Optional.empty()))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testIfErrorFalseBranch() throws TypeErrorException {
        // void foo() {
        //   if (true) {
        //     return;
        //   } else {
        //     return 1;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new IfStmt(new BooleanLiteralExp(true),
                                    new ReturnStmt(Optional.empty()),
                                    Optional.of(new ReturnStmt(Optional.of(new IntegerLiteralExp(1))))));
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testWhileNormal() throws TypeErrorException {
        // void foo() {
        //   while (true) {
        //     return;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new WhileStmt(new BooleanLiteralExp(true),
                                       new ReturnStmt(Optional.empty())));
        
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testWhileErrorGuard() throws TypeErrorException {
        // void foo() {
        //   while (1) {
        //     return;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new WhileStmt(new IntegerLiteralExp(1),
                                       new ReturnStmt(Optional.empty())));
        
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testWhileErrorBody() throws TypeErrorException {
        // void foo() {
        //   while (true) {
        //     return 1;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new WhileStmt(new BooleanLiteralExp(true),
                                       new ReturnStmt(Optional.of(new IntegerLiteralExp(1)))));
        
        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testBreakNormal() throws TypeErrorException {
        // void foo() {
        //   while (true) {
        //     break;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new WhileStmt(new BooleanLiteralExp(true),
                                       new BreakStmt()));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testBreakOutOfWhile() throws TypeErrorException {
        // void foo() {
        //   break;
        // }

        final FunctionDefinition foo =
            voidFunction(new BreakStmt());

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testContinueNormal() throws TypeErrorException {
        // void foo() {
        //   while (true) {
        //     continue;
        //   }
        // }

        final FunctionDefinition foo =
            voidFunction(new WhileStmt(new BooleanLiteralExp(true),
                                       new ContinueStmt()));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testContinueOutOfWhile() throws TypeErrorException {
        // void foo() {
        //   continue;
        // }

        final FunctionDefinition foo =
            voidFunction(new ContinueStmt());

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testCannotMakeVoidLocalVariable() throws TypeErrorException {
        // void foo() {
        //   void x = malloc(1);
        // }

        final FunctionDefinition foo =
            voidFunction(def(new VoidType(), "x", new MallocExp(new IntegerLiteralExp(1))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
        
    @Test
    public void testCanMakeVoidPointerLocalVariable() throws TypeErrorException {
        // void foo() {
        //   void* x = malloc(1);
        // }

        final FunctionDefinition foo =
            voidFunction(def(new PointerType(new VoidType()),
                             "x",
                             new MallocExp(new IntegerLiteralExp(1))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }        

    @Test
    public void testAssignmentNormal() throws TypeErrorException {
        // void foo() {
        //   int x = 7;
        //   x = 8;
        // }

        final FunctionDefinition foo =
            voidFunction(stmts(def(new IntType(), "x", new IntegerLiteralExp(7)),
                               new AssignmentStmt(new VariableLhs(new Variable("x")),
                                                  new IntegerLiteralExp(8))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentNoLhsVariable() throws TypeErrorException {
        // void foo() {
        //   int x = 7;
        //   y = 8;
        // }

        final FunctionDefinition foo =
            voidFunction(stmts(def(new IntType(), "x", new IntegerLiteralExp(7)),
                               new AssignmentStmt(new VariableLhs(new Variable("y")),
                                                  new IntegerLiteralExp(8))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentNonPointerDereferenceLhs() throws TypeErrorException {
        // void foo() {
        //   int x = 7;
        //   *x = 8;
        // }

        final FunctionDefinition foo =
            voidFunction(stmts(def(new IntType(), "x", new IntegerLiteralExp(7)),
                               new AssignmentStmt(new DereferenceLhs(new VariableLhs(new Variable("x"))),
                                                  new IntegerLiteralExp(8))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentErrorOnRight() throws TypeErrorException {
        // void foo() {
        //   int x = 7;
        //   x = true;
        // }

        final FunctionDefinition foo =
            voidFunction(stmts(def(new IntType(), "x", new IntegerLiteralExp(7)),
                               new AssignmentStmt(new VariableLhs(new Variable("x")),
                                                  new BooleanLiteralExp(true))));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testReturnNormal() throws TypeErrorException {
        // void foo() {
        //   return;
        // }

        final FunctionDefinition foo =
            voidFunction(new ReturnStmt(Optional.empty()));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnDuplicate() throws TypeErrorException {
        // void foo() {
        //   return;
        //   return;
        // }

        final FunctionDefinition foo =
            voidFunction(stmts(new ReturnStmt(Optional.empty()),
                               new ReturnStmt(Optional.empty())));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnIfOnlyTrue() throws TypeErrorException {
        // int foo() {
        //   if (true) {
        //     return 1;
        //   } else {
        //     1;
        //   }
        // }

        final Stmt body =
            new IfStmt(new BooleanLiteralExp(true),
                       new ReturnStmt(Optional.of(new IntegerLiteralExp(1))),
                       Optional.of(new ExpStmt(new IntegerLiteralExp(1))));
        
        final FunctionDefinition foo =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("foo"),
                                   EMPTY_VARDECS,
                                   Arrays.asList(body));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnIfOnlyFalse() throws TypeErrorException {
        // int foo() {
        //   if (true) {
        //     1;
        //   } else {
        //     return 1;
        //   }
        // }

        final Stmt body =
            new IfStmt(new BooleanLiteralExp(true),
                       new ExpStmt(new IntegerLiteralExp(1)),
                       Optional.of(new ReturnStmt(Optional.of(new IntegerLiteralExp(1)))));
        
        final FunctionDefinition foo =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("foo"),
                                   EMPTY_VARDECS,
                                   Arrays.asList(body));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }

    @Test
    public void testReturnIfBothBranches() throws TypeErrorException {
        // int foo() {
        //   if (true) {
        //     return 1;
        //   } else {
        //     return 2;
        //   }
        // }

        final Stmt body =
            new IfStmt(new BooleanLiteralExp(true),
                       new ReturnStmt(Optional.of(new IntegerLiteralExp(1))),
                       Optional.of(new ReturnStmt(Optional.of(new IntegerLiteralExp(2)))));
        
        final FunctionDefinition foo =
            new FunctionDefinition(new IntType(),
                                   new FunctionName("foo"),
                                   EMPTY_VARDECS,
                                   Arrays.asList(body));

        final Program prog = new Program(EMPTY_STRUCTURES,
                                         Arrays.asList(foo));
        Typechecker.typecheckProgram(prog);
    }
}
