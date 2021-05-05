package msifeed.makriva.expr.parser;

import msifeed.makriva.expr.*;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ExprParser {
    private final Deque<ExprFunctor> functors = new ArrayDeque<>();
    private final Deque<IExpr> output = new ArrayDeque<>();

    public IExpr parse(String input) throws ParsingException {
        final Deque<Token> tokens;
        try {
            tokens = new ArrayDeque<>(Tokenizer.tokenize(input));
        } catch (IOException e) {
            e.printStackTrace();
            return new ConstFloat(0f);
        }

        return parseExpr(tokens);
    }

    private IExpr parseExpr(Deque<Token> tokens) throws ParsingException {
        while (!tokens.isEmpty()) {
            final Token tk = tokens.pop();
            switch (tk.type) {
                case number:
                    output.push(parseNumber(tk));
                    break;
                case operator:
                    return parseOperator(tokens, tk);
                case identifier:
                    handleIdentifier(tk.str);
                    break;
                case lBracket:
                    handleBrackets(tokens);
                    break;
                case rBracket:
                case comma:
                    return output.pop();
            }
        }

        return output.pop();
    }

    private void handleIdentifier(String name) {
        if (name.equals("true")) {
            output.push(new ConstBool(true));
            return;
        } else if (name.equals("false")) {
            output.push(new ConstBool(false));
            return;
        }

        final ExprFunctor func = ExprFunctor.find(name);
        if (func != null) {
            functors.add(func);
            return;
        }

        // TODO: add variables
        output.push(new ConstBool(false));
    }

    private IExpr parseNumber(Token token) throws ParsingException {
        final float val = Float.parseFloat(token.str);
        if (Float.isNaN(val)) throw new ParsingException("Invalid float number (NaN)");
        return new ConstFloat(val);
    }

    private IExpr parseOperator(Deque<Token> tokens, Token token) throws ParsingException {
        final ExprFunctor func = ExprFunctor.find(token.str);

        if (output.isEmpty()) {
            if (func != ExprFunctor.minus) throw new ParsingException("Expected operator parameter");

            final FunctorCall call = new FunctorCall(ExprFunctor.negate);
            call.args[0] = parseExpr(tokens);
            return call;
        } else {
            final FunctorCall call = new FunctorCall(func);
            call.args[0] = output.pop();
            call.args[1] = parseExpr(tokens);
            return call;
        }
    }

    private void handleBrackets(Deque<Token> tokens) throws ParsingException {
        if (!functors.isEmpty())
            output.push(parseFunctor(tokens, functors.pop()));
        else
            output.push(parseExpr(tokens));
    }

    private IExpr parseFunctor(Deque<Token> tokens, ExprFunctor func) throws ParsingException {
        final FunctorCall call = new FunctorCall(func);

        for (int i = 0; i < func.args; i++) {
//            final Token tk = tokens.peek();
//            if (tk == null) throw new ParsingException("Unexpected EOL");
//            if (tk.type == TokenType.rBracket) throw new ParsingException("Unexpected right brace");
            call.args[i] = parseExpr(tokens);
        }

//        if (tokens.pop().type != TokenType.rBracket) throw new ParsingException("Expected right brace");

        return call;
    }
}
