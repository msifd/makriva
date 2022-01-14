package msifeed.makriva.expr.parser;

public enum TokenType {
    comma,
    bracket_open,
    bracket_close,
    operator("&|="),
    number("."),
    identifier(".0123456789"),
    ;

    private final String extraTails;

    TokenType() {
        this("");
    }

    TokenType(String extraTails) {
        this.extraTails = extraTails;
    }

    static TokenType identify(int c) {
        switch (c) {
            case ',':
                return comma;
            case '(':
                return bracket_open;
            case ')':
                return bracket_close;
            case '+':
            case '-':
            case '*':
            case '/':
            case '%':
            case '>':
            case '<':
            case '=':
            case '!':
            case '&':
            case '|':
                return operator;
            default:
                if (c >= '0' && c <= '9')
                    return number;
                else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_')
                    return identifier;
                else
                    return null;
        }
    }

    boolean canBeNext(int c) {
        switch (this) {
            case comma:
            case bracket_open:
            case bracket_close:
                return false;
            case operator:
                return extraTails.indexOf(c) != -1;
            case number:
                return identify(c) == number || c == '.';
            case identifier:
                return identify(c) == identifier || extraTails.indexOf(c) != -1;
        }

        return false;
    }
}
