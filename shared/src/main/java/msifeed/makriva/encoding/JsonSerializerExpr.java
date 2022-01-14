package msifeed.makriva.encoding;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import msifeed.makriva.expr.*;

import java.lang.reflect.Type;

public class JsonSerializerExpr implements JsonSerializer<IExpr> {
    @Override
    public JsonElement serialize(IExpr src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof ConstBool) return new JsonPrimitive(((ConstBool) src).value);
        if (src instanceof ConstFloat) return compactFloat(((ConstFloat) src).value);
        if (src instanceof ExprVariable) return new JsonPrimitive(((ExprVariable) src).name);
        if (src instanceof FunctorCall) return new JsonPrimitive(stringifyCall((FunctorCall) src));
        return null;
    }

    private JsonPrimitive compactFloat(float f) {
        if (f == (int) f) return new JsonPrimitive((int) f);
        else return new JsonPrimitive(f);
    }

    private String stringifyCall(FunctorCall call) {
        final ExprFunction func = call.func;

        if (func.isOperator()) {
            if (func == ExprFunction.not)
                return "!" + stringifyArg(func, call.args[0]);
            else if (func == ExprFunction.negate)
                return "-" + stringifyArg(func, call.args[0]);
            else
                return stringifyArg(func, call.args[0]) + func.name + stringifyArg(func, call.args[1]);
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append(func.name);
            sb.append('(');
            for (IExpr e : call.args) {
                sb.append(stringifyArg(func, e));
                sb.append(',');
            }
            if (call.args.length > 0) {
                sb.setLength(sb.length() - 1);
            }
            sb.append(')');
            return sb.toString();
        }
    }

    private String stringifyArg(ExprFunction callee, IExpr expr) {
        if (expr instanceof ConstBool) return String.valueOf(((ConstBool) expr).value);
        if (expr instanceof ConstFloat) return stringifyFloat(((ConstFloat) expr).value);
        if (expr instanceof ExprVariable) return ((ExprVariable) expr).name;

        final FunctorCall call = (FunctorCall) expr;
        if (callee.precedes(call.func)) {
            return "(" + stringifyCall(call) + ")";
        } else {
            return stringifyCall(call);
        }
    }

    private String stringifyFloat(float f) {
        if (f == (int) f) return String.format("%d", (int) f);
        else return String.format("%s", f);
    }
}
