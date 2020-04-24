package com.xxx.api.apiscaffold.util;

import java.util.Random;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2018/7/18 13:43
 * @extra code change the world
 * @description
 */
public class RandomStringUtil {

    // 组成密码的特殊字符数组，需要时可以再添加
    private static final char[] SPECIAL_CHARS = {
            '+', '=', '-', '_'
    };

    private static final Random RANDOM = new Random();

    public static enum SecurityLevel {
        low,
        high,
        strong,
        veryStrong,
    }

    public static String newRandomString(int length) {
        return newRandomString(SecurityLevel.high, length);
    }

    public static String newRandomString(SecurityLevel securityLevel, int length) {
        switch (securityLevel) {
            case low:
                return lowPassword(length);
            case high:
                return highPassword(length);
            case strong:
                return strongPassword(length);
            case veryStrong:
                return veryStrongPassword(length);
            default:
                break;
        }
        return strongPassword(length);
    }

    /**
     * 纯数字组成的密码
     * @param length
     * @return
     */
    private static String lowPassword(int length) {
        char[] pwdChars = new char[length];
        for (int i = 0; i < length; i++) {
            pwdChars[i] = randomChar('0', 10);
        }

        return String.valueOf(pwdChars);
    }

    /**
     * 小写字母和数字组成的密码
     * @param length
     * @return
     */
    private static String highPassword(int length) {
        char[] pwdChars = new char[length];

        int numberCount = RANDOM.nextInt(length - 2) + 1;
        int[] randomPos = getRandomSequence(length);

        for (int i = 0; i < length; i++) {
            if (i < numberCount) {
                pwdChars[randomPos[i]] = randomChar('0', 10);
            } else {
                pwdChars[randomPos[i]] = randomChar('a', 26);
            }
        }

        return String.valueOf(pwdChars);
    }

    /**
     * 由数字，大小写字母组成的字符串
     * @param length
     * @return
     */
    private static String strongPassword(int length) {
        char[] pwdChars = new char[length];

        int numberCount = RANDOM.nextInt(length - 2) + 1;
        int upperCount = RANDOM.nextInt(length - numberCount - 1) + 1;
        int[] randomPos = getRandomSequence(length);

        for (int i = 0; i < length; i++) {
            if (i < numberCount) {
                pwdChars[randomPos[i]] = randomChar('0', 10);
            } else if (i < (numberCount + upperCount)) {
                pwdChars[randomPos[i]] = randomChar('A', 26);
            } else {
                pwdChars[randomPos[i]] = randomChar('a', 26);
            }
        }

        return String.valueOf(pwdChars);
    }

    /**
     * 由数字，大小写字母，特殊字符组成的字符串
     * @param length
     * @return
     */
    private static String veryStrongPassword(int length) {
        char[] pwdChars = new char[length];

        int numberCount = RANDOM.nextInt(length - 2) + 1;
        int upperCount = RANDOM.nextInt(length - numberCount - 1) + 1;
        int top = length - numberCount - upperCount;
        int specialCount = 0;
        if (top > 1) {
            specialCount = RANDOM.nextInt(length - numberCount - upperCount - 1) + 1;
            if (specialCount > 2) {
                specialCount = 2;
            }
        }
        int[] randomPos = getRandomSequence(length);

        for (int i = 0; i < length; i++) {
            if (i < numberCount) {
                pwdChars[randomPos[i]] = randomChar('0', 10);
            } else if (i < (numberCount + upperCount)) {
                pwdChars[randomPos[i]] = randomChar('A', 26);
            } else if (i < (numberCount + upperCount + specialCount)) {
                pwdChars[randomPos[i]] = randomSpecialChar();
            } else {
                pwdChars[randomPos[i]] = randomChar('a', 26);
            }
        }

        return String.valueOf(pwdChars);
    }

    /**
     * 生成随机字符 ('0', 10) => 0 - 9; ('a', 26) => a - z; ('A', 26) => A - Z
     * @param begin
     * @param count
     * @return
     */
    private static char randomChar(char begin, int count) {
        char result = (char) (begin + RANDOM.nextInt(count));

        // 排除 '1', 'l', 'I', o, O
        while ('1' == result || 'l' == result || 'I' == result || 'o' == result || 'O' == result) {
            result = (char) (begin + RANDOM.nextInt(count));
        }

        return result;
    }

    private static char randomSpecialChar() {
        int length = SPECIAL_CHARS.length;

        return SPECIAL_CHARS[RANDOM.nextInt(length)];
    }

    /**
     * 生成随机序列
     * @param length
     * @return [0 - length] 的乱序数组
     */
    private static int[] getRandomSequence(int length) {
        int[] input = new int[length];
        int[] output = new int[length];

        for (int i = 0; i < length; i++) {
            input[i] = i;
        }

        int end = length;
        for (int i = 0; i < length; i++) {
            int temp = RANDOM.nextInt(end);
            output[i] = input[temp];
            input[temp] = input[end - 1];
            end--;
        }

        return output;
    }

}
