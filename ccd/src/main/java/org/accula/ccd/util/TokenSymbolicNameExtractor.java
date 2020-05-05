package org.accula.ccd.util;

import org.accula.ccd.lexer.gen.JavaLexer;
import org.antlr.v4.runtime.Token;

public class TokenSymbolicNameExtractor {

    private TokenSymbolicNameExtractor() {}

    public static String getSymbolicNameFrom(Token token) {
        return JavaLexer.VOCABULARY.getSymbolicName(token.getType());
    }
}
