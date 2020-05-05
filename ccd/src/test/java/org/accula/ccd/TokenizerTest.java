package org.accula.ccd;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class TokenizerTest {

    private File testFile;
    int[] actualTokenTypes = {32,108,111,69,111,69,111,67,108,35,108,9,108,111,108,63,108,35,108,38,108,48,108,111,61,111,65,66,108,111,62,108,63,108,111,69,111,69,111,61,59,62,67,108,64,108,64};

    @BeforeEach
    public void init() {
        testFile = new File("src/test/resources/HelloWorld.java");
    }

    @Test
    void tokenize() throws IOException {
        int[] tokenTypes = Tokenizer.tokenize(testFile).stream()
                .map(Token::getType)
                .mapToInt(__ -> __)
                .toArray();
        assertArrayEquals(tokenTypes, actualTokenTypes);
    }
}