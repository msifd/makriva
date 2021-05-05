package msifeed.makriva.expr;

import com.google.common.collect.Lists;
import msifeed.makriva.expr.parser.Tokenizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class TokenizerTests {
    private static List<String> listify(String... strings) {
        return Lists.newArrayList(strings);
    }

    private static List<String> tokenize(String input) throws IOException {
        return Tokenizer.tokenize(input).stream()
                .map(token -> token.str)
                .collect(Collectors.toList());
    }

    @Test
    void tokenize() throws IOException {
        assertLinesMatch(listify("1"), tokenize("1"));
        assertLinesMatch(listify("123456"), tokenize("123456"));
        assertLinesMatch(listify("3.14"), tokenize("3.14"));
        assertLinesMatch(listify("-", "1"), tokenize("-1"));
        assertLinesMatch(listify("1", "+", "1"), tokenize("1+1"));
        assertLinesMatch(listify("1", "-", "1"), tokenize("  1   - 1  "));

        assertLinesMatch(
                listify("if", "(", "true", ",", "1", ",", "-", "1", ")"),
                tokenize("if(true, 1, -1)")
        );

        assertLinesMatch(
                listify("if", "(", "true", ",", "min", "(", "a", ",", "b", ")", ",", "-", "1", ")"),
                tokenize("if(true,min(a,b),-1)")
        );
    }
}
