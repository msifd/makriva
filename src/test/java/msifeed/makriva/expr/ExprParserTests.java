package msifeed.makriva.expr;

import msifeed.makriva.expr.parser.ExprParser;
import msifeed.makriva.expr.parser.ParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExprParserTests {
    private final ExprParser parser = new ExprParser();
    private final EvalContext ctx = new EvalContext();

    @Test
    void booleans() throws ParsingException {
        assertTrue(parser.parse("true").asBool(ctx));
        assertFalse(parser.parse("false").asBool(ctx));

        assertTrue(parser.parse("1").asBool(ctx));
        assertTrue(parser.parse("123").asBool(ctx));
        assertFalse(parser.parse("0").asBool(ctx));
    }

    @Test
    void floats() throws ParsingException {
        assertEquals(5, parser.parse("5").asFloat(ctx));
        assertEquals(99, parser.parse("99").asFloat(ctx));
        assertEquals(-19.97f, parser.parse("-19.97").asFloat(ctx));
    }

    @Test
    void operators() throws ParsingException {
        assertEquals(10, parser.parse("5+5").asFloat(ctx));
        assertEquals(-5, parser.parse("5-10").asFloat(ctx));
        assertEquals(8, parser.parse("2*4").asFloat(ctx));
        assertEquals(0.5f, parser.parse("2/4").asFloat(ctx));
        assertEquals(-5, parser.parse("-5").asFloat(ctx));
    }

    @Test
    void brackets() throws ParsingException {
        assertEquals(4, parser.parse("(2*2)").asFloat(ctx));
        assertEquals(9, parser.parse("5+(2*2)").asFloat(ctx));
        assertEquals(10, parser.parse("(2*3)+4").asFloat(ctx));
        assertEquals(10, parser.parse("5+(2*2)+1").asFloat(ctx));
    }

    @Test
    void functors() throws ParsingException {
        assertTrue(0 != parser.parse("random()").asFloat(ctx));
        assertEquals(2, parser.parse("sqrt(4)").asFloat(ctx));
        assertEquals(2, parser.parse("min(2, 4)").asFloat(ctx));

        assertEquals(5, parser.parse("1+max(2, 4)").asFloat(ctx));
        assertEquals(6, parser.parse("1+max(2, 4)+1").asFloat(ctx));
    }
}
