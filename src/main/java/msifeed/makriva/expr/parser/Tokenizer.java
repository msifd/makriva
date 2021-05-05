package msifeed.makriva.expr.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.*;

public class Tokenizer {
    public static List<Token> tokenize(String input) throws IOException {
        final PushbackReader reader = new PushbackReader(new StringReader(input));

        final List<Token> tokens = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();

        TokenType prev = TokenType.space;
        for (int c = reader.read(); c > 0; c = reader.read()) {
            final TokenType curr = TokenType.identify(c);

            if (curr == TokenType.space) {
                if (sb.length() > 0) {
                    tokens.add(new Token(sb.toString(), prev));
                    sb.setLength(0);
                    prev = TokenType.space;
                }
                continue;
            }

            if (prev.canBeNext(curr)) {
                sb.appendCodePoint(c);
                if (prev == TokenType.space)
                    prev = curr;
            } else {
                tokens.add(new Token(sb.toString(), prev));
                sb.setLength(0);
                prev = TokenType.space;
                reader.unread(c);
            }
        }

        if (sb.length() > 0) {
            tokens.add(new Token(sb.toString(), prev));
        }

        return tokens;
    }
}
