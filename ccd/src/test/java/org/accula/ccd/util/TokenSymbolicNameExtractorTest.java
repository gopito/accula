package org.accula.ccd.util;

import org.accula.ccd.lexer.gen.JavaLexer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenSymbolicNameExtractorTest {

    @Test
    void getSymbolicNameFrom() {
        Token token = mock(Token.class);
        for (int tokenType = 1; tokenType <= 110; tokenType++) {
            when(token.getType()).thenReturn(tokenType);
            assertEquals(JavaLexer.VOCABULARY.getSymbolicName(tokenType), TokenSymbolicNameExtractor.getSymbolicNameFrom(token));
        }
    }
}