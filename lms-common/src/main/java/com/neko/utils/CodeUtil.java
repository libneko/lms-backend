package com.neko.utils;

import org.apache.commons.text.RandomStringGenerator;

public class CodeUtil {
    private static final RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')  // 可选字符范围
            .filteredBy(Character::isLetterOrDigit)  // 只要字母或数字
            .get();

    private static final RandomStringGenerator numberGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', '9')   // 范围为数字
            .filteredBy(Character::isDigit) // 只保留数字字符
            .get();

    public static String generate(int length) {
        return stringGenerator.generate(length);
    }


    public static String generateCode(int length) {
        return numberGenerator.generate(length);
    }
}
