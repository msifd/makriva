package msifeed.makriva.expr.parser;

import msifeed.makriva.expr.*;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ExprParser {
    private final Deque<ExprFunction> operators = new ArrayDeque<>();
    private final Deque<IExpr> output = new ArrayDeque<>();
    private TokenType prevToken = null;

    public IExpr parse(String input) throws ParsingException {
        operators.clear();
        output.clear();
        prevToken = null;

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
                    pushFunctor(parseOperator(tk));
                    break;
                case identifier:
                    handleIdentifier(tk.str);
                    break;
                case bracket_open:
                    pushFunctor(ExprFunction.brackets);
                    break;
                case comma:
                    wrapArgument();
                    break;
                case bracket_close:
                    wrapBrackets();
                    break;
            }
            prevToken = tk.type;
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == ExprFunction.brackets) throw new ParsingException("Unexpected bracket");
            output.push(wrapFunction(operators.pop()));
        }

        if (output.size() > 1) throw new ParsingException("Invalid expression output");

        return output.pop();
    }

    private void handleIdentifier(String name) throws ParsingException {
        if (name.equals("true")) {
            output.push(new ConstBool(true));
            return;
        } else if (name.equals("false")) {
            output.push(new ConstBool(false));
            return;
        }

        final ExprFunction func = ExprFunction.find(name);
        if (func != null) {
            operators.push(func);
            return;
        }

        final ExprVariable var = ExprVariable.find(name);
        if (var != null) {
            output.push(var);
            return;
        }

        // TODO: custom variables
        throw new ParsingException("Unknown variable");
    }

    private IExpr parseNumber(Token token) throws ParsingException {
        final float val = Float.parseFloat(token.str);
        if (Float.isNaN(val)) throw new ParsingException("Invalid float number (NaN)");
        return new ConstFloat(val);
    }

    private ExprFunction parseOperator(Token token) throws ParsingException {
        final ExprFunction func = ExprFunction.find(token.str);
        if (func == null) throw new ParsingException("Unknown operator " + token.str);

        if (canBinaryOperatorGoNext()) {
            return func;
        } else {
            if (func == ExprFunction.minus) return ExprFunction.negate;
            else if (func == ExprFunction.not) return ExprFunction.not;
            else throw new ParsingException("Unexpected operator");
        }
    }

    private boolean canBinaryOperatorGoNext() {
        return prevToken == TokenType.bracket_close
                || prevToken == TokenType.number
                || prevToken == TokenType.identifier;
    }

    private void pushFunctor(ExprFunction func) throws ParsingException {
        while (!operators.isEmpty()
                && operators.peek().precedes(func)
                && operators.peek() != ExprFunction.brackets) {
            output.push(wrapFunction(operators.pop()));
        }

        operators.push(func);
    }

    private void wrapArgument() throws ParsingException {
        while (!operators.isEmpty() && operators.peek() != ExprFunction.brackets) {
            output.push(wrapFunction(operators.pop()));
        }
    }

    private void wrapBrackets() throws ParsingException {
        if (operators.isEmpty()) throw new ParsingException("Unexpected closing bracket");

        while (!operators.isEmpty()) {
            final ExprFunction func = operators.pop();
            if (func == ExprFunction.brackets) return;

            output.push(wrapFunction(func));
        }

        throw new ParsingException("Missing bracket");
    }

    private IExpr wrapFunction(ExprFunction func) throws ParsingException {
        if (output.size() < func.args) throw new ParsingException("Functor '" + func.name + "' requires " + func.args);

        final FunctorCall call = new FunctorCall(func);
        for (int i = func.args - 1; i >= 0; i--) {
            call.args[i] = output.pop();
        }
        return call;
    }
}
