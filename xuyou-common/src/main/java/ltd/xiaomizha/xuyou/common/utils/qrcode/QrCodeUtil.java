package ltd.xiaomizha.xuyou.common.utils.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.utils.convert.ImageConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
@Slf4j
@Data
@Component
@ConditionalOnProperty(name = "scan.login.qrcode.enabled", havingValue = "true", matchIfMissing = true)
public class QrCodeUtil {

    @Value("${scan.login.qrcode.enabled:true}")
    private Boolean enabled;

    @Value("${scan.login.qrcode.width:200}")
    private Integer qrcodeWidth;

    @Value("${scan.login.qrcode.height:200}")
    private Integer qrcodeHeight;

    @Value("${scan.login.qrcode.expire-time:300}")
    private Integer expireTime;

    @Value("${scan.login.redis.key-prefix:scan_login:}")
    private String redisKeyPrefix;

    @Value("${scan.login.redis.state-key:scan_state:}")
    private String redisStateKey;

    @Value("${scan.login.qrcode.use-logo:true}")
    private Boolean useLogo;

    @Value("${scan.login.qrcode.logo-path:}")
    private String logoPath;

    @Value("${scan.login.qrcode.logo-ratio:0.2}")
    private Double logoRatio;

    @Value("${scan.login.callback.base-url:}")
    private String scanLoginCallbackBaseUrl;

    @Value("${scan.login.callback.confirm-path:/api/scan/login/confirm}")
    private String scanLoginConfirmPath;

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getQrcodeWidth() {
        return qrcodeWidth;
    }

    public Integer getQrcodeHeight() {
        return qrcodeHeight;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public String getRedisStateKey() {
        return redisStateKey;
    }

    public Boolean getUseLogo() {
        return useLogo;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public Double getLogoRatio() {
        return logoRatio;
    }

    public String getScanLoginCallbackBaseUrl() {
        return scanLoginCallbackBaseUrl;
    }

    public String getScanLoginConfirmPath() {
        return scanLoginConfirmPath;
    }

    /**
     * 生成二维码 Base64 字符串
     *
     * @param content 二维码内容, 存临时凭证 uuid
     * @return Base64 编码的二维码图片字符串, 前缀 data:image/png;base64,
     */
    public String generateQrCodeBase64(String content) {
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("二维码内容不能为空");
        }

        // 二维码配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 支持中文
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 30%容错率
        hints.put(EncodeHintType.MARGIN, 1); // 二维码边框空白宽度

        try {
            // 生成二维码矩阵
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, qrcodeWidth, qrcodeHeight, hints);

            // 转换为 BufferedImage
            BufferedImage image = new BufferedImage(qrcodeWidth, qrcodeHeight, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < qrcodeWidth; x++) {
                for (int y = 0; y < qrcodeHeight; y++) {
                    // 黑色二维码, 白色背景
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            // 转换为 Base64
            // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // ImageIO.write(image, "png", outputStream);
            // byte[] imageBytes = outputStream.toByteArray();
            //
            // return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

            return ImageConvertUtil.encodeImageToBase64(image, "png");
        } catch (Exception e) {
            log.error("二维码生成失败, 内容：{}", content, e);
            throw new RuntimeException("二维码生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成带 Logo 的二维码 Base64 字符串
     *
     * @param content   二维码内容
     * @param logoImage Logo 图片
     * @return Base64 编码的二维码图片字符串
     */
    public String generateQrCodeBase64WithLogo(String content, BufferedImage logoImage) throws Exception {
        String base64QrCode = generateQrCodeBase64(content);

        if (logoImage == null) {
            logoImage = loadDefaultLogo();
        }
        if (logoImage == null) {
            return base64QrCode;
        }

        // 将 Base64 字符串解码回 BufferedImage
        BufferedImage qrCodeImage = ImageConvertUtil.decodeBase64ToImage(base64QrCode);

        // 在二维码中心嵌入 Logo
        BufferedImage imageWithLogo = embedLogo(qrCodeImage, logoImage);

        // 转换回 Base64
        return ImageConvertUtil.encodeImageToBase64(imageWithLogo, "png");
    }

    /**
     * 生成带默认 Logo 的二维码, 从配置文件加载Logo
     *
     * @param content 二维码内容
     * @return Base64 编码的带 Logo 二维码图片字符串
     */
    public String generateQrCodeWithDefaultLogo(String content) {
        try {
            return generateQrCodeBase64WithLogo(content, null);
        } catch (Exception e) {
            log.warn("生成带Logo的二维码失败, 返回纯二维码: {}", e.getMessage());
            return generateQrCodeBase64(content);
        }
    }

    /**
     * 根据配置决定生成哪种类型的二维码
     * <p>
     * 如果配置了启用 Logo 且有 Logo 图片路径, 则生成带 Logo 的二维码,
     * 否则生成纯二维码
     *
     * @param content 二维码内容
     * @return Base64 编码的二维码图片字符串
     */
    public String generateQrCodeByConfig(String content) {
        boolean useLogo = isUseLogo();

        if (useLogo) {
            log.debug("使用带Logo的二维码生成方式");
            return generateQrCodeWithDefaultLogo(content);
        } else {
            log.debug("使用纯二维码生成方式");
            return generateQrCodeBase64(content);
        }
    }

    /**
     * 判断是否应该使用带 Logo 的二维码
     *
     * @return true: 使用带Logo的二维码, false: 使用纯二维码
     */
    public boolean isUseLogo() {
        // 优先使用显式配置的 use-logo 选项
        if (useLogo != null) {
            return useLogo && StringUtils.isNotBlank(logoPath);
        }
        // 如果没有显式配置, 则根据是否有 Logo 路径自动判断
        return StringUtils.isNotBlank(logoPath);
    }

    /**
     * 从 classpath 加载默认 Logo 图片
     *
     * @return Logo 图片对象, 如果不存在则返回 null
     */
    private BufferedImage loadDefaultLogo() {
        if (StringUtils.isBlank(logoPath)) {
            return null;
        }

        try {
            ClassPathResource resource = new ClassPathResource(logoPath);
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    return ImageIO.read(inputStream);
                }
            }
        } catch (Exception e) {
            log.debug("加载默认Logo图片失败: path={}, error={}", logoPath, e.getMessage());
        }

        return null;
    }

    /**
     * 在二维码图片中心嵌入 Logo
     *
     * @param qrCodeImage 二维码图片
     * @param logoImage   Logo 图片
     * @return 嵌入 Logo 后的图片
     */
    private BufferedImage embedLogo(BufferedImage qrCodeImage, BufferedImage logoImage) {
        int width = qrCodeImage.getWidth();
        int height = qrCodeImage.getHeight();

        // 计算 Logo 大小, 占二维码的比例
        // Math.max(0.1, Math.min(0.3, logoRatio))
        int logoWidth = (int) (width * Math.clamp(logoRatio, 0.1, 0.3));
        int logoHeight = (int) (height * Math.clamp(logoRatio, 0.1, 0.3));

        // 缩放 Logo 到目标大小
        Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

        // 创建新图片, 类型为 ARGB, 支持透明度
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) combinedImage.getGraphics();

        // 绘制原二维码
        g2d.drawImage(qrCodeImage, 0, 0, null);

        // 计算居中位置
        int x = (width - logoWidth) / 2;
        int y = (height - logoHeight) / 2;

        // 绘制白色圆角矩形背景
        int padding = 2; // 内边距
        int bgWidth = logoWidth + padding * 2;
        int bgHeight = logoHeight + padding * 2;
        int bgX = x - padding;
        int bgY = y - padding;

        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fill(new RoundRectangle2D.Float(bgX, bgY, bgWidth, bgHeight, 8, 8));

        // 绘制 Logo
        g2d.drawImage(scaledLogo, x, y, null);

        g2d.dispose();

        return combinedImage;
    }

}
