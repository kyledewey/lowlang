package lowlang.typechecker;

import lowlang.parser.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

// Tests for expressions that need no scopes.
// This means:
// -No variables
// -No structures
// -No functions
// -No pointers (which depend on variables or structures)
public class TypecheckerExpTest {
    // use null if there should be a type error
    public void assertExpType(final Type expected, final Exp exp) {
        try {
            final Type received = Typechecker.expTypeForTesting(exp);
            assertTrue("Expected type error; got: " + received.toString(),
                       expected != null);
            assertEquals(expected, received);
        } catch (final TypeErrorException e) {
            assertTrue("Unexpected type error: " + e.getMessage(),
                       expected == null);
        }
    }

    @Test
    public void testIntegerLiteralExp() {
        assertExpType(new IntType(),
                      new IntegerLiteralExp(42));
    }

    @Test
    public void testCharacterLiteralExp() {
        assertExpType(new CharType(),
                      new CharacterLiteralExp('a'));
    }

    @Test
    public void testBooleanLiteralExp() {
        assertExpType(new BoolType(),
                      new BooleanLiteralExp(true));
    }

    @Test
    public void testMallocWithInt() {
        assertExpType(new PointerType(new VoidType()),
                      new MallocExp(new IntegerLiteralExp(42)));
    }

    @Test
    public void testMallocWithNonInt() {
        assertExpType(null,
                      new MallocExp(new BooleanLiteralExp(false)));
    }

    @Test
    public void testSizeof() {
        assertExpType(new IntType(),
                      new SizeofExp(new BoolType()));
    }

    @Test
    public void testBinopPlusInts() {
        assertExpType(new IntType(),
                      new BinopExp(new IntegerLiteralExp(1),
                                   new PlusOp(),
                                   new IntegerLiteralExp(2)));
    }

    @Test
    public void testBinopPlusNonIntOrPointer() {
        assertExpType(null,
                      new BinopExp(new CharacterLiteralExp('a'),
                                   new PlusOp(),
                                   new IntegerLiteralExp(1)));
    }

    @Test
    public void testMinusInts() {
        assertExpType(new IntType(),
                      new BinopExp(new IntegerLiteralExp(1),
                                   new MinusOp(),
                                   new IntegerLiteralExp(2)));
    }

    @Test
    public void testMinusNonInts() {
        assertExpType(null,
                      new BinopExp(new IntegerLiteralExp(1),
                                   new MinusOp(),
                                   new CharacterLiteralExp('a')));
    }

    @Test
    public void testMultInts() {
        assertExpType(new IntType(),
                      new BinopExp(new IntegerLiteralExp(1),
                                   new MultOp(),
                                   new IntegerLiteralExp(2)));
    }

    @Test
    public void testMultNonInts() {
        assertExpType(null,
                      new BinopExp(new IntegerLiteralExp(1),
                                   new MultOp(),
                                   new CharacterLiteralExp('a')));
    }

    @Test
    public void testDivInts() {
        assertExpType(new IntType(),
                      new BinopExp(new IntegerLiteralExp(1),
                                   new DivOp(),
                                   new IntegerLiteralExp(2)));
    }

    @Test
    public void testDivNonInts() {
        assertExpType(null,
                      new BinopExp(new IntegerLiteralExp(1),
                                   new DivOp(),
                                   new CharacterLiteralExp('a')));
    }

    @Test
    public void testEqualSameType() {
        assertExpType(new BoolType(),
                      new BinopExp(new CharacterLiteralExp('a'),
                                   new EqualsOp(),
                                   new CharacterLiteralExp('b')));
    }

    @Test
    public void testEqualDifferentTypes() {
        assertExpType(null,
                      new BinopExp(new CharacterLiteralExp('a'),
                                   new EqualsOp(),
                                   new IntegerLiteralExp(1)));
    }

    @Test
    public void testLessThanInts() {
        assertExpType(new BoolType(),
                      new BinopExp(new IntegerLiteralExp(1),
                                   new LessThanOp(),
                                   new IntegerLiteralExp(0)));
    }

    @Test
    public void testLessThanNonInts() {
        assertExpType(null,
                      new BinopExp(new CharacterLiteralExp('a'),
                                   new LessThanOp(),
                                   new IntegerLiteralExp(0)));
    }

    @Test
    public void testCastWellTypedSubexpression() {
        assertExpType(new IntType(),
                      new CastExp(new IntType(),
                                  new CharacterLiteralExp('a')));
    }

    @Test
    public void testCastIllTypedSubexpression() {
        assertExpType(null,
                      new CastExp(new CharType(),
                                  new BinopExp(new IntegerLiteralExp(1),
                                               new PlusOp(),
                                               new CharacterLiteralExp('a'))));
    }
} // TypecheckerTest
