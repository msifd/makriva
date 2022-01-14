package msifeed.makriva.encoding;

import com.google.common.base.Splitter;
import com.google.gson.*;
import msifeed.makriva.model.AnimationRules;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.parser.ExprParser;
import msifeed.makriva.expr.parser.ParsingException;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class AnimationJsonDeserializer implements JsonDeserializer<AnimationRules> {
    private final ExprParser parser = new ExprParser();
    private final Splitter pathSplitter = Splitter.on('.');

    @Override
    public AnimationRules deserialize(JsonElement jsonEl, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!jsonEl.isJsonObject())
            throw new JsonParseException("Animation should be an object.");

        final JsonObject json = jsonEl.getAsJsonObject();
        final AnimationRules anim = new AnimationRules();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().startsWith("if ")) {
                final AnimationRules child = deserialize(entry.getValue(), typeOfT, context);
                child.condition = parseCondition(entry.getKey());
                anim.children.add(child);
                continue;
            }

            final Iterator<String> path = pathSplitter.split(entry.getKey()).iterator();
            if (!path.hasNext()) throw new JsonParseException("Animation entry missing target name");
            final String target = path.next();
            if (!path.hasNext()) throw new JsonParseException("Animation entry missing specifier");
            final String specifier = path.next();

            if (target.equals("skeleton")) {
                anim.skeleton.put(getBipedPart(specifier), parseIExprArray(entry.getValue(), 3, context));
            } else {
                anim.bones.put(target, parseEntry(specifier, entry.getValue(), context));
            }
        }

        return anim;
    }

    private IExpr parseCondition(String rawKey) {
        try {
            // cut out "if " and leave bool exp
            return parser.parse(rawKey.substring(3));
        } catch (ParsingException e) {
            e.printStackTrace();
            throw new JsonParseException("Failed to parse condition");
        }
    }

    private BipedPart getBipedPart(String name) {
        try {
            return BipedPart.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid biped part: " + name);
        }
    }

    private AnimationRules.BoneParams parseEntry(String field, JsonElement jsonEl, JsonDeserializationContext context) {
        final AnimationRules.BoneParams entry = new AnimationRules.BoneParams();
        switch (field) {
            case "rotation":
                entry.rotation = parseIExprArray(jsonEl, 3, context);
                break;
            default:
                throw new JsonParseException("Unknown animation entry field: " + field);
        }
        return entry;
    }

    private IExpr[] parseIExprArray(JsonElement json, int len, JsonDeserializationContext context) {
        if (!json.isJsonArray())
            throw new JsonParseException("Animation entry value should be an array");
        final IExpr[] val = context.deserialize(json, IExpr[].class);
        if (val.length != len)
            throw new JsonParseException("Expected array with length " + len + ", got " + val.length);
        return val;
    }
}
