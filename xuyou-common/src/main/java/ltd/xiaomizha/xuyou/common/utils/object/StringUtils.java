package ltd.xiaomizha.xuyou.common.utils.object;

public class StringUtils {

    /**
     * 获取配置值
     *
     * @param values 可变参数
     * @return 第一个有效值
     */
    public static String getFirstValidValue(String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        // 返回第一个非null非空的值
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取指定位置的配置值
     *
     * @param index  索引位置
     * @param values 可变参数
     * @return 指定位置的值
     */
    public static String getValueAt(int index, String... values) {
        // 检查边界
        if (values == null || index < 0 || index >= values.length) {
            throw new IllegalArgumentException("无效的索引或参数");
        }
        return values[index];
    }

    /**
     * 查找包含特定关键字的配置值
     *
     * @param keyword 关键字
     * @param values  可变参数
     * @return 包含关键字的第一个值
     */
    public static String getValueByKeyword(String keyword, String... values) {
        if (values == null || keyword == null) {
            return null;
        }

        for (String value : values) {
            if (value != null && value.contains(keyword)) {
                return value;
            }
        }
        return null;
    }
}
