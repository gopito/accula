plugins {
    antlr
    id("org.springframework.boot") version "2.3.0.RC1"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

repositories {
    maven(url = "https://repo.spring.io/milestone")
}

version = "1.0-SNAPSHOT"

dependencies {
    antlr("org.antlr:antlr4:4.8")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

//Run this task to generate JavaLexer sources
tasks.generateGrammarSource {
    arguments = arguments + listOf("-package", "org.accula.ccd.lexer.gen")
    outputDirectory = file("src/main/java/org/accula/ccd/lexer/gen")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}