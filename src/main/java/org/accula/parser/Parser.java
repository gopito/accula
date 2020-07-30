package org.accula.parser;

import generated.org.accula.parser.Java9Lexer;
import generated.org.accula.parser.Java9Parser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Parser {
    public static Flux<List<Token>> getFunctionsAsTokens(@NotNull final FileEntity fileEntity) {
//        System.err.println("Parsing : " + fileEntity.getOwner() + "/" + fileEntity.getName());
        final var lexer = new Java9Lexer(CharStreams.fromString(fileEntity.getContent()));
        final var tokens = new CommonTokenStream(lexer);
        final var parser = new Java9Parser(tokens);
        final var parseTree = parser.compilationUnit();

        final var walker = new ParseTreeWalker();
        final var listener = new JavaListener(tokens);
        walker.walk(listener, parseTree);

        return Flux
                .fromStream(
                        listener
                                .getFunctions()
                                .map(func -> func
                                        .stream()
                                        .filter(token -> isAllowedToken(token, listener.getTypeArgs()))
                                        .map(antlrToken -> anonymize(antlrToken, fileEntity))
                                        .collect(Collectors.toUnmodifiableList())
                                )
                );
    }

    private static boolean isAllowedToken(@NotNull final org.antlr.v4.runtime.Token token,
                                          @NotNull final Set<org.antlr.v4.runtime.Token> typeArgs) {
        return !TokenFilter.EXCLUDE_TOKENS.contains(token.getType())
                && !typeArgs.contains(token);
    }

    private static Token anonymize(@NotNull final org.antlr.v4.runtime.Token antlrToken,
                                   @NotNull final FileEntity fileEntity) {
        final var type = TokenFilter.PRIMITIVE_TYPES.contains(antlrToken.getType()) ?
                Java9Lexer.Identifier : antlrToken.getType();

        return new org.accula.parser.Token(
                type,
                antlrToken.getText(),
                antlrToken.getLine(),
                fileEntity.getName(),
                fileEntity.getOwner(),
                fileEntity.getPath()
        );
    }
}
