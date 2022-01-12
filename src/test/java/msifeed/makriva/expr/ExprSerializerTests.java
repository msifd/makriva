package msifeed.makriva.expr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msifeed.makriva.json.JsonSerializerExpr;
import msifeed.makriva.expr.parser.ExprParser;
import msifeed.makriva.expr.parser.ParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprSerializerTests {
    private final ExprParser parser = new ExprParser();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ConstBool.class, new JsonSerializerExpr())
            .registerTypeAdapter(ConstFloat.class, new JsonSerializerExpr())
            .registerTypeAdapter(ExprVariable.class, new JsonSerializerExpr())
            .registerTypeAdapter(FunctorCall.class, new JsonSerializerExpr())
            .create();

    private String ser(String input) throws ParsingException {
        return gson.toJson(parser.parse(input));
    }

    @Test
    void primitives() throws ParsingException {
        assertEquals("true", ser("true"));
        assertEquals("false", ser("false"));

        assertEquals("123", ser("123"));
        assertEquals("3.778", ser("3.778"));
    }

    @Test
    void operations() throws ParsingException {
        assertEquals("\"1+2\"", ser("1+2"));
        assertEquals("\"-1.2+-2\"", ser("-1.2+-2"));
        assertEquals("\"2*2+3*4+1\"", ser("2*2+3*4+1"));
        assertEquals("\"1*(2+3)*4+1\"", ser("1*(2+3)*4+1"));
    }

    @Test
    void functions() throws ParsingException {
        assertEquals("\"random()\"", ser("random()"));
        assertEquals("\"min(1,2)\"", ser("min(1,2)"));
        assertEquals("\"min(1,2)+3\"", ser("min(1,2)+3"));
    }

    @Test
    void variables() throws ParsingException {
        assertEquals("\"headPitch*0.9\"", ser("headPitch*0.9"));
        assertEquals("\"(1+limbSwing)*0.2\"", ser("(1+limbSwing)*0.2"));
    }
}
