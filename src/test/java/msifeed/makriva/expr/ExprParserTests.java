package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.expr.parser.ExprParser;
import msifeed.makriva.expr.parser.ParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExprParserTests {
    private final ExprParser parser = new ExprParser();
    private final EvalContext ctx = new EvalContext();

    private boolean evalBool(String input) throws ParsingException {
        return parser.parse(input).asBool(ctx);
    }

    private float evalFloat(String input) throws ParsingException {
        return parser.parse(input).asFloat(ctx);
    }

    @Test
    void booleans() throws ParsingException {
        assertTrue(evalBool("true"));
        assertFalse(evalBool("false"));

        assertTrue(evalBool("1"));
        assertTrue(evalBool("123"));
        assertFalse(evalBool("0"));
    }

    @Test
    void floats() throws ParsingException {
        assertEquals(5, evalFloat("5"));
        assertEquals(99, evalFloat("99"));
        assertEquals(-19.97f, evalFloat("-19.97"));
    }

    @Test
    void booleanOperators() throws ParsingException {
        assertTrue(evalBool("true || true"));
        assertTrue(evalBool("true || false"));
        assertTrue(evalBool("false || true"));
        assertFalse(evalBool("false || false"));

        assertTrue(evalBool("true && true"));
        assertFalse(evalBool("true && false"));
        assertFalse(evalBool("false && true"));
        assertFalse(evalBool("false && false"));

        assertFalse(evalBool("!true && !false"));
        assertFalse(evalBool("!false && !true"));
        assertTrue(evalBool("!false && true"));
        assertFalse(evalBool("true && !true"));

        assertThrows(ParsingException.class, () -> evalBool("true <| false"));
    }

    @Test
    void floatOperators() throws ParsingException {
        assertEquals(10, evalFloat("5+5"));
        assertEquals(-5, evalFloat("5-10"));
        assertEquals(8, evalFloat("2*4"));
        assertEquals(0.5f, evalFloat("2/4"));
        assertEquals(-5, evalFloat("-5"));
        assertEquals(-5, evalFloat("5+-10"));
        assertEquals(-10, evalFloat("---10"));

        assertEquals(11, evalFloat("1+2*3+4"));
        assertEquals(17, evalFloat("2*2+3*4+1"));
    }

    @Test
    void brackets() throws ParsingException {
        assertEquals(4, evalFloat("(2*2)"));
        assertEquals(9, evalFloat("5+(2*2)"));
        assertEquals(10, evalFloat("(2*3)+4"));
        assertEquals(10, evalFloat("5+(2*2)+1"));
        assertEquals(30, evalFloat("2*(1+2)*5"));

        assertThrows(ParsingException.class, () -> evalFloat("(123))"));
    }

    @Test
    void functions() throws ParsingException {
        assertNotEquals(evalFloat("random()"), evalFloat("random()"));
        assertEquals(2, evalFloat("sqrt(4)"));
        assertEquals(2, evalFloat("min(2, 4)"));
        assertEquals(30, evalFloat("if(false, 20, 30)"));

        assertEquals(5, evalFloat("1+max(2, 4)"));
        assertEquals(5, evalFloat("1+max(2,4,)"));
        assertEquals(6, evalFloat("1+max(2, 4)+1"));

        assertEquals(3, evalFloat("sqrt((5+4))"));
        assertEquals(20, evalFloat("if(false || true, 20, 30)"));

        assertThrows(ParsingException.class, () -> evalFloat("sqrt(123))"));
        assertThrows(ParsingException.class, () -> evalFloat("sqrt(123"));
        assertThrows(ParsingException.class, () -> evalFloat("foobar(123)"));
    }

    @Test
    void staticVariables() throws ParsingException {
        assertDoesNotThrow(() -> evalFloat("1 + limbSwing"));
        assertDoesNotThrow(() -> evalFloat("1 + limbSwingAmount"));
    }
}
