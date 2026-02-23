package ltd.xiaomizha.xuyou.common.utils.license;


import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 许可证工具类
 * <p>
 * 用于处理许可证相关的工具方法
 */
public class LicenseUtils {

    /**
     * 格式化许可证的remarks字段
     *
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param licenseTypeStr 许可证类型字符串
     * @return 格式化后的remarks字段
     */
    public static String formatLicenseRemarks(LocalDateTime startTime, LocalDateTime endTime, String licenseTypeStr) {
        // 计算有效期天数
        long days = Duration.between(startTime, endTime).toDays();

        // 获取许可证类型中文名称
        LicenseType licenseType = LicenseType.getByValue(licenseTypeStr);
        String licenseTypeChinese = licenseType != null ? licenseType.getDescription() : LicenseType.TRIAL.getDescription();

        // 构建remarks字段
        if (days >= 365) {
            int years = (int) (days / 365);
            return years + "年期" + licenseTypeChinese + "许可证";
        } else {
            return days + "天" + licenseTypeChinese + "许可证";
        }
    }

}
