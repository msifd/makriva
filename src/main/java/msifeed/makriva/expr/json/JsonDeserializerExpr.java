package msifeed.makriva.expr.json;

import com.google.gson.*;
import msifeed.makriva.expr.ConstBool;
import msifeed.makriva.expr.ConstFloat;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.parser.ExprParser;
import msifeed.makriva.expr.parser.ParsingException;

import java.lang.reflect.Type;

public class JsonDeserializerExpr implements JsonDeserializer<IExpr> {
    private final ExprParser parser = new ExprParser();

    @Override
    public IExpr deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("The Float expression should be a number or a string.");

        final JsonPrimitive prim = json.getAsJsonPrimitive();
        if (prim.isBoolean()) return new ConstBool(prim.getAsBoolean());
        if (prim.isNumber()) return new ConstFloat(prim.getAsFloat());

        if (!prim.isString()) throw new JsonParseException("Expression should be bool, number or a string");
        try {
            return parser.parse(prim.getAsString());
        } catch (ParsingException e) {
            throw new JsonParseException("Can't parse expr '" + prim.getAsString() + "'. Error: " + e.getMessage());
        }
    }
}
