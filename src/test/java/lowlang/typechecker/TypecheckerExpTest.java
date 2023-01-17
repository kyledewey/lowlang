package lowlang.typechecker;

import lowlang.tokenizer.Tokenizer;
import lowlang.tokenizer.TokenizerException;
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
    public void assertExpType(final String expectedType,
                              final String exp) throws TokenizerException, ParseException {
        assertExpType((expectedType == null) ? null : Parser.parseType(Tokenizer.tokenize(expectedType)),
                      Parser.parseExp(Tokenizer.tokenize(exp)));
    }
    
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
    public void testIntegerLiteralExp() throws TokenizerException, ParseException {
        assertExpType("int", "42");
    }

    @Test
    public void testBooleanLiteralExp() throws TokenizerException, ParseException {
        assertExpType("bool", "true");
    }

    @Test
    public void testMallocWithInt() throws TokenizerException, ParseException {
        assertExpType("void*", "malloc(42)");
    }

    @Test
    public void testMallocWithNonInt() throws TokenizerException, ParseException {
        assertExpType(null,
                      "malloc(false)");
    }

    @Test
    public void testSizeof() throws TokenizerException, ParseException {
        assertExpType("int", "sizeof(bool)");
    }

    @Test
    public void testBinopPlusInts() throws TokenizerException, ParseException {
        assertExpType("int", "1 + 2");
    }

    @Test
    public void testBinopPlusNonIntOrPointer() throws TokenizerException, ParseException {
        assertExpType(null, "true + 1");
    }

    @Test
    public void testMinusInts() throws TokenizerException, ParseException {
        assertExpType("int", "1 - 2");
    }

    @Test
    public void testMinusNonInts() throws TokenizerException, ParseException {
        assertExpType(null, "1 - true");
    }

    @Test
    public void testMultInts() throws TokenizerException, ParseException {
        assertExpType("int", "1 * 2");
    }

    @Test
    public void testMultNonInts() throws TokenizerException, ParseException {
        assertExpType(null, "1 * false");
    }

    @Test
    public void testDivInts() throws TokenizerException, ParseException {
        assertExpType("int", "1 / 2");
    }

    @Test
    public void testDivNonInts() throws TokenizerException, ParseException {
        assertExpType(null, "1 / true");
    }

    @Test
    public void testEqualSameType() throws TokenizerException, ParseException {
        assertExpType("bool", "1 == 1");
    }

    @Test
    public void testEqualDifferentTypes() throws TokenizerException, ParseException {
        assertExpType(null, "true == 1");
    }

    @Test
    public void testLessThanInts() throws TokenizerException, ParseException {
        assertExpType("bool", "1 < 0");
    }

    @Test
    public void testLessThanNonInts() throws TokenizerException, ParseException {
        assertExpType(null, "true < 0");
    }

    @Test
    public void testCastWellTypedSubexpression() throws TokenizerException, ParseException {
        assertExpType("int", "(int)true");
    }

    @Test
    public void testCastIllTypedSubexpression() throws TokenizerException, ParseException {
        assertExpType(null, "(bool)(1 + true)");
    }
} // TypecheckerTest
