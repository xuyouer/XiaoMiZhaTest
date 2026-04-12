package ltd.xiaomizha.xuyou.common.utils.convert;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 图片转换工具类
 */
@Slf4j
public class ImageConvertUtil {

    private ImageConvertUtil() {
    }

    /**
     * 将 Base64 编码的图片字符串解码为 BufferedImage 对象
     *
     * @param base64String Base64 编码的图片字符串, 支持两种格式:
     *                     1. 纯 Base64 编码, 如: "iVBORw0KGgoAAAANSUhEUg..."
     *                     2. Data URI 格式, 如: "data:image/png;base64,iVBORw0KGgo..."
     * @return 解码后的 BufferedImage 对象
     * @throws RuntimeException 如果解码失败
     */
    public static BufferedImage decodeBase64ToImage(String base64String) {
        if (base64String == null || base64String.isBlank()) {
            throw new IllegalArgumentException("Base64字符串不能为空");
        }

        try {
            String base64Data = base64String;

            // 移除 Data URI 前缀
            // data:image/png;base64, 或 data:image/jpg;base64, 等
            if (base64String.contains(",")) {
                base64Data = base64String.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            log.error("Base64解码图片失败", e);
            throw new RuntimeException("Base64解码图片失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 BufferedImage 对象编码为 Base64 字符串
     *
     * @param image      要编码的 BufferedImage 对象
     * @param formatName 图片格式名称, 如: "png"、"jpg"、"jpeg"、"gif"
     * @return Base64 编码的图片字符串, Data URI 格式: data:image/{format};base64,...
     * @throws RuntimeException 如果编码失败
     */
    public static String encodeImageToBase64(BufferedImage image, String formatName) {
        if (image == null) {
            throw new IllegalArgumentException("图片对象不能为空");
        }

        if (formatName == null || formatName.isBlank()) {
            formatName = "png";
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            // 返回 Data URI 格式
            return "data:image/" + formatName.toLowerCase() + ";base64," + base64;
        } catch (Exception e) {
            log.error("图片编码为Base64失败, 格式: {}", formatName, e);
            throw new RuntimeException("图片编码为Base64失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 BufferedImage 对象编码为 Base64 字符串, 默认PNG格式
     *
     * @param image 要编码的 BufferedImage 对象
     * @return Base64 编码的图片字符串, Data URI 格式
     */
    public static String encodeImageToBase64(BufferedImage image) {
        return encodeImageToBase64(image, "png");
    }

    /**
     * 将字节数组编码为 Base64 字符串, 纯Base64, 无前缀
     *
     * @param imageBytes 图片字节数组
     * @return 纯 Base64 编码字符串
     */
    public static String encodeBytesToBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            throw new IllegalArgumentException("字节数组不能为空");
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 将 Base64 字符串解码为字节数组
     *
     * @param base64String Base64 编码字符串, 支持纯Base64或Data URI格式
     * @return 解码后的字节数组
     */
    public static byte[] decodeBase64ToBytes(String base64String) {
        if (base64String == null || base64String.isBlank()) {
            throw new IllegalArgumentException("Base64字符串不能为空");
        }

        String base64Data = base64String;

        // 移除 Data URI 前缀
        if (base64String.contains(",")) {
            base64Data = base64String.split(",")[1];
        }

        return Base64.getDecoder().decode(base64Data);
    }

    /**
     * 检查字符串是否为有效的 Base64 图片编码
     *
     * @param base64String 待检查的字符串
     * @return true-有效, false-无效
     */
    public static boolean isValidBase64Image(String base64String) {
        if (base64String == null || base64String.isBlank()) {
            return false;
        }

        try {
            String base64Data = base64String;

            // 移除 Data URI 前缀
            if (base64String.contains(",")) {
                base64Data = base64String.split(",")[1];
            }

            // 尝试解码
            byte[] bytes = Base64.getDecoder().decode(base64Data);

            // 尝试读取为图片
            return ImageIO.read(new ByteArrayInputStream(bytes)) != null;
        } catch (Exception e) {
            return false;
        }
    }

}
