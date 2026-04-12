package ltd.xiaomizha.xuyou.common.utils.code;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 机器码生成工具类
 */
public class MachineCodeUtil {

    /**
     * 生成 3 位机器码 (字母开头, 字母数字组合)
     * <p>
     * 规则:
     * - 第 1 位: 大写字母 (A-Z)
     * - 后 2 位: 字母或数字 (A-Z, 0-9)
     * <p>
     * 示例: A12, B3X, Z9K, M7P 等
     *
     * @return 3 位机器码字符串
     */
    public static String generate3DigitCode() {
        StringBuilder code = new StringBuilder(3);

        // 第1位: 大写字母 (A-Z)
        char firstChar = (char) ('A' + ThreadLocalRandom.current().nextInt(26));
        code.append(firstChar);

        // 后2位: 字母或数字 (A-Z, 0-9), 共36种可能
        for (int i = 0; i < 2; i++) {
            char c = generateAlphaOrDigit();
            code.append(c);
        }
        
        return code.toString();
    }

    /**
     * 生成指定长度的机器码 (字母开头, 字母数字组合)
     *
     * @param length 码长度 (必须 >= 2)
     * @return 指定长度的机器码字符串
     */
    public static String generateCode(int length) {
        if (length < 2) {
            throw new IllegalArgumentException("码长度不能小于2");
        }

        StringBuilder code = new StringBuilder(length);

        // 第1位: 大写字母 (A-Z)
        char firstChar = (char) ('A' + ThreadLocalRandom.current().nextInt(26));
        code.append(firstChar);

        // 后续位: 字母或数字 (A-Z, 0-9)
        for (int i = 1; i < length; i++) {
            char c = generateAlphaOrDigit();
            code.append(c);
        }

        return code.toString();
    }

    /**
     * 批量生成不重复的3位机器码
     *
     * @param count 需要生成的数量 (最大 36*36 = 1296 个)
     * @return 不重复的机器码列表
     */
    public static java.util.List<String> generateUniqueCodes(int count) {
        int maxPossible = 26 * 36 * 36; // A-Z + [A-Z0-9]² = 33696 种组合
        if (count > maxPossible) {
            throw new IllegalArgumentException("最多可生成 " + maxPossible + " 个不重复的3位机器码");
        }

        java.util.Set<String> codeSet = new java.util.HashSet<>();
        while (codeSet.size() < count) {
            String code = generate3DigitCode();
            codeSet.add(code);
        }

        return new java.util.ArrayList<>(codeSet);
    }

    /**
     * 校验机器码格式是否正确
     *
     * @param code 待校验的机器码
     * @return true-格式正确, false-格式错误
     */
    public static boolean validateCodeFormat(String code) {
        if (code == null || code.length() != 3) {
            return false;
        }

        // 第1位必须是大写字母
        char first = code.charAt(0);
        if (!Character.isUpperCase(first)) {
            return false;
        }

        // 后2位必须是字母或数字
        for (int i = 1; i < 3; i++) {
            char c = code.charAt(i);
            if (!isAlphaOrDigit(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 生成单个字母或数字字符
     *
     * @return 字母或数字字符
     */
    private static char generateAlphaOrDigit() {
        int random = ThreadLocalRandom.current().nextInt(36); // 0-35

        if (random < 26) {
            // 0-25: 大写字母 A-Z
            return (char) ('A' + random);
        } else {
            // 26-35: 数字 0-9
            return (char) ('0' + (random - 26));
        }
    }

    /**
     * 判断字符是否为字母或数字
     *
     * @param c 字符
     * @return true-是字母或数字, false-其他字符
     */
    private static boolean isAlphaOrDigit(char c) {
        return Character.isUpperCase(c) || Character.isDigit(c);
    }

}
