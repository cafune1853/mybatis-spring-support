package com.github.cafune1853.mybatis.spring.support.util;

/**
 * @author doggy1853
 */
public class StringUtil {

    /**
     * 将驼峰式字符串转换成下划线形式，如camelCase -> camel_case, HTTPStatus -> http_status, personIDs -> person_i_ds
     * @param str:字符串
     * @return 转换后的字符串
     */
    public static String camelCaseToUnderScore(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Name should not be null.");
        }
        StringBuilder sbd = new StringBuilder();
        char prevChar = str.charAt(0);
        sbd.append(Character.toLowerCase(prevChar));
        for (int i = 1, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                int next = i + 1;
                char nextChar = (next < len) ? str.charAt(next) : '\0';
                if (Character.isLowerCase(prevChar) || Character.isLowerCase(nextChar)) {
                    if (prevChar != '_') {
                        sbd.append('_');
                    }
                }
                sbd.append(Character.toLowerCase(c));
            } else {
                sbd.append(c);
            }
            prevChar = c;
        }
        return sbd.toString();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}
