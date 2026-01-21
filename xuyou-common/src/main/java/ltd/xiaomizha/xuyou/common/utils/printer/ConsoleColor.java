package ltd.xiaomizha.xuyou.common.utils.printer;

/**
 * 控制台颜色工具类
 */
public class ConsoleColor {

    // ANSI 转义码
    public static final String RESET = "\033[0m";

    // 文本颜色
    public static final String BLACK = "\033[30m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String MAGENTA = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";

    // 背景颜色
    public static final String BLACK_BG = "\033[40m";
    public static final String RED_BG = "\033[41m";
    public static final String GREEN_BG = "\033[42m";
    public static final String YELLOW_BG = "\033[43m";
    public static final String BLUE_BG = "\033[44m";
    public static final String MAGENTA_BG = "\033[45m";
    public static final String CYAN_BG = "\033[46m";
    public static final String WHITE_BG = "\033[47m";

    // 文本样式
    public static final String BOLD = "\033[1m";
    public static final String DIM = "\033[2m";
    public static final String ITALIC = "\033[3m";
    public static final String UNDERLINE = "\033[4m";
    public static final String BLINK = "\033[5m";
    public static final String REVERSE = "\033[7m";
    public static final String HIDDEN = "\033[8m";
    public static final String REVERSED = "\033[7m";

    /**
     * 检测终端是否支持ANSI颜色
     */
    public static boolean supportsColor() {
        // 检查系统属性
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows: 检查是否支持ANSI（Windows 10+的终端支持）
            return System.getenv("ANSICON") != null ||
                    System.getenv("WT_SESSION") != null || // Windows Terminal
                    "true".equals(System.getProperty("jansi.passthrough")) ||
                    "true".equals(System.getProperty("spring.output.ansi.enabled"));
        }
        // Unix/Linux/Mac: 通常支持ANSI
        return true;
    }

    /**
     * 安全着色
     * <p>
     * 如果终端不支持颜色, 则返回原始文本
     */
    public static String safeColor(String text, String colorCode) {
        if (supportsColor()) {
            return colorCode + text + RESET;
        }
        return text;
    }

    /**
     * 创建带有颜色和样式的文本
     */
    public static String colorize(String text, String color, String style) {
        return color + style + text + RESET;
    }

    /**
     * 将文本着色为绿色
     */
    public static String green(String text) {
        return safeColor(text, GREEN);
    }

    /**
     * 将文本着色为红色
     */
    public static String red(String text) {
        return safeColor(text, RED);
    }

    /**
     * 将文本着色为黄色
     */
    public static String yellow(String text) {
        return safeColor(text, YELLOW);
    }

    /**
     * 将文本着色为蓝色
     */
    public static String blue(String text) {
        return safeColor(text, BLUE);
    }

    /**
     * 将文本着色为青色
     */
    public static String cyan(String text) {
        return safeColor(text, CYAN);
    }

    /**
     * 将文本着色为洋红色
     */
    public static String magenta(String text) {
        return safeColor(text, MAGENTA);
    }

    /**
     * 将文本加粗
     */
    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    /**
     * 将文本添加下划线
     */
    public static String underline(String text) {
        return UNDERLINE + text + RESET;
    }

    /**
     * 创建进度条
     */
    public static String progressBar(int current, int total, int width) {
        float percentage = (float) current / total;
        int filledWidth = (int) (percentage * width);

        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < width; i++) {
            if (i < filledWidth) {
                bar.append(safeColor("=", GREEN));
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ");
        bar.append(String.format("%3d%%", (int) (percentage * 100)));

        return bar.toString();
    }

    /**
     * 打印成功消息
     */
    public static void success(String message) {
        System.out.println(green("✓ " + message));
    }

    public static void successCircle(String message) {
        System.out.println(green("🟢 " + message));
    }

    /**
     * 打印错误消息
     */
    public static void error(String message) {
        System.out.println(red("✗ " + message));
    }

    /**
     * 打印警告消息
     */
    public static void warn(String message) {
        System.out.println(yellow("⚠ " + message));
    }

    /**
     * 打印信息消息
     */
    public static void info(String message) {
        System.out.println(cyan("ℹ " + message));
    }
}