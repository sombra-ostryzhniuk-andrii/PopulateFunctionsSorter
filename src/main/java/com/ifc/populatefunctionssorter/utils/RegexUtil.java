package com.ifc.populatefunctionssorter.utils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {

    private RegexUtil() {
        throw new UnsupportedOperationException("Unable to create an object of the util RegexUtil");
    }

    public static Optional<String> substring(String content, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }
}
