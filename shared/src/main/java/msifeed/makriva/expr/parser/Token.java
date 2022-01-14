package msifeed.makriva.expr.parser;

public class Token {
    public final String str;
    public final TokenType type;

    public Token(String str, TokenType type) {
        this.str = str;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{'" + str + "'}";
    }
}
