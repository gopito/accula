package org.accula.ccd;

import antlr.gen.JavaLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Tokenizer {

    private Tokenizer() {}

    public static List<? extends Token> tokenize(File file) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromPath(file.toPath()));
        return lexer.getAllTokens();
    }
}
