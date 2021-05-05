package msifeed.makriva.expr.json;

import com.google.gson.*;
import msifeed.makriva.expr.ConstBool;
import msifeed.makriva.expr.ConstFloat;
import msifeed.makriva.expr.IExpr;

import java.lang.reflect.Type;

public class JsonAdapterExpr implements JsonDeserializer<IExpr>, JsonSerializer<IExpr> {
    @Override
    public IExpr deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("The Float expression should be a number or a string.");

        final JsonPrimitive prim = json.getAsJsonPrimitive();
        if (prim.isBoolean()) {
            return new ConstBool(prim.getAsBoolean());
        } if (prim.isNumber()) {
            return new ConstFloat(prim.getAsFloat());
        } else if (prim.isString()) {
            return null; // TODO: add parser
        } else {
            throw new JsonParseException("The Float expression should be a number or a string.");
        }
    }

    @Override
    public JsonElement serialize(IExpr src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof ConstBool) {
            return new JsonPrimitive(((ConstBool) src).value);
        } else if (src instanceof ConstFloat) {
            return new JsonPrimitive(((ConstFloat) src).value);
        } else {
            return null; // TODO: add printer
        }
    }
}
