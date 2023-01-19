package lowlang.typechecker;

import lowlang.tokenizer.Tokenizer;
import lowlang.tokenizer.TokenizerException;
import lowlang.parser.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

public class TypecheckerScopeTest {
    public static void typechecks(final String program) throws TokenizerException, ParseException, TypeErrorException {
        Typechecker.typecheckProgram(Parser.parse(Tokenizer.tokenize(program)));
    }
    @Test
    public void testVariableDefinitionAndUse() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 0;" +
                   "  int y = x;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testAccessUndeclaredVariable() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = x;" +
                   "}");
    }

    @Test
    public void testPointerToVariable() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 0;" +
                   "  int* y = &x;" +
                   "}");
    }

    @Test
    public void testPointerDereferenceRhs() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 0;" +
                   "  int* p = &x;" +
                   "  int y = *p;" +
                   "}");
    }

    @Test
    public void testPointerDereferenceLhs() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 0;" +
                   "  int* p = &x;" +
                   "  *p = 7;" +
                   "}");
    }

    @Test
    public void testAddPointer() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 0;" +
                   "  int* y = &x;" +
                   "  int* z = y + 3;" +
                   "}");
    }

    @Test
    public void testNormalStructureCreation() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  int y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, 8);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureDuplicateFields() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  int x;" +
                   "};");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureDuplicateStructureNames() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo { int x; };" +
                   "struct Foo { int x; };");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureNonExistentStructureField() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  Bar x;" +
                   "};");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureVoidField() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  void y;" +
                   "};");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureCreationTooManyParams() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  int y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, 8, 9);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureCreationWrongParamTypes() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  bool y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(true, 7);" +
                   "}");
    }

    @Test
    public void testNormalStructureAccess() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  bool y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, true);" +
                   "  int g = f.x;" +
                   "}");
    }

    @Test
    public void testNormalStructureFieldAssignment() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  bool y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, true);" +
                   "  f.x = 8;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testCreateNonexistentStructure() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  Foo f = Foo(1);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureAccessNonexistentField() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  bool y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, true);" +
                   "  int h = f.z;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testStructureAccessNonStructure() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  1.bar;" +
                   "}");
    }
        
    @Test
    public void testNormalStructurePointerToField() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("struct Foo {" +
                   "  int x;" +
                   "  bool y;" +
                   "};" +
                   "void foo() {" +
                   "  Foo f = Foo(7, true);" +
                   "  int* g = &f.x;" +
                   "}");
    }
    
    @Test
    public void testNormalFunctionCall() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(int x, bool y) {" +
                   "  return 7;" +
                   "}" +
                   "void foo() {" +
                   "  blah(7, true);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionVoidParam() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(void x) {" +
                   "  return 7;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionParameterNonexistentStructure() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo(Foo f) { return; }");
    }
        
    @Test
    public void testFunctionDefinitionVoidPointerParam() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(void* x) {" +
                   "  return 7;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionDuplicateFunctionNames() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { return; }" +
                   "void foo() { return; }");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionDefinitionDuplicateParameterNames() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo(int x, bool x) { return; }");
    }
        
    @Test(expected = TypeErrorException.class)
    public void testFunctionCallNotEnoughParams() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(int x, bool y) {" +
                   "  return 7;" +
                   "}" +
                   "void foo() {" +
                   "  blah(7);" +
                   "}");
    }
        
    @Test(expected = TypeErrorException.class)
    public void testFunctionCallTooManyParams() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(int x, bool y) {" +
                   "  return 7;" +
                   "}" +
                   "void foo() {" +
                   "  blah(7, true, true);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionCallWrongTypes() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int blah(int x, bool y) {" +
                   "  return 7;" +
                   "}" +
                   "void foo() {" +
                   "  blah(true, 5);" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testFunctionCallNonexistent() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  blah(7, true);" +
                   "}");
    }

    @Test
    public void testIfNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  if (true) { return; } else { return; }" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testIfErrorGuard() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { if (1) { return; } else { return; } }");
    }
        
    @Test(expected = TypeErrorException.class)
    public void testIfErrorTrueBranch() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { if (true) { return 1; } else { return; } }");
    }

    @Test(expected = TypeErrorException.class)
    public void testIfErrorFalseBranch() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { if (true) { return; } else { return 1; } }");
    }

    @Test
    public void testWhileNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { while (true) { return; } }");
    }

    @Test(expected = TypeErrorException.class)
    public void testWhileErrorGuard() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { while (1) { return; } }");
    }

    @Test(expected = TypeErrorException.class)
    public void testWhileErrorBody() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { while (true) { return 1; } }");
    }

    @Test
    public void testBreakNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { while (true) { break; } }");
    }

    @Test(expected = TypeErrorException.class)
    public void testBreakOutOfWhile() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { break; }");
    }

    @Test
    public void testContinueNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { while (true) { continue; } }");
    }

    @Test(expected = TypeErrorException.class)
    public void testContinueOutOfWhile() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { continue; }");
    }

    @Test(expected = TypeErrorException.class)
    public void testCannotMakeVoidLocalVariable() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { void x = malloc(1); }");
    }
        
    @Test
    public void testCanMakeVoidPointerLocalVariable() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { void* x = malloc(1); }");
    }        

    @Test
    public void testAssignmentNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 7;" +
                   "  x = 8;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentNoLhsVariable() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 7;" +
                   "  y = 8;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentNonPointerDereferenceLhs() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 7;" +
                   "  *x = 8;" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testAssignmentErrorOnRight() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() {" +
                   "  int x = 7;" +
                   "  x = true;" +
                   "}");
    }

    @Test
    public void testReturnNormal() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { return; }");
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnDuplicate() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void foo() { return; return; }");
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnIfOnlyTrue() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int foo() {" +
                   "  if (true) { return 1; } else { 1; }" +
                   "}");
    }

    @Test(expected = TypeErrorException.class)
    public void testReturnIfOnlyFalse() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int foo() {" +
                   "  if (true) { 1; } else { return 1; }" +
                   "}");
    }

    @Test
    public void testReturnIfBothBranches() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int foo() {" +
                   "  if (true) { return 1; } else { return 2; }" +
                   "}");
    }

    @Test
    public void testPrint() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("void main() { print(1); }");
    }

    @Test
    public void testIndirectCall() throws TokenizerException, ParseException, TypeErrorException {
        typechecks("int add(int x, int y) { return x + y; }" +
                   "int sub(int x, int y) { return x - y; }" +
                   "void main() {" +
                   "  (int, int) => int f = &add;" +
                   "  print(f(1, 2));" +
                   "  f = &sub;" +
                   "  print(f(5, 1));" +
                   "}");
    }
}
