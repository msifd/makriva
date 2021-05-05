package msifeed.makriva.expr.parser;

public enum TokenType {
    space, comma, lBracket, rBracket, operator, number, dot, identifier;

    static TokenType identify(int c) {
        switch (c) {
            case ',':
                return comma;
            case '(':
                return lBracket;
            case ')':
                return rBracket;
            case '+':
            case '-':
            case '*':
            case '/':
                return operator;
            case '.':
                return dot;
            default:
                if (c >= '0' && c <= '9')
                    return number;
                else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_')
                    return identifier;
                else
                    return space;
        }
    }

    boolean canBeNext(TokenType next) {
        switch (this) {
            case space:
                return true;
            case comma:
            case lBracket:
            case rBracket:
            case operator:
                return false;
            case number:
                return next == number || next == dot;
            case identifier:
                return next == number || next == identifier;
        }

        return false;
    }
}
