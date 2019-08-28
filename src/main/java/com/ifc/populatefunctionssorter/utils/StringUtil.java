package com.ifc.populatefunctionssorter.utils;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("Unable to create an object of the util " + StringUtil.class.getName());
    }

    public static String validateString(String string) {
        return StringUtils.isEmpty(string) ? string : string.toLowerCase().trim();
    }
}
