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

        TokenType prev = null;
        for (int c = reader.read(); c > 0; c = reader.read()) {
            if (Character.isWhitespace(c)) {
                if (sb.length() > 0) {
                    tokens.add(new Token(sb.toString(), prev));
                    sb.setLength(0);
                    prev = null;
                }
                continue;
            }

            if (prev == null || prev.canBeNext(c)) {
                sb.appendCodePoint(c);
                if (prev == null)
                    prev = TokenType.identify(c);
            } else {
                tokens.add(new Token(sb.toString(), prev));
                sb.setLength(0);
                prev = null;
                reader.unread(c);
            }
        }

        if (sb.length() > 0) {
            tokens.add(new Token(sb.toString(), prev));
        }

        return tokens;
    }
}
