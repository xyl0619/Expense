package com.in6206.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class I18nAssetsTest {

    private static final List<String> PAGES = List.of(
            "templates/index.html",
            "templates/login.html",
            "templates/register.html",
            "templates/dashboard.html",
            "templates/admin.html"
    );
    private static final List<String> CONSUMERS = List.of(
            "templates/index.html",
            "templates/login.html",
            "templates/register.html",
            "templates/dashboard.html",
            "templates/admin.html",
            "static/js/common.js",
            "static/js/dashboard.js",
            "static/js/admin.js"
    );
    private static final Pattern KEY = Pattern.compile(
            "['\"]((?:language|brand|nav|index|auth|dashboard|admin|common|dynamic)\\.[A-Za-z0-9]+)['\"]");

    @Test
    void everyPageLoadsSharedLanguageAssets() {
        PAGES.forEach(page -> {
            String html = resource(page);
            assertThat(html).contains("@{/js/i18n.js}");
            assertThat(html).contains("fragments/language :: switch");
        });
        assertThat(resource("templates/index.html")).contains("data-language-welcome");
    }

    @Test
    void everyUsedKeyHasChineseAndEnglishTranslations() {
        Set<String> usedKeys = CONSUMERS.stream()
                .map(this::resource)
                .flatMap(source -> {
                    Matcher matcher = KEY.matcher(source);
                    return matcher.results().map(result -> result.group(1));
                })
                .collect(Collectors.toSet());
        String dictionary = resource("static/js/i18n.js");

        usedKeys.forEach(key -> assertThat(occurrences(dictionary, "'" + key + "':"))
                .as("Chinese and English translations for %s", key)
                .isEqualTo(2));
    }

    private int occurrences(String source, String value) {
        return (source.length() - source.replace(value, "").length()) / value.length();
    }

    private String resource(String path) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
            assertThat(stream).as("resource %s", path).isNotNull();
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + path, exception);
        }
    }
}
